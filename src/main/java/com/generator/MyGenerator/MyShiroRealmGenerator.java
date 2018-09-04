package com.generator.MyGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.generator.HelperClass.TableMeta;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

public class MyShiroRealmGenerator implements IGenerator{
//	protected String template = "/com/generator/MyGenerator/templates/SuperModel.jf";
	protected String template = "MyShiroRealm.jf";
	protected String shiroIniTemplate = "ShiroIni.jf";
	
	protected MyGenerator myGenerator;
	protected String myShiroRealmPackageName;
	protected String myShiroRealmOutputDir;
	
	public MyShiroRealmGenerator(String myShiroRealmPackageName, String myShiroRealmOutputDir,MyGenerator myGenerator) {
		this.myShiroRealmPackageName = myShiroRealmPackageName;
		this.myShiroRealmOutputDir = myShiroRealmOutputDir;
		this.myGenerator=myGenerator;
	}
	
	/**
	 * 使用自定义模板生成 SuperModel
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setBaseControllerOutputDir(String myShiroRealmOutputDir) {
		if (StrKit.notBlank(myShiroRealmOutputDir)) {
			this.myShiroRealmOutputDir = myShiroRealmOutputDir;
		}
	}
	
	public void setMyShiroRealmPackageName(String myShiroRealmPackageName) {
		if (StrKit.notBlank(myShiroRealmPackageName)) {
			this.myShiroRealmPackageName = myShiroRealmPackageName;
		}
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate myShiroRealm file ...");
		System.out.println("myShiroRealm Output Dir: " + myShiroRealmOutputDir);
		
		Engine engine = Engine.create("forMyShiroRealm");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());
		
		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);
		
		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}
	
	/**
	 * _MappingKit.java 覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(myShiroRealmOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String target = myShiroRealmOutputDir + File.separator + myGenerator.myShiroRealmClassName + ".java";
			File file = new File(target);
			if (file.exists()) {
				System.out.println("已存在,不覆盖");
				return; // 若 存在，不覆盖
			}
			fw = new FileWriter(file);
			fw.write(ret);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (fw != null) {
				try {fw.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
			}
		}
	}
}
