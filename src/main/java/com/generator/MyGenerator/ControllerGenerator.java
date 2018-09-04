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

public class ControllerGenerator implements IGenerator{
//	protected String template ="/com/generator/MyGenerator/templates/ObjController.jf";
	protected String template ="ObjController.jf";

	protected MyGenerator myGenerator;
	protected String controllerPackageName;
	protected String controllerOutputDir;

	public ControllerGenerator(String controllerPackageName, String controllerOutputDir,MyGenerator myGenerator) {
		if (StrKit.isBlank(controllerPackageName)) {
			throw new IllegalArgumentException("controllerPackageName can not be blank.");
		}
		if (controllerPackageName.contains("/") || controllerPackageName.contains("\\")) {
			throw new IllegalArgumentException("controllerPackageName error : " + controllerPackageName);
		}
		if (StrKit.isBlank(controllerOutputDir)) {
			throw new IllegalArgumentException("controllerOutputDir can not be blank.");
		}

		this.controllerPackageName = controllerPackageName;
		this.controllerOutputDir = controllerOutputDir;
		this.myGenerator=myGenerator;
	}

	/**
	 * 使用自定义模板生成 model
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate controller ...");
		System.out.println("Controller Output Dir: " + controllerOutputDir);

		Engine engine = Engine.create("forController");
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

		String ret = Engine.use("forController").getTemplate(template).renderToString(data);
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
		File dir = new File(controllerOutputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String target = controllerOutputDir + File.separator + tableMeta.modelName+ "Controller" + ".java";

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
