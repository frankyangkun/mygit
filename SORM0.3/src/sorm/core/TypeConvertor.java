package sorm.core;

public interface TypeConvertor {
	/**
	 * 将数据库数据类型转换成java的数据类型
	 * @param columnType 数据库字段的数据类型
	 * @return java的数据类型
	 */
	public String databaseType2JavaType(String columnType);
	/**
	 * 将java数据类型转换成数据库的数据类型
	 * @param columnType 数据库字段的数据类型
	 * @return java的数据类型
	 */
	public String javaType2DatabaseType(String columnType);
}
