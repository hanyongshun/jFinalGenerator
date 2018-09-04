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

public class UtilsGenerator implements IGenerator {
	// protected String template =
	// "/com/generator/MyGenerator/templates/web.jf";
	protected String ipKitTemplate = "IpKit.jf";
	protected String resultMsgTemplate = "ResultMsg.jf";
	protected String resultStatusCodeTemplate = "ResultStatusCode.jf";

	protected MyGenerator myGenerator;
	protected String utilsPackageName;
	protected String utilsOutputDir;

	public UtilsGenerator(String utilsPackageName, String utilsOutputDir, MyGenerator myGenerator) {
		this.utilsPackageName = utilsPackageName;
		this.utilsOutputDir = utilsOutputDir;
		this.myGenerator = myGenerator;
	}

	public void setUtilsOutputDir(String utilsOutputDir) {
		if (StrKit.notBlank(utilsOutputDir)) {
			this.utilsOutputDir = utilsOutputDir;
		}
	}
	
	public void setUtilsPackageName(String utilsPackageName) {
		if (StrKit.notBlank(utilsPackageName)) {
			this.utilsPackageName = utilsPackageName;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate utils files ...");
		System.out.println("utils Output Dir: " + utilsOutputDir);

		Engine engine = Engine.create("forUtils");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);

		String ret = engine.getTemplate(ipKitTemplate).renderToString(data);
		writeToFile(ret, utilsOutputDir, "IpKit.java");

		ret = engine.getTemplate(resultMsgTemplate).renderToString(data);
		writeToFile(ret, utilsOutputDir, "ResultMsg.java");

		ret = engine.getTemplate(resultStatusCodeTemplate).renderToString(data);
		writeToFile(ret, utilsOutputDir, "ResultStatusCode.java");
	}

	/**
	 * web.xml 不覆盖写入
	 */
	protected void writeToFile(String ret, String outPutDir, String filename) {
		FileWriter fw = null;
		try {
			File dir = new File(outPutDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String target = outPutDir + File.separator + filename;
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
