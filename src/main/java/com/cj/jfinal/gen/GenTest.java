package com.cj.jfinal.gen;

import com.cj.common.config.MainConfig;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;

import javax.sql.DataSource;

/**
 * @version 1.0
 * @author： jinmunan
 * @date： 2022-07-13 16:16
 */
public class GenTest {
    public static void main(String[] args) {

        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = "com.cj.jfinal.gen.model";

        // base model 所使用的包名
        String baseModelPackageName = modelPackageName + ".base";

        // base model 文件保存路径
        // 注意从 jfinal 4.9.12 版开始，PathKit.getWebRootPath() 在此的用法要改成 System.getProperty("user.dir")
        String baseModelOutputDir = System.getProperty("user.dir") + "/src/main/java/" + baseModelPackageName.replace('.', '/');

        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        System.out.println("输出路径：" + baseModelOutputDir);

        // 创建生成器
        Generator gen = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);

        // 设置数据库方言
        gen.setDialect(new MysqlDialect());

        // 在 getter、setter 方法上生成字段备注内容
        gen.setGenerateRemarks(true);

        // 开始生成代码
        gen.generate();
    }

    private static DataSource getDataSource() {
        DruidPlugin druidPlugin = MainConfig.createDruidPlugin();
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }
}
