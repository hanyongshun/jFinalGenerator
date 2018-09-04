package com.generator.MyGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.generator.HelperClass.TableMeta;
import com.jfinal.kit.Kv;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

public class SuperModelGenerator implements IGenerator{
//	protected String template = "/com/generator/MyGenerator/templates/SuperModel.jf";
	protected String template = "SuperModel.jf";
	
	protected MyGenerator myGenerator;
	protected String superModelPackageName;
	protected String superModelOutputDir;
	
	public SuperModelGenerator(String superModelPackageName, String superModelOutputDir,MyGenerator myGenerator) {
		this.superModelPackageName = superModelPackageName;
		this.superModelOutputDir = superModelOutputDir;
		this.myGenerator=myGenerator;
	}
	
	/**
	 * 使用自定义模板生成 SuperModel
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setSuperModelOutputDir(String superModelOutputDir) {
		if (StrKit.notBlank(superModelOutputDir)) {
			this.superModelOutputDir = superModelOutputDir;
		}
	}
	
	public void setSuperModelPackageName(String superModelPackageName) {
		if (StrKit.notBlank(superModelPackageName)) {
			this.superModelPackageName = superModelPackageName;
		}
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate SuperModel file ...");
		System.out.println("SuperModel Output Dir: " + superModelOutputDir);
		
		Engine engine = Engine.create("forSuperModel");
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
			File dir = new File(superModelOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String target = superModelOutputDir + File.separator + myGenerator.superModelClassName + ".java";
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
