package sorm.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 封装了JDBC常用的查询操作
 * @author yang
 *
 */
public class JDBCUtils {
	/**
	 * 给sql传参
	 * @param ps 预编译sql语句对象
	 * @param params 参数
	 */
	public static void handleParams(PreparedStatement ps,Object[] params){
		if(params!=null){
			for (int i = 0; i < params.length; i++) {
				try {
					ps.setObject(1+i, params[i]);//表示占位符代表的参数，key从1开始，value是数组，因为参数可能是数组
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
