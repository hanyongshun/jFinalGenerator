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

public class JfinalPropGenerator implements IGenerator{
//	protected String template ="/com/generator/MyGenerator/templates/jfinal.jf";
	protected String template ="jfinal.jf";

	protected MyGenerator myGenerator;
	protected String jfinalPropOutputDir;
	
	public JfinalPropGenerator(String jfinalPropOutputDir, MyGenerator myGenerator) {
		if (StrKit.isBlank(jfinalPropOutputDir)) {
			throw new IllegalArgumentException("jfinalPropOutputDir can not be blank.");
		}
		
		this.jfinalPropOutputDir = jfinalPropOutputDir;
		this.myGenerator=myGenerator;
	}
	
	public void setTemplate(String template) {
		if(StrKit.notBlank(template)){
			this.template = template;
		}
	}

	public void setJfinalPropOutputDir(String jfinalPropOutputDir) {
		if(StrKit.notBlank(jfinalPropOutputDir)){
			this.jfinalPropOutputDir = jfinalPropOutputDir;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate jfinalProp file ...");
		System.out.println("JfinalProp Output Dir: " + jfinalPropOutputDir);
		
		Engine engine = Engine.create("forJfinalProp");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());
		
		Kv data = Kv.by("myGenerator", myGenerator);
		
		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}
	
	/**
	 * jfinal.properties 不覆盖
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(jfinalPropOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			String target = jfinalPropOutputDir + File.separator + myGenerator.alittle_config_file_name;
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
