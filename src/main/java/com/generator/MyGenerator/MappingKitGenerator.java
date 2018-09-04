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

/**
 * MappingKit 文件生成器
 */
public class MappingKitGenerator implements IGenerator{
	
//	protected String template = "/com/generator/MyGenerator/templates/_MappingKit.jf";
	protected String template = "_MappingKit.jf";
	
	protected MyGenerator myGenerator;
	protected String mappingKitPackageName;
	protected String mappingKitOutputDir;
	
	public MappingKitGenerator(String mappingKitPackageName,String mappingKitOutputDir,MyGenerator myGenerator) {
		this.mappingKitPackageName = mappingKitPackageName;
		this.mappingKitOutputDir = mappingKitOutputDir;
		this.myGenerator=myGenerator;
	}
	
	/**
	 * 使用自定义模板生成 MappingKit
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	
	public void setMappingKitOutputDir(String mappingKitOutputDir) {
		if (StrKit.notBlank(mappingKitOutputDir)) {
			this.mappingKitOutputDir = mappingKitOutputDir;
		}
	}
	
	public void setMappingKitPackageName(String mappingKitPackageName) {
		if (StrKit.notBlank(mappingKitPackageName)) {
			this.mappingKitPackageName = mappingKitPackageName;
		}
	}
	
	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate MappingKit file ...");
		System.out.println("MappingKit Output Dir: " + mappingKitOutputDir);
		
		Engine engine = Engine.create("forMappingKit");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());
		
		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);
		
		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}
	
	/**
	 * _MappingKit.java 不覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(mappingKitOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String target = mappingKitOutputDir + File.separator + myGenerator.mappingKitClassName + ".java";
			File file = new File(target);
			if (file.exists()) {
				System.out.println("已存在,不覆盖");
				return; // 若 存在，不覆盖
			}
			fw = new FileWriter(target);
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
