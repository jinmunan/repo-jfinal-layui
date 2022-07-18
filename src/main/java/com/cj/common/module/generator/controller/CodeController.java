package com.cj.common.module.generator.controller;

import com.cj.common.base.controller.BaseController;
import com.cj.common.module.generator.service.CodeService;
import com.cj.common.vo.Grid;
import com.jfinal.aop.Inject;
import com.jfinal.core.Path;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.generator.ColumnMeta;
import com.jfinal.plugin.activerecord.generator.TableMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 代码生成器
 *
 * @author THINKPAD
 */
@Path("/portal/generator/code")
public class CodeController extends BaseController {

    @Inject
    CodeService codeService;

    /**
     * 渲染代码器首页
     */
    public void index() {
        render("index.html");
        /*如果有参数*/
        if (getPara(0) != null) {
            render("index2.html");
        }
    }

    /**
     * 渲染生成表页面
     */
    public void tables() {
        render("tables.html");
    }

    /**
     * 数据库表列表
     * 包含模糊查询
     */
    public void tablesList() {
        // 查询表列表 json 需要配置模板
        renderJson(codeService.queryTablesList(getAllParamsToRecord()));
    }

    /**
     * 生成代码
     *
     */
    public void createCode() {
        Record record = getAllParamsToRecord();
        // 参数
        String[] tableNameArray = getPara("name").split(",");
        String modelPackage = getPara("packageName") + ".model.";
        String[] modelNameArray = getPara("modelName").split(",");
        List<Record> codeList = new ArrayList<>();
        List<String> modelList = new ArrayList<>();
        for (int i = 0; i < tableNameArray.length; i++) {
            // 查询表信息
            record.set("name", tableNameArray[i] + "=");
            Grid grid = codeService.queryTablesList(record);
            @SuppressWarnings("unchecked")
            List<TableMeta> tableList = (List<TableMeta>) grid.getList();
            List<ColumnMeta> columnMetas = tableList.get(0).columnMetas;

            // 用模板引擎生成 HTML 片段 replyItem
            Ret ret = Ret.by("columnMetas", columnMetas);
            ret.set("modelName", StrKit.firstCharToLowerCase(modelNameArray[i]));
            ret.set("primaryKey", tableList.get(0).primaryKey);
            String formItem = renderToString("temp/_form.html", ret);
            String tableItem = renderToString("temp/_table.html", ret);

            // 创建模板内容
            record.set("modelName", modelPackage + modelNameArray[i]);
            record.set("primaryKey", tableList.get(0).primaryKey);
            record.set("tableComment", tableList.get(0).remarks);
            record.set("tableNames", tableNameArray);
            record.set("isSubTable", false);
            Record codeRecord = codeService.createCodeTemplete(record, formItem, tableItem);
            codeList.add(codeRecord);
            modelList.add(modelPackage + modelNameArray[i]);
        }

        // 存储数据，用于创建本地文件
        setSessionAttr("downloadCode", codeList);
        setSessionAttr("modelList", modelList);
        setSessionAttr("packageName", getPara("packageName"));
        renderJson(Ret.ok("data", codeList));
    }
//
//    /**
//     * 下载代码
//     */
//    public void download() {
//        List<Record> codeList = getSessionAttr("downloadCode");
//        String packageName = getSessionAttr("packageName");
//        List<String> modelList = getSessionAttr("modelList");
//        String codeName = getPara("codeName");
//        try {
//            for (int i = 0; i < codeList.size(); i++) {
//                codeService.downloadFile(getPara("type"), modelList.get(i), packageName, codeList.get(i).set("codeName", codeName));
//            }
//            renderJson(ok());
//        } catch (IOException e) {
//            handerException(e);
//            renderJson(fail());
//        }
//    }
//
//    public void createSubTableCode() {
//        Record record = getAllParamsToRecord();
//        String basePackage = getPara("packageName");
//        String modelPackage = basePackage + ".model.";
//        List<Record> codeList = new ArrayList<>();
//        List<String> modelList = new ArrayList<>();
//        //主表,只支持单一主表
//        String tableName = getPara("tableName").split(",")[0];
//        String modelName = getPara("modelName").split(",")[0];
//        String tableColumn = getPara("tableColumn");
//        record.set("name", tableName);
//        Ret ret = codeService.tableItemRet(record, modelName);
//        String formItem = renderToString("temp/_form.html", ret);
//        String tableItem = renderToString("temp/_table.html", ret);
//
//        // 创建模板内容
//        record.set("modelName", modelPackage + modelName);
//        record.set("primaryKey", ret.get("primaryKey"));
//        record.set("tableComment", ret.get("tableComment"));
//        record.set("isSubTable", false);
//        Record codeRecord = codeService.createCodeTemplete(record, formItem, tableItem);
//        codeList.add(codeRecord);
//        modelList.add(modelPackage + modelName);
//
//        //子表
//        String[] subTableNameArray = getPara("subTableName").split(",");
//        String[] subModelNameArray = getPara("subModelName").split(",");
//        String[] subTableColumnArray = getPara("subTableColumn").split(",");
//        StringBuffer subTableTitle = new StringBuffer();
//        List<Record> subTableList = new ArrayList<>();
//        StringBuffer renderSubTable = new StringBuffer();
//        for (int i = 0; i < subTableNameArray.length; i++) {
//            record.set("name", subTableNameArray[i]);
//            record.set("modelName", subModelNameArray[i]);
//            record.set("basePackage", basePackage);
//            record.set("refColumn", subTableColumnArray[i]);
//            Record subTable = subRecord(record, subTableTitle, codeList, modelList);
//            subTableList.add(subTable);
//            //页面调用子表js方法
//            renderSubTable.append("renderSubTable").append(i + 1).append("(refId);\r\n	");
//        }
//
//        //主表index模板
//        String subTableScript = renderToString("temp/_subtable.html", Ret.by("subTableList", subTableList));
//        record.set("modelName", modelPackage + modelName);
//        String indexSubTable = codeService.createIndexTableCode(record, tableItem, subTableScript, subTableTitle.toString());
//        indexSubTable = indexSubTable.replace("${refId}", tableColumn);
//        indexSubTable = indexSubTable.replace("${renderSubTable}", renderSubTable.toString());
//        codeRecord.set("index.html", indexSubTable);
//
//        // 存储数据，用于创建本地文件
//        setSessionAttr("downloadCode", codeList);
//        setSessionAttr("modelList", modelList);
//        setSessionAttr("packageName", getPara("packageName"));
//        renderJson(Ret.ok("data", codeList));
//    }
//
//    /**
//     * 子表模板
//     *
//     * @param record
//     * @param subTableTitle
//     * @param codeList
//     * @param modelList
//     * @return
//     */
//    private Record subRecord(Record record, StringBuffer subTableTitle, List<Record> codeList, List<String> modelList) {
//        String modelName = record.getStr("modelName");
//        String basePackage = record.getStr("basePackage");
//        Ret itemRet = codeService.tableItemRet(record, modelName);
//        if (subTableTitle.length() > 0)
//            subTableTitle.append(",");
//        String tableComment = itemRet.getStr("tableComment");
//        subTableTitle.append("'")
//                .append(tableComment.equals("") ? "Tab" : tableComment)
//                .append("'");
//        itemRet.set("isEdit", true);
//        String formItem = renderToString("temp/_form.html", itemRet);
//        String tableItem = renderToString("temp/_table.html", itemRet);
//        // 创建模板内容
//        String modelPackage = basePackage + ".model.";
//        record.set("modelName", modelPackage + modelName);
//        record.set("primaryKey", itemRet.get("primaryKey"));
//        //这里从表的注释会覆盖主表注释，导致主表弹窗显示从表名称，所以注释掉
//        //record.set("tableComment", itemRet.get("tableComment"));
//        record.set("isSubTable", true);
//        Record codeRecord = codeService.createCodeTemplete(record, formItem, tableItem);
//        codeList.add(codeRecord);
//        modelList.add(modelPackage + modelName);
//
//        Record subTable = new Record();
//        subTable.set("tableCols", tableItem);
//        subTable.set("rowData", codeService.rowDataJson(itemRet.getAs("columnMetas")));
//        String portal = basePackage.split("\\.")[2];
//        String modular = basePackage.substring(basePackage.indexOf(portal));
//        String actionKey = "/" + modular.replace(".", "/") + "/" + StrKit.firstCharToLowerCase(modelName);
//        subTable.set("actionKey", actionKey);
//        return subTable;
//    }
}
