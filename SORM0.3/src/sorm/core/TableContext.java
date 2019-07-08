package sorm.core;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sorm.bean.ColumnInfo;
import sorm.bean.TableInfo;
import sorm.utils.JavaFileUtils;
import sorm.utils.StringUtils;

/**
 * 负责获取管理数据库所有表结构和类结构的关系，并可以根据表结构生成类结构。
 * @author gaoqi www.sxt.cn
 *ps:就是把数据库里的表信息读取出来，放到了类里面
 */
public class TableContext {
	/**
	 * 表名为key，表信息对象为value
	 */
	public static  Map<String,TableInfo>  tables = new HashMap<String,TableInfo>();
	
	/**
	 * 将po的class对象和表信息对象关联起来，便于重用！
	 */
	public static  Map<Class,TableInfo>  poClassTableMap = new HashMap<Class,TableInfo>();
	
	private TableContext(){}
	
	static {
		try {
			//初始化获得表的信息
			Connection con = DBManager.getConn();
			DatabaseMetaData dbmd = con.getMetaData(); 
			System.out.println(dbmd.getURL());/////////jdbc:mysql://localhost:3306/sorm
			System.out.println(dbmd.getUserName());/////////root@localhost
			ResultSet tableRet = dbmd.getTables(null, "%","%",new String[]{"TABLE"}); 
			System.out.println(tableRet);///////
			while(tableRet.next()){
				String tableName = (String) tableRet.getObject("TABLE_NAME");
				System.out.println(tableName);/////////dept
				TableInfo ti = new TableInfo(tableName, new ArrayList<ColumnInfo>(),new HashMap<String, ColumnInfo>());
				System.out.println(ti.getTname()+"***eee***");///////null***eee*** 结果是因为TableInfo类中这个构造器没写完，this.xx=xx全都没写
				tables.put(tableName, ti);
				System.out.println(tables.keySet());///////[dept]
				System.out.println(ti.getColumns().toString());//报空指针，说明引用为空，却调用了方法,添加51行测试代码，果然为null
				for (TableInfo temp : tables.values()) {
					System.out.println(temp+"===xxx");///////
				}
				ResultSet set = dbmd.getColumns(null, "%", tableName, "%");  //查询表中的所有字段
				while(set.next()){
					ColumnInfo ci = new ColumnInfo(set.getString("COLUMN_NAME"), set.getString("TYPE_NAME"), 0);
					ti.getColumns().put(set.getString("COLUMN_NAME"), ci);
				}
				ResultSet set2 = dbmd.getPrimaryKeys(null, "%", tableName);  //查询t_user表中的主键
				while(set2.next()){
					ColumnInfo ci2 = (ColumnInfo) ti.getColumns().get(set2.getObject("COLUMN_NAME"));
					ci2.setKeyType(1);  //设置为主键类型
					ti.getPriKeys().add(ci2);
				}
				
				if(ti.getPriKeys().size()>0){  //取唯一主键。。方便使用。如果是联合主键。则为空！
					ti.setOnlyPriKey(ti.getPriKeys().get(0));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
//		更新类结构，每次启动框架，都把类结构更新一遍
		updateJavaPOFile();
		
		//加载po包下面所有的类，便于重用，提高效率！不需要每次反复去对应class对象和table
		loadPOTables();
	}
	
	/**
	 * 根据表结构，更新配置的po包下面的java类
	 * 实现了从表结构转化到类结构
	 */
	public static void updateJavaPOFile(){
		Map<String,TableInfo> map = TableContext.tables;
		for(TableInfo t:map.values()){
			JavaFileUtils.createJavaPOFile(t,new MySqlTypeConvertor());
		}	
	}
	/**
	 * 加载po包下面的类
	 */
	public static void loadPOTables(){
		//思路 通过反射将类的class对象和tableinfo关联起来,key是class对象，value是表信息对象
//		Class c = Class.forName("sorm.test.po.Emp");
//		poClassTableMap.put(c, tableinfo);
		//实现
		for(TableInfo tableInfo:tables.values()){
			try {
				Class c = Class.forName(DBManager.getConf().getPoPackage()+"."+StringUtils.firstChar2UpperCase(tableInfo.getTname()));
				poClassTableMap.put(c, tableInfo);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
		 Map<String,TableInfo>  tables = TableContext.tables;
		 System.out.println(tables);
	}
}
