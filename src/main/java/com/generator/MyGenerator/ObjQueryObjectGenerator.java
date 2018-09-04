package com.generator.MyGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.generator.HelperClass.TableMeta;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

public class ObjQueryObjectGenerator implements IGenerator{
//	protected String template ="/com/generator/MyGenerator/templates/ObjQueryObject.jf";
	protected String template ="ObjQueryObject.jf";

	protected MyGenerator myGenerator;
	protected String objQueryObjectPackageName;
	protected String objQueryObjectOutputDir;

	public ObjQueryObjectGenerator(String objQueryObjectPackageName, String objQueryObjectOutputDir,MyGenerator myGenerator) {
		if (StrKit.isBlank(objQueryObjectPackageName)) {
			throw new IllegalArgumentException("objQueryObjectPackageName can not be blank.");
		}
		if (objQueryObjectPackageName.contains("/") || objQueryObjectPackageName.contains("\\")) {
			throw new IllegalArgumentException("objQueryObjectPackageName error : " + objQueryObjectPackageName);
		}
		if (StrKit.isBlank(objQueryObjectOutputDir)) {
			throw new IllegalArgumentException("objQueryObjectOutputDir can not be blank.");
		}

		this.objQueryObjectPackageName = objQueryObjectPackageName;
		this.objQueryObjectOutputDir = objQueryObjectOutputDir;
		this.myGenerator=myGenerator;
	}

	/**
	 * 使用自定义模板生成 model
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate objQueryObject ...");
		System.out.println("objQueryObject Output Dir: " + objQueryObjectOutputDir);

		Engine engine = Engine.create("forObjQueryObject");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		for (TableMeta tableMeta : tableMetas) {
			genModelContent(tableMeta);
		}
		writeToFile(tableMetas);
	}

	protected void genModelContent(TableMeta tableMeta) {
		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMeta", tableMeta);

		String ret = Engine.use("forObjQueryObject").getTemplate(template).renderToString(data);
		tableMeta.modelContent = ret;
	}

	protected void writeToFile(List<TableMeta> tableMetas) {
		try {
			for (TableMeta tableMeta : tableMetas) {
				writeToFile(tableMeta);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 若 model 文件存在，则不生成，以免覆盖用户手写的代码
	 */
	protected void writeToFile(TableMeta tableMeta) throws IOException {
		File dir = new File(objQueryObjectOutputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String target = objQueryObjectOutputDir + File.separator + tableMeta.modelName+ this.myGenerator.queryObjectName + ".java";

		File file = new File(target);
		if (file.exists()) {
			System.out.println("已存在,不覆盖");
			return; // 若 Model 存在，不覆盖
		}

		FileWriter fw = new FileWriter(file);
		try {
			fw.write(tableMeta.modelContent);
		} finally {
			fw.close();
		}
	}
}
