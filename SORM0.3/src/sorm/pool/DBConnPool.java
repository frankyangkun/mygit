package sorm.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import sorm.core.DBManager;
/**
 * 连接池的类
 * @author yang
 */
public class DBConnPool {
	/**
	 * 连接池对象
	 */
	private List<Connection> pool;
	/**
	 * 最大连接数
	 */
	private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize();
	/**
	 * 最小连接数
	 */
	private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();
	/**
	 * 初始化连接池，使池中的连接数达到最小值
	 */
	public void initPool(){
		if(pool == null){
			pool = new ArrayList<Connection>();
		}
		while(pool.size()<DBConnPool.POOL_MIN_SIZE){
			pool.add(DBManager.createConn());//把获得的连接加到pool容器
			System.out.println("初始化连接池数："+pool.size());
		}
	}
	/**
	 * 从连接池中取出一个连接，通常是池中最后一个
	 * @return
	 */
	public synchronized Connection getConnection(){//可能多线程多人同时取到，加上同步
		int last_index = pool.size()-1;
		Connection conn = pool.get(last_index);//获得索引为最后的一个连接，pool是Connection类型的list，这里只获取了1个，所以类型是Connection
		pool.remove(last_index);//传入的是索引，如果不删掉，别人也可能用到同样的连接，就有问题
		return conn;
	}
	/**
	 * 将连接放回池中，并非真正关闭连接
	 * @param conn
	 */
	public synchronized void close(Connection conn){
		if(pool.size()>=POOL_MAX_SIZE){
			try {
				if(conn!=null){
					conn.close();//如果大于连接池限制，就真正关闭
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else{
			pool.add(conn);//将连接放回池中
		}
	}
	public DBConnPool() {
		initPool();//通过构造器初始化
	}
}