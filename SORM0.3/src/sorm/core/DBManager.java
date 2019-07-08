package sorm.core;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import sorm.bean.Configuration;
import sorm.pool.DBConnPool;

/**
 * 根据配置信息，维持连接对象的管理（增加连接池功能）
 * @author yang
 */
public class DBManager {
	/**
	 * 配置信息
	 */
	private static Configuration conf;
	/**
	 * 连接池对象
	 */
	private static DBConnPool pool;
	static {//目前还是用资源文件，如果用xml，需要修改代码
		Properties pros = new Properties();//读取和配置信息
		try {
			pros.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		conf = new Configuration();//这句忘了会报空指针错误，因为下面的操作都来自于Configuration类
		conf.setDriver(pros.getProperty("driver"));
		conf.setPoPackage(pros.getProperty("poPackage"));
		conf.setPwd(pros.getProperty("pwd"));
		conf.setSrcPath(pros.getProperty("srcPath"));
		conf.setUrl(pros.getProperty("url"));
		conf.setUser(pros.getProperty("user"));
		conf.setUsingDB(pros.getProperty("usingDB"));
		conf.setQueryClass(pros.getProperty("queryClass"));
		conf.setPoolMinSize(Integer.parseInt(pros.getProperty("poolMinSize")));//pros.getProperty("poolMinSize")返回的是string，定义的是数字，强转一下
		conf.setPoolMinSize(Integer.parseInt(pros.getProperty("poolMaxSize")));//Integer.parseInt()是把()里的内容换成整数
		
		//加载TableContext 不加载的话类信息和表结构信息进不来
		System.out.println(TableContext.class);
	}
	/**
	 * 获得Connection对象
	 * @return
	 */
	public static Connection getConn(){//获得Mysql连接
//		try {
////			Class.forName(pros.getProperty("mysqlDriver"));
//			Class.forName(conf.getDriver());//使用配置对象，已经封装好了，下面同理
////			return DriverManager.getConnection(pros.getProperty("mysqlURL"),pros.getProperty("mysqlUser"),pros.getProperty("mysqlPwd"));
//			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());//目前直接建立连接，后期增加连接池处理，提高效率！
//		} catch (Exception e) {//由于更新的数据库版本，要在url后面加上&useSSL=false，否则会报Establishing SSL connection警告
//			e.printStackTrace();
//			return null;//注意是放在catch块里面，遇到异常返回空
//		}
		if(pool==null){
			pool = new DBConnPool();
		}
		return pool.getConnection();//直接从连接池获取连接对象
	}
	/**
	 * 创建新的Connection对象
	 * @return
	 */
	public static Connection createConn(){
		try {
//			Class.forName(pros.getProperty("mysqlDriver"));
			Class.forName(conf.getDriver());//使用配置对象，已经封装好了，下面同理
//			return DriverManager.getConnection(pros.getProperty("mysqlURL"),pros.getProperty("mysqlUser"),pros.getProperty("mysqlPwd"));
			return DriverManager.getConnection(conf.getUrl(),conf.getUser(),conf.getPwd());//目前直接建立连接，后期增加连接池处理，提高效率！
		} catch (Exception e) {//由于更新的数据库版本，要在url后面加上&useSSL=false，否则会报Establishing SSL connection警告
			e.printStackTrace();
			return null;//注意是放在catch块里面，遇到异常返回空
		}
	}
	/**
	 * 关闭传入的ResultSet，Statement，Connetion对象
	 * @param rs
	 * @param st
	 * @param conn
	 */
	public static void close(ResultSet rs,Statement st,Connection conn){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(st!=null){
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
//		if(conn!=null){
//			try {
//				conn.close();
				pool.close(conn);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
	}
	/**
	 * 关闭传入的Statement，Connetion对象
	 * @param st
	 * @param conn
	 */
	public static void close(Statement st,Connection conn){
		if(st!=null){
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
//		if(conn!=null){
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		pool.close(conn);
	}
	/**
	 * 关闭传入的Connetion对象
	 * @param conn
	 */
	public static void close(Connection conn){
//		if(conn!=null){
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		pool.close(conn);
	}
	/**
	 * 返回Configuration对象
	 * @return
	 */
	public static Configuration getConf(){
		return conf;
	}
}
