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

public class Log4jGenerator implements IGenerator {
//	protected String template = "/com/generator/MyGenerator/templates/web.jf";
	protected String template = "log4j.jf";

	protected MyGenerator myGenerator;
	protected String log4jOutputDir;

	public Log4jGenerator(String log4jOutputDir, MyGenerator myGenerator) {
		this.log4jOutputDir = log4jOutputDir;
		this.myGenerator=myGenerator;
	}

	/**
	 * 使用自定义模板生成 SuperModel
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void setLog4jOutputDir(String log4jOutputDir) {
		if (StrKit.notBlank(log4jOutputDir)) {
			this.log4jOutputDir = log4jOutputDir;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate log4j.properties file ...");
		System.out.println("log4j.properties Output Dir: " + log4jOutputDir);

		Engine engine = Engine.create("forLog4j");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("myGenerator", myGenerator);

		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}

	/**
	 * web.xml 不覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(log4jOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String target = log4jOutputDir + File.separator + myGenerator.log4jName + ".properties";
			File file = new File(target);
			if (file.exists()) {
				System.out.println("已存在,不覆盖");
				return; // 若 存在，不覆盖
			}
			fw = new FileWriter(file);
			fw.write(ret);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					LogKit.error(e.getMessage(), e);
				}
			}
		}
	}
}
