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

public class CoreRoutesGenerator implements IGenerator {
//	protected String template = "/com/generator/MyGenerator/templates/CoreRoutes.jf";
	protected String template = "CoreRoutes.jf";

	protected MyGenerator myGenerator;
	protected String coreRoutesPackageName;
	protected String coreRoutesOutputDir;

	public CoreRoutesGenerator(String coreRoutesPackageName, String coreRoutesOutputDir,MyGenerator myGenerator) {
		this.coreRoutesPackageName = coreRoutesPackageName;
		this.coreRoutesOutputDir = coreRoutesOutputDir;
		this.myGenerator=myGenerator;
	}

	/**
	 * 使用自定义模板生成 CoreRoutes
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void setCoreRoutesOutputDir(String coreRoutesOutputDir) {
		if (StrKit.notBlank(coreRoutesOutputDir)) {
			this.coreRoutesOutputDir = coreRoutesOutputDir;
		}
	}

	public void setCoreRoutesPackageName(String coreRoutesPackageName) {
		if (StrKit.notBlank(coreRoutesPackageName)) {
			this.coreRoutesPackageName = coreRoutesPackageName;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate CoreRoutes file ...");
		System.out.println("CoreRoutes Output Dir: " + coreRoutesOutputDir);

		Engine engine = Engine.create("forCoreRoutes");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);

		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}

	/**
	 * CoreRoutes.java 不覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(coreRoutesOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String target = coreRoutesOutputDir + File.separator + myGenerator.coreRoutesName + ".java";
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
