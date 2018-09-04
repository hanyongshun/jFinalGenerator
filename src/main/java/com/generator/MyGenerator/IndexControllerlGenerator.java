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

public class IndexControllerlGenerator implements IGenerator{
//	protected String template = "/com/generator/MyGenerator/templates/SuperModel.jf";
	protected String template = "IndexController.jf";
	
	protected MyGenerator myGenerator;
	protected String indexControllerPackageName;
	protected String indexControllerOutputDir;
	
	public IndexControllerlGenerator(String indexControllerPackageName, String indexControllerOutputDir,MyGenerator myGenerator) {
		this.indexControllerPackageName = indexControllerPackageName;
		this.indexControllerOutputDir = indexControllerOutputDir;
		this.myGenerator=myGenerator;
	}
	
	/**
	 * 使用自定义模板生成 SuperModel
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setIndexControllerOutputDir(String indexControllerOutputDir) {
		if (StrKit.notBlank(indexControllerOutputDir)) {
			this.indexControllerOutputDir = indexControllerOutputDir;
		}
	}
	
	public void setIndexControllerPackageName(String indexControllerPackageName) {
		if (StrKit.notBlank(indexControllerPackageName)) {
			this.indexControllerPackageName = indexControllerPackageName;
		}
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate IndexController file ...");
		System.out.println("IndexController Output Dir: " + indexControllerOutputDir);
		
		Engine engine = Engine.create("forIndexController");
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
			File dir = new File(indexControllerOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String target = indexControllerOutputDir + File.separator + myGenerator.indexControllerClassName + ".java";
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
