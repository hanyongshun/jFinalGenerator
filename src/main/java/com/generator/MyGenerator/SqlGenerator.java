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

public class SqlGenerator implements IGenerator{
//	protected String template = "/com/generator/MyGenerator/templates/sql.jf";
	protected String template = "sql.jf";

	protected MyGenerator myGenerator;
	protected String sqlOutputDir;

	public SqlGenerator(String sqlOutputDir,MyGenerator myGenerator) {
		if (StrKit.isBlank(sqlOutputDir)) {
			throw new IllegalArgumentException("sqlOutputDir can not be blank.");
		}

		this.sqlOutputDir = sqlOutputDir;
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
		System.out.println("Controller Output Dir: " + sqlOutputDir);

		Engine engine = Engine.create("forSql");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		for (TableMeta tableMeta : tableMetas) {
			genModelContent(tableMeta);
		}
		writeToFile(tableMetas);
	}

	protected void genModelContent(TableMeta tableMeta) {
		Kv data = Kv.by("tableMeta", tableMeta);
		data.set("myGenerator",myGenerator);

		String ret = Engine.use("forSql").getTemplate(template).renderToString(data);
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
		File dir = new File(sqlOutputDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		String target = sqlOutputDir + File.separator + tableMeta.name+ ".sql";

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
