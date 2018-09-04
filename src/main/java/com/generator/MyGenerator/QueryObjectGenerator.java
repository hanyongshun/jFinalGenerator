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

public class QueryObjectGenerator implements IGenerator {
//	protected String template = "/com/generator/MyGenerator/templates/QueryObject.jf";
	protected String template = "QueryObject.jf";

	protected MyGenerator myGenerator;
	protected String queryObjectPackageName;
	protected String queryObjectOutputDir;

	public QueryObjectGenerator(String queryObjectPackageName, String queryObjectOutputDir,MyGenerator myGenerator) {
		this.queryObjectPackageName = queryObjectPackageName;
		this.queryObjectOutputDir = queryObjectOutputDir;
		this.myGenerator = myGenerator;
	}

	/**
	 * 使用自定义模板生成 MappingKit
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void setMappingKitOutputDir(String queryObjectOutputDir) {
		if (StrKit.notBlank(queryObjectOutputDir)) {
			this.queryObjectOutputDir = queryObjectOutputDir;
		}
	}

	public void setMappingKitPackageName(String queryObjectPackageName) {
		if (StrKit.notBlank(queryObjectPackageName)) {
			this.queryObjectPackageName = queryObjectPackageName;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate QueryObject file ...");
		System.out.println("QueryObject Output Dir: " + queryObjectOutputDir);

		Engine engine = Engine.create("forQueryObject");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);

		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}

	/**
	 * QueryObject.java 不覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(queryObjectOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String target = queryObjectOutputDir + File.separator + myGenerator.queryObjectName + ".java";
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
