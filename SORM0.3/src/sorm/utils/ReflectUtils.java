package sorm.utils;

import java.lang.reflect.Method;

/**
 * 封装了反射常用的操作（封装框架不可能不用反射）
 * @author yang
 */
public class ReflectUtils {
	/**
	 * 调用obj对象对应属性fieldName的get方法
	 * @param fieldName 属性名称
	 * @param obj 对象名称
	 * @return 获取到的结果对象
	 */
	public static Object invokeGet(String fieldName,Object obj){
		try {
			Class c = obj.getClass();
			Method m = c.getMethod("get"+StringUtils.firstChar2UpperCase(fieldName), null);//参数2是参数类型对应的Class对象，因为可能有重载的方法，只传名字可能无法区分,get方法一般是null
//			Object priKeyValue = m.invoke(obj, null);//invoke是调用的意思，参数是null，set方法才会使用参数
			return m.invoke(obj, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 调用obj对象对应属性fieldName的set方法
	 * @param columnName 列名称
	 * @param obj 对象名称
	 * @param columnValue 列值
	 */
	public static void invokeSet(Object obj,String columnName,Object columnValue){
		try {//因为是set方法，所以getDeclaredMethod第二个参数要传参数类型对应的Class对象
			if(columnValue!=null){//由于别处传过来的columnValue可能为空，因为DB中列值可能为空，所以判断为空时就不调用set方法了
				Method m = obj.getClass().getDeclaredMethod("set"+StringUtils.firstChar2UpperCase(columnName),columnValue.getClass());
				m.invoke(obj, columnValue);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
