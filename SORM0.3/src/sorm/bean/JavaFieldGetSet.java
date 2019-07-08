package sorm.bean;
/**
 * 封装了java属性和get，set方法的源代码
 * @author yang
 *ps:也就是根据数据库信息生成对应的javabean源码
 *想封装什么数据，就定义什么javabean
 */
public class JavaFieldGetSet {
	/**
	 * 属性的源码信息，如private int userId;
	 */
	private String fieldInfo;
	/**
	 * get方法的源码信息，如public int getUserId();
	 */
	private String getInfo;
	/**
	 * set方法的源码信息，如public int setUserId();
	 */
	private String setInfo;
	
	@Override
	public String toString() {//便于调试
		System.out.println(fieldInfo);
		System.out.println(getInfo);
		System.out.println(setInfo);
		return super.toString();
	}
	public JavaFieldGetSet() {
	}
	
	public JavaFieldGetSet(String fieldInfo, String getInfo, String setInfo) {
		super();
		this.fieldInfo = fieldInfo;
		this.getInfo = getInfo;
		this.setInfo = setInfo;
	}

	public String getFieldInfo() {
		return fieldInfo;
	}

	public void setFieldInfo(String fieldInfo) {
		this.fieldInfo = fieldInfo;
	}

	public String getGetInfo() {
		return getInfo;
	}

	public void setGetInfo(String getInfo) {
		this.getInfo = getInfo;
	}

	public String getSetInfo() {
		return setInfo;
	}

	public void setSetInfo(String setInfo) {
		this.setInfo = setInfo;
	}
	
}
