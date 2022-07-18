package com.cj.module.core.generator.service;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.config.MainConfig;
import com.cj.module.core.generator.kit.GeneratorKit;
import com.cj.common.vo.Grid;
import com.jfinal.kit.*;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;


import java.io.IOException;
import java.util.*;

/**
 * 代码生成器接口
 *
 * @author THINKPAD
 */
public class CodeService {


    /**
     * 针对 Model 中七种可以自动转换类型的 getter 方法，调用其具有确定类型返回值的 getter 方法
     * 享用自动类型转换的便利性，例如 getInt(String)、getStr(String)
     * 其它方法使用泛型返回值方法： get(String)
     * 注意：jfinal 3.2 及以上版本 Model 中的六种 getter 方法才具有类型转换功能
     */
    @SuppressWarnings("serial")
    protected static Map<String, String> getterTypeMap = new HashMap<String, String>() {{
        put("java.lang.String", "getStr");
        put("java.lang.Integer", "getInt");
        put("java.lang.Long", "getLong");
        put("java.lang.Double", "getDouble");
        put("java.lang.Float", "getFloat");
        put("java.lang.Short", "getShort");
        put("java.lang.Byte", "getByte");
    }};

    static Engine engine;
    static DruidPlugin druidPlugin;

    static {
        engine = new Engine();
        engine.setToClassPathSourceFactory();
        engine.addSharedMethod(new StrKit());
        engine.addSharedObject("getterTypeMap", getterTypeMap);
        engine.addSharedObject("javaKeyword", JavaKeyword.me);
        druidPlugin = MainConfig.createDruidPlugin();
        druidPlugin.start();
    }

    /**
     * 查询数据库表并渲染到页面
     *
     * @param record
     * @return
     */
    public Grid queryTablesList(Record record) {
        MetaBuilder metaBuilder = new MetaBuilder(druidPlugin.getDataSource());
        // oracle数据库
        if ("oracle".equals(PropKit.get("dbType"))) {
            metaBuilder.setDialect(new OracleDialect());
        }
        // 添加不需要获取的数据表白名单
        String[] excTable = {"sys_user", "sys_org", "sys_role", "sys_function", "sys_role_function", "sys_user_role", "sys_log", "sys_tree", "data_dictionary", "data_dictionary_value", "file_uploaded"};
        metaBuilder.addExcludedTable(excTable);
        // 实体名称去掉前缀
        metaBuilder.setRemovedTableNamePrefixes("w_", "t_", "W_", "T_");
        // 设置生成备注
        metaBuilder.setGenerateRemarks(true);
        // TableMeta 数据库的表
        List<TableMeta> tableMetas = metaBuilder.build();
        List<TableMeta> resultList = new ArrayList<>();

        // 模糊查询的参数
        String name = record.get("name");
        int pageNumber = Integer.valueOf(record.get("pageNumber", "1"));
        int pageSize = Integer.valueOf(record.get("pageSize", "10"));
        int startIndex = (pageNumber - 1) * pageSize;

        // 就是为了分页封装数据用的
        for (int i = 0; i < tableMetas.size(); i++) {
            //搜索
            if (name != null) {
                // 以什么结束
                if (name.endsWith("=")) {
                    if (tableMetas.get(i).name.equalsIgnoreCase(name.replace("=", ""))) {
                        resultList.add(tableMetas.get(i));
                    }
                } else {
                    // 存在
                    if (tableMetas.get(i).name.indexOf(name) != -1) {
                        resultList.add(tableMetas.get(i));
                    }
                }
            } else {
                if (i >= startIndex) {
                    resultList.add(tableMetas.get(i));
                }
            }
            //分页
            if (resultList.size() == pageSize) {
                break;
            }
        }

        //oracle数据库
        if ("oracle".equals(PropKit.get("dbType"))) {
            return new Grid(buildTableRemarksByOracle(resultList), tableMetas.size());
        }

        //mysql数据库
        return new Grid(buildTableRemarks(resultList), tableMetas.size());
    }

    /**
     * 查询表备注信息
     *
     * @param tableMetaList
     * @return
     */
    private List<TableMeta> buildTableRemarks(List<TableMeta> tableMetaList) {
        String sql = "SELECT TABLE_NAME,TABLE_COMMENT FROM information_schema.TABLES WHERE TABLE_NAME=?";
        for (TableMeta t : tableMetaList) {
            Record rd = Db.find(sql, t.name).get(0);
            t.remarks = rd.getStr("TABLE_COMMENT");
        }
        return tableMetaList;
    }

