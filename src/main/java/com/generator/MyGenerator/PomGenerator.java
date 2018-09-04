package com.generator.MyGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.generator.HelperClass.TableMeta;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

public class PomGenerator implements IGenerator {

//	protected String template = "/com/generator/MyGenerator/templates/pom.jf";
	protected String template = "pom.jf";

	protected String pomXMLOutputDir;
	protected String pomXMLFileName = "pom";

	public PomGenerator(String pomXMLOutputDir) {
		if (StrKit.isBlank(pomXMLOutputDir)) {
			throw new IllegalArgumentException("pomXMLOutputDir can not be blank.");
		}

		this.pomXMLOutputDir = pomXMLOutputDir;
	}

	public void setTemplate(String template) {
		if (StrKit.notBlank(template)) {
			this.template = template;
		}
	}

	public void setAllSqlOutputDir(String pomXMLOutputDir) {
		if (StrKit.notBlank(pomXMLOutputDir)) {
			this.pomXMLOutputDir = pomXMLOutputDir;
		}
	}

	public void setAllSqlFileName(String pomXMLFileName) {
		if (StrKit.notBlank(pomXMLFileName)) {
			this.pomXMLFileName = pomXMLFileName;
		}
	}

	public void generate(List<TableMeta> tableMetas) {
		System.out.println("Generate pomXML file ...");
		System.out.println("PomXML Output Dir: " + pomXMLOutputDir);

		Engine engine = Engine.create("forPomXML");
		engine.setSourceFactory(new ClassPathSourceFactory());
		engine.setBaseTemplatePath("templates");
		engine.addSharedMethod(new StrKit());

		Kv data = Kv.by("tableMetas", tableMetas);

		String ret = engine.getTemplate(template).renderToString(data);
		mergeXML(new File(pomXMLOutputDir + pomXMLFileName + ".xml"), ret);
	}

	/**
	 * 新增xml片段
	 * 
	 * @param xml
	 * @param appendingXml
	 * @throws Exception
	 */
	public void mergeXML(File xml, String appendingXml) {
		XMLWriter writer = null;
		try {
			SAXReader reader = new SAXReader();
			Document doc = reader.read(xml);

			Document flagment = DocumentHelper.parseText(appendingXml);
			Element flagEle = flagment.getRootElement();
			flagEle.setQName(new QName(flagEle.getName(), doc.getRootElement().getNamespace()));
			if (flagEle.elements().size() > 0) {
				for (Object c : flagEle.elements()) {
					Element cel = (Element) c;
					cel.setQName(new QName(cel.getName(), flagEle.getNamespace()));
				}
			}
			doc.getRootElement().add(flagEle);

			writer = new XMLWriter(new FileWriter(xml));
			writer.write(doc.getRootElement());
		} catch (Exception e) {
			throw new RuntimeException("融合xml文件失败:" + pomXMLFileName);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
