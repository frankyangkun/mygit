package sorm.utils;
/**
 * 封装了字符串常用的操作
 * @author yang
 *
 */
public class StringUtils {
	/**
	 * 将目标字符串首字母变为大写
	 * @param str 目标字符串
	 * @return 首字母变为大写的字符串
	 */
	public static String firstChar2UpperCase(String str){
		//abcd-->Abcd
		//abcd-->ABCD-->取出A再把原字符串后面的加上即可
		return str.toUpperCase().substring(0,1)+str.substring(1);
	}
}