    /**
     * Oracle 建表备注
     *
     * @param tableMetaList
     * @return
     */
    private List<TableMeta> buildTableRemarksByOracle(List<TableMeta> tableMetaList) {
        String sql = "select ut.COLUMN_NAME,uc.COMMENTS,tc.COMMENTS TABLE_COMMENTS"
                + " from user_tab_columns  ut"
                + " inner JOIN user_col_comments uc"
                + " on ut.TABLE_NAME  = uc.TABLE_NAME and ut.COLUMN_NAME = uc.COLUMN_NAME"
                + " inner JOIN user_tab_comments tc"
                + " on ut.TABLE_NAME = tc.TABLE_NAME"
                + " where ut.TABLE_NAME=? ";

        for (TableMeta t : tableMetaList) {
            List<Record> list = Db.find(sql, t.name);
            Record rd = list.get(0);
            t.remarks = rd.getStr("TABLE_COMMENTS");
            t.primaryKey = t.primaryKey.toLowerCase();
            List<ColumnMeta> columnMetas = t.columnMetas;
            columnMetas.forEach(column -> {
                String name = column.name;
                list.forEach(r -> {
                    String columnName = r.getStr("COLUMN_NAME");
                    if (name.equals(columnName)) {
                        column.remarks = r.getStr("COMMENTS");
                        column.name = name.toLowerCase();
                    }
                });
            });
        }
        return tableMetaList;
    }

    /**
     * 生成模板代码
     *
     * @param record
     * @param formItem
     * @param tableItem
     * @return
     */
    public Record createCodeTemplate(Record record, String formItem, String tableItem) {
        Record result = new Record();
        List<String> codeJava = new ArrayList<>();
        List<String> codeHtml = new ArrayList<>();
        String modelName = record.getStr("modelName");
        String authorName = record.get("authorName");
        String basePackage = record.getStr("packageName");
        String tableComment = record.getStr("tableComment");
        String primaryKey = record.getStr("primaryKey");

        //模板变量
        GeneratorKit codeKit = GeneratorKit.getInstance();
        String portal = basePackage.split("\\.")[2];
        String modular = basePackage.substring(basePackage.indexOf(portal));
        @SuppressWarnings("static-access")
        Kv kv = codeKit.setBasePackage(basePackage).setModular(modular.replace(".", "/")).getJavaKv(modelName);
        kv.set("author", authorName).set("tableComment", tableComment).set("primaryKey", primaryKey);

        //项目模板
        String[] java = {"Controller", "Service"};
        String[] html = {"index", "add", "edit", "_form"};

        //java模板内容
        for (String str : java) {
            String content = Db.getSql("code." + str);
            @SuppressWarnings("unchecked")
            Iterator<Object> iter = kv.keySet().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                System.out.println(obj);
                content = content.replace("${" + obj + "}", kv.get(obj).toString());
            }
            result.set(str + ".java", content);
            codeJava.add(str + ".java");
        }

        //html模板内容
        for (String str : html) {
            String content = Db.getSql("code." + str);
            @SuppressWarnings("unchecked")
            Iterator<Object> iter = kv.keySet().iterator();
            while (iter.hasNext()) {
                Object obj = iter.next();
                content = content.replace("${" + obj + "}", kv.get(obj).toString());
            }

            content = content.replace("${formCols}", formItem);
            content = content.replace("${tableCols}", tableItem);

            result.set(str + ".html", content);
            codeHtml.add(str + ".html");
        }

        // 创建model代码
        result.set("Model.java", createModelCode(record.getStr("name"), modelName));
        codeJava.add("Model.java");

        // 创建基本model模板
        result.set("BaseModel.java", createBaseModelCode(record.getStr("name"), modelName));
        codeJava.add("BaseModel.java");
        //如果是主从表模板，覆盖原来模板
        if (record.getBoolean("isSubTable")) {
            kv.set("refColumn", record.getStr("refColumn"));
            result.set("Service.java", createServiceCode(kv));
            codeJava.add("Service.java");

            result.set("Controller.java", createControllerCode(kv));
            codeJava.add("Controller.java");
        }

        result.set("codejava", codeJava);
        result.set("codehtml", codeHtml);

