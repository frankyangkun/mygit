package sorm.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sorm.bean.ColumnInfo;
import sorm.bean.JavaFieldGetSet;
import sorm.bean.TableInfo;
import sorm.core.DBManager;
import sorm.core.MySqlTypeConvertor;
import sorm.core.TableContext;
import sorm.core.TypeConvertor;

/**
 * 封装了生成Java文件（源代码）常用的操作
 * @author yang
 */
public class JavaFileUtils {
	/**
	 * 根据字段信息生成java属性信息，如varchar username--private String username,以及相应的set和get方法
	 * @param column 字段信息
	 * @param convertor 类型转化器
	 * @return java属性和set/get方法源码
	 */
	public static JavaFieldGetSet createFieldGetSetSRC(ColumnInfo column,TypeConvertor convertor){
		JavaFieldGetSet jfgs = new JavaFieldGetSet();
		String javaFieldType = convertor.databaseType2JavaType(column.getDataType());//将varchar转为String
		jfgs.setFieldInfo("\tprivate "+javaFieldType+" "+column.getName()+";\n");//拼接字符串，\t\n是为了生成的代码有格式
		
		//public String getUsername(){return username;}
		//生成get方法的源代码
		StringBuilder getSrc = new StringBuilder();//由于需要频繁拼接，就用StringBuilder
		getSrc.append("\tpublic "+javaFieldType+" get"+StringUtils.firstChar2UpperCase(column.getName())+"(){\n");
		getSrc.append("\t\treturn "+column.getName()+";\n");
		getSrc.append("\t}\n ");
		jfgs.setGetInfo(getSrc.toString());
		
		//public String setUsername(String username){return this.username=username;}
		//生成set方法的源代码
		StringBuilder setSrc = new StringBuilder();
		setSrc.append("\tpublic void set"+StringUtils.firstChar2UpperCase(column.getName())+"(");
		setSrc.append(javaFieldType+" "+column.getName()+"){\n");
		setSrc.append("\t\tthis."+column.getName()+"="+column.getName()+";\n");
		setSrc.append("\t}\n");
		jfgs.setSetInfo(setSrc.toString());
		
		return jfgs;
	}
	
	/**
	 * 根据表信息生成Java类的源代码（构造器没生成）
	 * @param tableInfo
	 * @param convertor
	 * @return
	 */
	public static String createJavaSrc(TableInfo tableInfo,TypeConvertor convertor){
		Map<String,ColumnInfo> columns = tableInfo.getColumns();//表中所有字段信息
		List<JavaFieldGetSet> javaFields = new ArrayList<>();//所有java属性信息及set，get方法
		for (ColumnInfo c : columns.values()) {
			javaFields.add(createFieldGetSetSRC(c,convertor));
		}
		StringBuilder src = new StringBuilder();
		//生成package语句
		src.append("package "+DBManager.getConf().getPoPackage()+";\n\n");
		//生成import语句
		src.append("import java.sql.*;\n");//先写死要导入的包
		src.append("import java.util.*;\n\n");
		//生成类声明语句
		src.append("public class "+StringUtils.firstChar2UpperCase(tableInfo.getTname())+" {\n\n");
		//生成属性列表源码
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getFieldInfo());
		}
		src.append("\n\n");
		//生成get方法列表
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getGetInfo());
		}
		//生成set方法列表
		for (JavaFieldGetSet f : javaFields) {
			src.append(f.getSetInfo());
		}
		//生成类结束
		src.append("}\n");
//		System.out.println(src);
		return src.toString();
	}
	/**
	 * 将生成的文件放到指定的包里
	 * @param tableinfo
	 * @param convertor
	 */
	public static void createJavaPOFile(TableInfo tableinfo,TypeConvertor convertor){
		String src = createJavaSrc(tableinfo,convertor);
		String srcPath = DBManager.getConf().getSrcPath()+"/src/";//视频只写了"/"，可能是mac和windows的区别，获得的路径mac会少个src
		String packagePath = DBManager.getConf().getPoPackage().replaceAll("\\.", "/");//把.换成\,正则是\. \\，在java中一斜杠变两个，或\\\\写成/也行，mac里写成/
		
		File f = new File(srcPath+packagePath);//先创建文件夹
		System.out.println(f.getAbsolutePath()+"*******");
		if(!f.exists()){
			f.mkdirs();
		}
		//通过io流把字符串写到对应的文件里
		BufferedWriter bw = null;
		try {//再创建文件
			bw = new BufferedWriter(new FileWriter(f.getAbsoluteFile()+"/"+StringUtils.firstChar2UpperCase(tableinfo.getTname())+".java"));//写到指定地方，地方就是dp.properties里最后两项指定的
			bw.write(src);
			System.out.println("建立表"+tableinfo.getTname()+"对应的java类"+StringUtils.firstChar2UpperCase(tableinfo.getTname())+".java");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(bw!=null){
					bw.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args) {
//		ColumnInfo ci = new ColumnInfo("username","varchar",0);
//		ColumnInfo ci2 = new ColumnInfo("id","int",0);
//		JavaFieldGetSet f = createFieldGetSetSRC(ci2, new MysqlTypeConvertor());
//		System.out.println(f); 
		
		Map<String,TableInfo> map = TableContext.tables;
		for (TableInfo t : map.values()) {//遍历，将所有的表都生成对应的javabean
//			TableInfo t = map.get("dept");
//			createJavaSrc(t, new MySqlTypeConvertor());
			createJavaPOFile(t, new MySqlTypeConvertor());
		}
	}
}
