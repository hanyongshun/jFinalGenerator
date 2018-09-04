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

public class BaseControllerlGenerator implements IGenerator{
//	protected String template = "/com/generator/MyGenerator/templates/SuperModel.jf";
	protected String template = "BaseController.jf";
	
	protected MyGenerator myGenerator;
	protected String baseControllerPackageName;
	protected String baseControllerlOutputDir;
	
	public BaseControllerlGenerator(String baseControllerPackageName, String baseControllerlOutputDir,MyGenerator myGenerator) {
		this.baseControllerPackageName = baseControllerPackageName;
		this.baseControllerlOutputDir = baseControllerlOutputDir;
		this.myGenerator=myGenerator;
	}
	
	/**
	 * 使用自定义模板生成 SuperModel
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setBaseControllerOutputDir(String baseControllerOutputDir) {
		if (StrKit.notBlank(baseControllerOutputDir)) {
			this.baseControllerlOutputDir = baseControllerOutputDir;
		}
	}
	
	public void setBaseControllerPackageName(String baseControllerPackageName) {
		if (StrKit.notBlank(baseControllerPackageName)) {
			this.baseControllerPackageName = baseControllerPackageName;
		}
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate BaseController file ...");
		System.out.println("BaseController Output Dir: " + baseControllerlOutputDir);
		
		Engine engine = Engine.create("forBaseController");
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
			File dir = new File(baseControllerlOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String target = baseControllerlOutputDir + File.separator + myGenerator.baseControllerClassName + ".java";
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
