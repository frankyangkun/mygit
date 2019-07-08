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
 * 负责针对Mysql数据库的查询
 * @author yang
 *增删改都是对象--库
 *查询是从库--对象
 */
public class MySqlQuery extends Query{
	public static void main(String[] args) {
//		Emp e = new Emp();
		//测试delete
//		e.setId(4);//如果mysql中设置了id为自增，这里可以不用设置id，否则执行两次就会报主键重复
//		new MySqlQuery().delete(e);//先根据DB表生成了Emp类，然后通过传一个Emp对象来删除它对应的DB中的数据，删除id=2的
		
		//测试insert
//		e.setEmpname("lili");
//		e.setBirthday(new java.sql.Date(System.currentTimeMillis()));//更改表中字段birthday的值
//		e.setAge(30);
//		e.setSalary(3000.0);
//		new MySqlQuery().insert(e);//实现了直接把对象存到数据库
		
		//测试update
//		e.setEmpname("newone21");
//		e.setId(10);//有同名的，所以需要指定id，指定id后可以改名
//		e.setAge(31);
//		e.setSalary(13000.0);
//		new MySqlQuery().update(e, new String[]{"empname","age","salary"});
		
		//测试queryRows查询多行
//		List<Emp> list = new MySqlQuery().queryRows("select id,empname,age from emp where age>? and salary<?", 
//				Emp.class, new Object[]{22,5000});
//		for (Emp emp : list) {  
//			System.out.println(emp.getEmpname());
//		}
		
		//测试复杂语句查询 专门封装一个VO类javabean（值对象）用来存储sql字段的值
//		String sql2 = "select e.empname,e.salary+e.bonus 'xinshui',d.dname 'deptname', d.address 'deptaddr'"
//					+"from emp e join dept d on e.deptId = d.id";
//		List<EmpVO> list2 = new MySqlQuery().queryRows(sql2, EmpVO.class, null);//参数是空，不需要传
//		for (EmpVO emp : list2) {
//			System.out.println(emp.getEmpname()+"--"+emp.getDeptaddr()+"--"+emp.getXinshui());
//		}
		
		//测试返回结果为Number的
//		Object obj = new MySqlQuery().queryValue("select count(*) from emp where salary>?",new Object[]{1000});
//		Number obj2 = new MySqlQuery().queryNumber("select count(*) from emp where salary>?",new Object[]{1000});
//		System.out.println((Number)obj);
//		System.out.println(obj2);
		//因为Number包含了所有数字类型，所有只要需要，可以把返回值转成任意类型,比如obj2.intValue() obj2.DoubleValue()
		
		//通过工厂类获得query对象 前提需要在配置文件配置query对象
//		Query q = QueryFactory.createQuery();
//		String sql2 = "select e.empname,e.salary+e.bonus 'xinshui',d.dname 'deptname', d.address 'deptaddr'"
//				+"from emp e join dept d on e.deptId = d.id";
//		List<EmpVO> list2 = q.queryRows(sql2, EmpVO.class, null);//参数是空，不需要传
//		for (EmpVO emp : list2) {
//			System.out.println(emp.getEmpname()+"--"+emp.getDeptaddr()+"--"+emp.getXinshui());
//		}
	}
	@Override
	public Object queryPagenate(int pageNum, int size) {
		// TODO Auto-generated method stub
		return null;
	}
}