        return result;
    }

    /**
     * 创建Model层代码
     *
     * @param tableName
     * @param className
     * @return
     */
    public String createModelCode(String tableName, String className) {
        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = className.substring(0, className.lastIndexOf("."));
        // base model 所使用的包名
        String baseModelPackageName = modelPackageName + ".base";

        Record record = new Record();
        record.set("pageNumber", "1").set("pageSize", "1").set("name", tableName);
        @SuppressWarnings("unchecked")
        List<TableMeta> tableMetas = (List<TableMeta>) queryTablesList(record).getList();

        // 设置模板地址
        String template = "/com/cj/module/core/generator/model_template.jf";
        Kv data = Kv.by("modelPackageName", modelPackageName);
        data.set("baseModelPackageName", baseModelPackageName);
        data.set("generateDaoInModel", false);
        data.set("tableMeta", tableMetas.get(0));
        String content = engine.getTemplate(template).renderToString(data);
        return content;
    }

    /**
     * 创建BaseModel代码
     *
     * @param tableName
     * @param className
     * @return
     */
    public String createBaseModelCode(String tableName, String className) {
        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = className.substring(0, className.lastIndexOf("."));
        // base model 所使用的包名
        String baseModelPackageName = modelPackageName + ".base";

        Record record = new Record();
        record.set("pageNumber", "1").set("pageSize", "1").set("name", tableName);
        @SuppressWarnings("unchecked")
        List<TableMeta> tableMetas = (List<TableMeta>) queryTablesList(record).getList();

        // 这个是jfinal内置的
        String template = "/com/jfinal/plugin/activerecord/generator/base_model_template.jf";
        Kv data = Kv.by("baseModelPackageName", baseModelPackageName);
        data.set("generateChainSetter", true);
        data.set("tableMeta", tableMetas.get(0));
        String content = engine.getTemplate(template).renderToString(data);
        return content;
    }

    /**
     * 下载文件
     *
     * @param #type
     * @param #className
     * @param #packageName
     * @return
     */
    @SuppressWarnings("static-access")
    public void downloadFile(String type, String className, String packageName, Record record) throws IOException {
        GeneratorKit genratorKit = GeneratorKit.getInstance();
        String portal = packageName.split("\\.")[2];
        String modular = packageName.substring(packageName.indexOf(portal));
        //设置package
        genratorKit.setBasePackage(packageName.substring(0, packageName.indexOf(portal)));
        //设置模块
        genratorKit.setModular(modular.replace(".", "/"));
        List<String> listCode = record.get(type);
        String[] codeName = record.getStr("codeName").split(",");
        //后端代码
        if ("codeJava".equals(type)) {
            for (String str : codeName) {
                genratorKit.createJavaFile(className, str, record.getStr(str));
            }
        }
        //前端代码
        else if ("codeHtml".equals(type)) {
            for (String str : codeName) {
                genratorKit.createHtmlFile(className, str, record.getStr(str));
            }
        }
        //下载所有代码
        else {
            listCode = record.get("codejava");
            for (String str : listCode) {
                genratorKit.createJavaFile(className, str, record.getStr(str));
            }
            listCode = record.get("codehtml");
            for (String str : listCode) {
                genratorKit.createHtmlFile(className, str, record.getStr(str));
            }
        }

    }

    /**
     * 创建索引表代码
     *
     * @param #record
     * @param #tableItem
     * @param #subTableScript
     * @param #subTableTitle
     * @return
     */
    public String createIndexTableCode(Record record, String tableItem, String subTableScript, String subTableTitle) {
        String modelName = record.getStr("modelName");
        String authorName = record.get("authorName");
        String basePackage = record.getStr("packageName");
        String tableComment = record.getStr("tableComment");
        String primaryKey = record.getStr("primaryKey");

        //模板变量
        GeneratorKit codeKit = GeneratorKit.getInstance();
        String portal = basePackage.split("\\.")[2];
        String modular = basePackage.substring(basePackage.indexOf(portal));
        @SuppressWarnings("static-access")
        Kv kv = codeKit.setBasePackage(basePackage).setModular(modular.replace(".", "/")).getJavaKv(modelName);
        kv.set("author", authorName).set("tableComment", tableComment).set("primaryKey", primaryKey);

        //index.html模板内容
        String content = Db.getSql("code.index_subtable");
        @SuppressWarnings("unchecked")
        Iterator<Object> iter = kv.keySet().iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            content = content.replace("${" + obj + "}", kv.get(obj).toString());
        }

        content = content.replace("${subTableTitle}", subTableTitle);
        content = content.replace("${subTableScript}", subTableScript);
        content = content.replace("${tableCols}", tableItem);

        return content;
    }

    /**
     * 创建服务层代码
     *
     * @param #data
     * @return
     */
    private String createServiceCode(Kv data) {
        String template = "/com/cj/module/core/generator/service_template.jf";
        return engine.getTemplate(template).renderToString(data);
    }

    /**
     * 创建控制层代码
     *
     * @param data
     * @return
     */
    private String createControllerCode(Kv data) {
        String template = "/com/cj/module/core/generator/controller_template.jf";
        return engine.getTemplate(template).renderToString(data);
    }

    /**
     * 表项目ret
     *
     * @param #record
     * @param #modelName
     * @return
     */
    public Ret tableItemRet(Record record, String modelName) {
        Grid grid = queryTablesList(record);
        @SuppressWarnings("unchecked")
        List<TableMeta> tableList = (List<TableMeta>) grid.getList();
        List<ColumnMeta> columnMetas = tableList.get(0).columnMetas;
        Ret ret = Ret.by("columnMetas", columnMetas);
        ret.set("modelName", StrKit.firstCharToLowerCase(modelName));
        ret.set("primaryKey", tableList.get(0).primaryKey);
        ret.set("tableComment", tableList.get(0).remarks);
        return ret;
    }

    /**
     * 行数据 Json
     *
     * @param #columnMetas
     * @return
     */
    public JSONObject rowDataJson(List<ColumnMeta> columnMetas) {
        JSONObject rowData = new JSONObject();
        for (ColumnMeta column : columnMetas) {
            rowData.put(column.name, "");
        }
        return rowData;
    }
}
