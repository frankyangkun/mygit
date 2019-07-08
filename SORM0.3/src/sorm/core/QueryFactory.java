package sorm.core;
/**
 * 创建Query对象的工厂类，返回query对象
 * @author yang
 */
public class QueryFactory {//工厂类本身设置成单例模式，只能有一个
//	private static QueryFactory factory = new QueryFactory();//createQuery()换成static就不用这个了
//	private static Class c;
	private static Query prototypeObj;//原型对象
	
	static{//加载指定的query类,DBManager.getConf().getQueryClass()
		//在QueryFactory类加载时就加载配置文件中的queryClass，初始化一次，获得是个String,反射获取Class对象
		try {
//			c = Class.forName(DBManager.getConf().getQueryClass());
			Class c = Class.forName(DBManager.getConf().getQueryClass());
			prototypeObj = (Query) c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//加载po包下面所有的类，便于重用，提高效率！
		TableContext.loadPOTables();
	}
	private QueryFactory(){//构造器私有
	}
	
	
//	public Query createQuery(){//反射newInstance可能会有效率问题,可考虑克隆模式
//		try {
//			return (Query) c.newInstance();
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;//必须有这句
//		}
//	}
	public static Query createQuery(){//通过克隆模式
		try {
			return (Query) prototypeObj.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
