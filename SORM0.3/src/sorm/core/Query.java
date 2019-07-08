package sorm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JDBCUtils;
import sorm.utils.ReflectUtils;

/**
 * 负责查询（对外提供服务的核心类）
 * @author yang
 */
@SuppressWarnings("all")
public abstract class Query implements Cloneable{
	/**
	 * 采用模板方法模式将jdbc操作封装成模板，便于重用
	 * @param sql sql语句
	 * @param params sql的参数
	 * @param clazz 记录要封装到的java类 bean
	 * @param back CallBack的实现类，实现回调
	 * @return
	 */
	public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack back){
		// 和executeDML方法套路差不多 外部传进sql语句，参数为params，每行记录封装到clazz对象
		Connection conn = DBManager.getConn();  
//		List list = null;//存放查询结果的容器  直接return，不用单独定义了
		PreparedStatement ps = null;
		ResultSet rs = null;//存放结果集，存放多行结果！！
		try {
			ps = conn.prepareStatement(sql);
			//给sql设置参数，比较常用，也封装成方法JDBCUtils.handleParams
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);//delete from emp where id=2
			rs = ps.executeQuery();//execute()返回的是boolean，executeQuery()返回的是结果集
			
			return back.doExecute(conn, ps, rs);//相当于占个位置，调用时具体去实现接口的doExecute方法 
		} catch (Exception e) {
			e.printStackTrace();
			return null;//出现异常，返回空
		}finally {
			DBManager.close(conn);
		}
	}
	
	/**
	 * 直接执行一个DML语句
	 * @param sql sql语句
	 * @param params 参数
	 * @return 执行sql语句后影响了记录的行数
	 */
	public int executeDML(String sql,Object[] params){
		Connection conn = DBManager.getConn();
		int count = 0;
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			//给sql设置参数，比较常用，也封装成方法JDBCUtils.handleParams
//			if(params!=null){
//				for (int i = 0; i < params.length; i++) {
//					ps.setObject(1+i, params[i]);//表示占位符代表的参数，key从1开始
//				}
//			}
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);//delete from emp where id=2
			count = ps.executeUpdate();//返回执行结果数量
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBManager.close(conn);
		}
		return count;
	}
	
	/**
	 * 将一个对象存储到数据库中
	 * 把对象中不为null的属性存入数据库，如果数字为null，则存0
	 * @param obj 要存储的对象
	 */
	public void insert(Object obj){
		//obj-->表中。insert into 表名(id,uname,pwd) values(?,?,?)
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();//存储sql的参数对象
		TableInfo tableinfo = TableContext.poClassTableMap.get(c);//获得对应的表信息，c是key，表信息是value
		StringBuilder sql = new StringBuilder("insert into "+tableinfo.getTname()+" (");
		int countNotNullField = 0;//计算不为null的属性的数量
		Field[] fs = c.getDeclaredFields();
		for (Field f : fs) {
			String fieldName = f.getName();
			Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);//获得值
			if(fieldValue!=null){
				countNotNullField++;
				sql.append(fieldName+",");
				params.add(fieldValue);
			}
		}
		sql.setCharAt(sql.length()-1, ')');//把最后一个value值生成的,换成）
		sql.append(" values(");
		for (int i = 0; i < countNotNullField; i++) {
			sql.append("?,");
		}
		sql.setCharAt(sql.length()-1, ')');//把最后一个value值生成的,换成）
		executeDML(sql.toString(), params.toArray());//sql是StringBuilder，要转成String，params是List，用toArray转成Object数组
	}
	
	/**
	 * 删除clazz表示类对应的表中的记录（指定主键值id的记录）
	 * @param clazz 跟表对应的类的class对象
	 * @param id 主键值
	 */
	public void delete(Class clazz,Object id){//根据Class对象删除,即传一个类名，就可删除DB中的表
		//Emp.class,2-->delete from emp where id=2
		TableInfo tableinfo = TableContext.poClassTableMap.get(clazz);//通过class对象找table
		//获得主键
		ColumnInfo onlyPriKey = tableinfo.getOnlyPriKey();
		String sql = "delete from "+tableinfo.getTname()+" where "+onlyPriKey.getName()+"=?";
		executeDML(sql, new Object[]{id});
	}
	
	/**
	 * 删除对象在数据库中对应的记录（对象所在的类对应到表，对象的主键的值对应到记录）
	 * @param obj
	 */
	public void delete(Object obj){//根据传进来的类对象直接删除表信息
		Class c = obj.getClass();
		TableInfo tableinfo = TableContext.poClassTableMap.get(c);//通过key(class对象)找value(table)
		ColumnInfo onlyPriKey = tableinfo.getOnlyPriKey();//获得主键
		//通过反射机制，调用属性对应的get或set方法 很常用 所以封装成方法ReflectUtils.invokeGet
//		try {
//			Method m = c.getMethod("get"+StringUtils.firstChar2UpperCase(onlyPriKey.getName()), null);//参数2是参数类型对应的Class对象，因为可能有重载的方法，只传名字可能无法区分,get方法一般是null
//			Object priKeyValue = m.invoke(obj, null);//invoke是调用的意思，参数是null，set方法才会使用参数
//			delete(c,priKeyValue);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);
		delete(c,priKeyValue);
	}
	
	/**
	 * 更新对象对应的记录，并只更新指定字段的值
	 * @param obj 所要更新的对象
	 * @param fieldNames 更新的属性列表
	 * @return 执行sql语句后影响了记录的行数
	 */
	public int update(Object obj,String[] fieldNames){//update user set uname=?,pwd=?
		//传个属性列表进来，一次性修改，比如obj{"uname","pwd"}-->update 表名 set uname=?,pwd=? where id=?
		//套路差不多，先通过obj对象获得对应的class对象，从而获得表名
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();//存储sql的参数对象
		TableInfo tableinfo = TableContext.poClassTableMap.get(c);//获得对应的表信息，c是key，表信息是value
		ColumnInfo priKey = tableinfo.getOnlyPriKey();
		StringBuilder sql = new StringBuilder("update "+tableinfo.getTname()+" set ");
		for (String fname : fieldNames) {//循环属性列表
			Object fvalue = ReflectUtils.invokeGet(fname, obj);//通过属性名（比如uname）获得属性值（比如tom）
			params.add(fvalue);
			sql.append(fname+"=?,");
		}
		sql.setCharAt(sql.length()-1, ' ');//把最后一个value值生成的,换成空格。[uname=?,变成uname=? ]
		sql.append(" where ");
		sql.append(priKey.getName()+"=? ");
		
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));
		return executeDML(sql.toString(), params.toArray());
	}
	
	/**
	 * 查询返回多行记录，并将每行记录封装到clazz指定的类的对象中
	 * @param sql sql查询语句
	 * @param clazz 封装数据的javabean类的Class对象
	 * @param params sql参数
	 * @return 查询到的结果
	 */
	public List queryRows(final String sql,final Class clazz,final Object[] params){
		// 和executeDML方法套路差不多 外部传进sql语句，参数为params，每行记录封装到clazz对象
		return (List)executeQueryTemplate(sql, params, clazz, new CallBack(){//执行模板，实现CallBack的方法！！！！ 直接return它
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;//存放查询结果的容器
				try {
					ResultSetMetaData metaData = rs.getMetaData();//存放每行结果的多列结果！！
					//多行
					while (rs.next()) {
						if(list==null){
							list = new ArrayList();
						}
						Object rowObj = clazz.newInstance();//调用javabean的无参构造器，至于是哪个bean，这里调用时传入的Emp.class，是emp的bean
						//多列
						for (int i = 0; i < metaData.getColumnCount(); i++) {
							String columnName = metaData.getColumnLabel(i + 1);//获得列标签，不要用getColumnName，因为列可能会有别名，参数是列索引，jdbc中索引都要+1，都是从1开始
							Object columnValue = rs.getObject(i + 1);//每列的值
							//调用rowObj对象的setUsername(String uname)方法，将columnValue的值设置进去,封装成方法便于重用
							//Method m = clazz.getDeclaredMethod("set"+StringUtils.firstChar2UpperCase(columnName),columnValue.getClass());
							//m.invoke(rowObj, columnValue);
							ReflectUtils.invokeSet(rowObj, columnName, columnValue);
						}
						list.add(rowObj);
					} 
				} catch (Exception e) {
				}
				return list;
			}
		});
	}
	
	/**
	 * 查询返回1行记录，并将每行记录封装到clazz指定的类的对象中
	 * @param sql sql查询语句
	 * @param clazz 封装数据的javabean类的Class对象
	 * @param params sql参数
	 * @return 查询到的结果
	 */
	public Object queryUniqueRow(String sql,Class clazz,Object[] params){//查询结果是一行多列
		List list = queryRows(sql, clazz, params);
		return (list==null||list.size()>0)?null:list.get(0);//如果list不为空并且list里有东西才返回，取出第一个元素，因为只有1个元素
	}//注意是||不是&&,否则报空指针错误
	/**
	 * 根据主键值直接查找对应的对象
	 * @param clazz 封装数据的javabean类的Class对象
	 * @param id 主键值
	 * @return 查询到的对象
	 */
	public Object queryById(Class clazz,Object id){
		//select * from emp where id =?
		TableInfo tableinfo = TableContext.poClassTableMap.get(clazz);//通过class对象找table
		//获得主键
		ColumnInfo onlyPriKey = tableinfo.getOnlyPriKey();
		String sql = "select * from "+tableinfo.getTname()+" where "+onlyPriKey.getName()+"=?";
		return queryUniqueRow(sql, clazz, new Object[]{id});//根据id查询只会有1个结果，所以用queryUniqueRow方法
	}
	/**
	 * 查询返回1个值（一行一列），并将每行记录封装到clazz指定的类的对象中
	 * @param sql sql查询语句 
	 * @param params sql参数
	 * @return 查询到的结果
	 */
	public Object queryValue(String sql,Object[] params){//查询结果是一行一列，逻辑和queryRows差不多
		return executeQueryTemplate(sql, params, null, new CallBack() {
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object value = null;
				try {
					while(rs.next()){
						//select count(*) from user 结果是一行一列
						value = rs.getObject(1);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return value;
			}
		});
	}
	
	/**
	 * 查询返回1个数字（一行一列），并将该值返回
	 * @param sql 查询语句
	 * @param params sql参数
	 * @return 查询到的数字
	 */
	public Number queryNumber(String sql,Object[] params){//查询结果只是数字，如果有查询结果只是Date或String，也可多加几个方法
		return (Number)queryValue(sql,params);//因为Number包含了所有数字类型，所有只要需要，可以把返回值转成任意类型
	}
	
	/**
	 * 分页查询
	 * @param pageNum 第几页数据
	 * @param size 每页显示多少记录
	 * @return
	 */
	public abstract Object queryPagenate(int pageNum,int size);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();//克隆模式
	}
}
