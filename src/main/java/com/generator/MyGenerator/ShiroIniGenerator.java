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

public class ShiroIniGenerator implements IGenerator {
//	protected String template = "/com/generator/MyGenerator/templates/web.jf";
	protected String template = "shiro.jf";

	protected MyGenerator myGenerator;
	protected String shiroIniOutputDir;

	public ShiroIniGenerator(String shiroIniOutputDir, MyGenerator myGenerator) {
		this.shiroIniOutputDir = shiroIniOutputDir;
		this.myGenerator=myGenerator;
	}

	/**
	 * 使用自定义模板生成 SuperModel
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	public void setShiroIniOutputDir(String shiroIniOutputDir) {
		if (StrKit.notBlank(shiroIniOutputDir)) {
			this.shiroIniOutputDir = shiroIniOutputDir;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate shiro.ini file ...");
		System.out.println("shiro.ini Output Dir: " + shiroIniOutputDir);

		Engine engine = Engine.create("forShiroIni");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("myGenerator", myGenerator);
		data.set("tableMetas", tableMetas);

		String ret = engine.getTemplate(template).renderToString(data);
		writeToFile(ret);
	}

	/**
	 * web.xml 不覆盖写入
	 */
	protected void writeToFile(String ret) {
		FileWriter fw = null;
		try {
			File dir = new File(shiroIniOutputDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String target = shiroIniOutputDir + File.separator + myGenerator.shiroIniName + ".ini";
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
