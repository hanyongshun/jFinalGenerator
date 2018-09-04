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

public class CoreConfigGenerator implements IGenerator {
//	protected String template = "com/generator/MyGenerator/templates/CoreConfig.jf";
	protected String template = "CoreConfig.jf";

	protected MyGenerator myGenerator;
	protected String coreConfigPackageName;
	protected String coreConfigOutputDir;

	public CoreConfigGenerator(String coreConfigPackageName, String coreConfigOutputDir, MyGenerator myGenerator) {
		this.coreConfigPackageName = coreConfigPackageName;
		this.coreConfigOutputDir = coreConfigOutputDir;
		this.myGenerator = myGenerator;
	}

	/**
	 * 使用自定义模板生成 CoreConfig
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void setCoreConfigOutputDir(String coreConfigOutputDir) {
		if (StrKit.notBlank(coreConfigOutputDir)) {
			this.coreConfigOutputDir = coreConfigOutputDir;
		}
	}

	public void setCoreConfigPackageName(String coreConfigPackageName) {
		if (StrKit.notBlank(coreConfigPackageName)) {
			this.coreConfigPackageName = coreConfigPackageName;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate CoreConfig file ...");
		System.out.println("CoreConfig Output Dir: " + coreConfigOutputDir);

		Engine engine = Engine.create("forCoreConfig");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("coreConfigPackageName", coreConfigPackageName);
		data.set("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);

		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}

	/**
	 * CoreConfig.java 不覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(coreConfigOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String target = coreConfigOutputDir + File.separator + myGenerator.coreConfigName + ".java";
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
