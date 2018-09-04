package com.generator.MyGenerator;

import java.util.List;

import com.generator.HelperClass.TableMeta;

public interface IGenerator {
	void generate(List<TableMeta> tableMetas);
}
