package com.cj.common.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.cj.common.config.WebContant;
import com.cj.common.entity.visit.Visitor;
import com.cj.common.entity.visit.util.VisitorUtil;
import com.cj.common.file.service.FileService;
import com.cj.common.model.FileUploaded;
import com.cj.common.safe.TokenService;
import com.cj.common.util.kit.IdKit;
import com.cj.common.vo.Feedback;
import com.jfinal.aop.Aop;
import com.jfinal.core.Controller;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基于JFinal的通用controller接口进行增强
 *
 * @author THINKPAD
 */
public class BaseController extends Controller {
    private static final Logger LOG = Logger.getLogger(BaseController.class);

    FileService fileService = Aop.get(FileService.class);
    TokenService tokenService = Aop.get(TokenService.class);

    /**
     * 访问者信息
     **/
    @NotAction
    public Visitor getVisitor() {
        return VisitorUtil.getVisitor(this);
    }

    /**
     * 用户编号
     **/
    @NotAction
    public String getUserCode() {
        return getVisitor() != null ? getVisitor().getCode() : null;
    }

    /**
     * 捕获异常
     */
    @NotAction
    public void handerException(Exception e) {
        LOG.info(e.getMessage(), e);
        e.printStackTrace();
    }

    /**
     * 记录日志信息
     */
    @NotAction
    public void logInfo(String message) {
        LOG.info(message);
    }

    /**
     * 输出异常信息到页面
     */
    @NotAction
    public void setException(String message) {
        setAttr("e", new Exception(message));
    }

    /**
     * 输出提示信息到页面
     */
    @NotAction
    public void setMsg(String message) {
        setAttr("msg", message);
    }

    /**
     * 创建32位字符串
     */
    @NotAction
    public String createUUID() {
        return IdKit.createUUID();
    }

    /**
     * {
     * "code": "success",
     * "success": true,
     * "error": false,
     * "msg": "成功"
     * }
     */
    @NotAction
    public Feedback suc() {
        return Feedback.success("成功");
    }

    /**
     * {
     * "code": "success",
     * "success": true,
     * "error": false,
     * "msg": ""
     * }
     */
    @NotAction
    public Feedback suc(String msg) {
        return Feedback.success(msg);
    }

    /**
     * {
     * "code": "error",
     * "success": false,
     * "error": true,
     * "msg": "失败"
     * }
     */
    @NotAction
    public Feedback err() {
        return Feedback.error("失败");
    }

    /**
     * {
     * "code": "error",
     * "success": false,
     * "error": true,
     * "msg": ""
     * }
     */
    @NotAction
    public Feedback err(String msg) {
        return Feedback.error(msg);
    }

    /**
     * 获取数组变量ids
     */
    @NotAction
    public List<String> getIds() {
        return getArray("ids");
    }

    /**
     * 获取数组变量
     */
    @NotAction
    public List<String> getArray(String arrayName) {
        List<String> ids = new ArrayList<>();
        int i = 0;
        while (true) {
            if (getPara(arrayName + "[" + i + "]") == null) {
                break;
            }
            ids.add(getPara(arrayName + "[" + i + "]"));
            i++;
        }
        return ids;
    }

    /**
     * 保存文件记录
     */
    @NotAction
    public String saveFile(UploadFile uploadFile) {
        return fileService.saveFile(uploadFile);
    }

    @NotAction
    public String saveFile(UploadFile uploadFile, String objectId) {
        return fileService.saveFile(uploadFile, objectId);
    }

    @NotAction
    public List<String> saveFiles(List<UploadFile> list) {
        return fileService.saveFiles(list);
    }

    @NotAction
    public List<String> saveFiles(List<UploadFile> list, String objectId) {
        return fileService.saveFiles(list, objectId);
    }

    /**
     * 通过url获取文件记录对象
     */
    @NotAction
    public FileUploaded getFileUploaded(String url) {
        return fileService.queryFileUploadedByUrl(url);
    }

    @NotAction
    public FileUploaded getFileUploadedByObjectId(String objectId) {
        return fileService.queryFileUploadedByObjectId(objectId);
    }

    @NotAction
    public List<FileUploaded> getFileUploadListByObjectId(String objectId) {
        return fileService.queryFileUploadedListByObjectId(objectId);
    }

    /**
     * 删除文件
     */
    @NotAction
    public void deleteFileByUrl(String url) {
        fileService.deleteFile(url);
    }

    @NotAction
    public void deleteFileByUrls(List<String> urls) {
        fileService.deleteFiles(urls);
    }

    @NotAction
    public void deleteFileByModel(FileUploaded entity) {
        fileService.delete(entity);
    }

    @NotAction
    public void deleteFileByObjectId(String objectId) {
        fileService.deleteFileByObjectId(objectId);
    }

    /**
     * 导入数据
     */
    @NotAction
    public Boolean importExcel(UploadFile uf, String sql) {
        boolean b = fileService.importExcel(uf, sql);
        fileService.deleteFile(uf);
        return b;
    }

    /**
     * 导出数据.xlsx
     */
    @NotAction
    public void exportExcel(String[] title, String fileName, String sql) {
        fileService.exportExcelxlsx(getResponse(), title, fileName, sql);
    }

    @NotAction
    public void exportExcel(String[] title, String sql) {
        fileService.exportExcelxlsx(getResponse(), title, null, sql);
    }

    /**
     * 文件名放前面，避免误用
     */
    @NotAction
    public void exportExcel(String fileName, String[] title, String sql, Object... paras) {
        fileService.exportExcelxlsx(getResponse(), title, fileName, sql, paras);
    }

    @NotAction
    public void exportExcel(String[] title, String sql, Object... paras) {
        fileService.exportExcelxlsx(getResponse(), title, null, sql, paras);
    }

    @NotAction
    public void exportExcel(String[] title, String fileName, List<Record> list) {
        fileService.exportExcelxlsx(getResponse(), title, fileName, list);
    }

    @NotAction
    public void exportExcel(String[] title, List<Record> list) {
        fileService.exportExcelxlsx(getResponse(), title, null, list);
    }

    /**
     * 写内容到html
     */
    @NotAction
    public void writeToHtml(String fileName, String text) {
        File file = new File(PathKit.getWebRootPath() + getViewPath() + "/" + fileName);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            handerException(e);
        }
    }

    /**
     * 返回接口成功数据
     */
    @NotAction
    public Ret ok(Object data) {
        return Ret.ok("msg", "成功").set("data", data);
    }

    @NotAction
    public Ret ok() {
        return Ret.ok("msg", "成功");
    }

    @NotAction
    public Ret fail() {
        return Ret.fail("msg", "失败");
    }

    /**
     * 返回接口失败数据
     */
    @NotAction
    public Ret fail(String msg) {
        return Ret.fail("msg", msg);
    }

    /**
     * 获取请求参数,转化为JSONObject <br/>
     * 本方法不接受body传参，body传参数请使用getBodyJson()
     *
     * @return
     */
    @NotAction
    public JSONObject getAllParamsToJson() {
        JSONObject result = new JSONObject();
        Map<String, String[]> map = getParaMap();
        Set<String> keySet = map.keySet();
        for (String key : keySet) {
            if (map.get(key) instanceof String[]) {
                String[] value = map.get(key);
                if (value.length == 0) {
                    result.put(key, null);

                } else if (value.length == 1 && key.indexOf("[]") < 0) {
                    result.put(key, value[0]);
                } else {
                    result.put(key, value);
                }
            } else {
                result.put(key, map.get(key));
            }
        }
        return result;
    }

    /**
     * 获取请求参数,转化为JFinal的Record对象 ,<br/>
     * 本方法不接受body传参，body中的参数请使用getBodyJson()
     *
     * @return
     */
    @NotAction
    public Record getAllParamsToRecord() {
        @SuppressWarnings("unchecked")
        Record result = new Record().setColumns(getKv());
        result.remove("_jfinal_token");
        return result;
    }

    /**
     * 构造Kv对象
     */
    @NotAction
    public Kv byKv(Object key, Object value) {
        return Kv.by(key, value);
    }

    /**
     * 创建token
     */
    @Override
    @NotAction
    public String createToken() {
        tokenService.createToken(this);
        return null;
    }

    /**
     * 获取http请求body参数
     */
    public JSONObject getBodyJson() {
        String body = getRawData();
        JSONObject bodyJson = (JSONObject) JSONObject.parseObject(body);
        return bodyJson;
    }

    /**
     * 获取上传文件web地址,temp/upload/20120500/20120500263200001.jpg
     */
    @NotAction
    public String getUploadFilePath(FileUploaded fileUpload) {
        if (fileUpload != null) {
            String path = fileUpload.getSavePath();
            return path.substring(path.indexOf(WebContant.BASE_UPLOAD_PATH));
        }
        return null;
    }

    /**
     * 获取上传文件web地址,temp/upload/20120500/20120500263200001.jpg
     */
    @NotAction
    public String getUploadFilePath(String objectId) {
        FileUploaded fileUpload = getFileUploadedByObjectId(objectId);
        return getUploadFilePath(fileUpload);
    }

    /**
     * 获取上传文件web地址,https://www.qinhaisenlin.com/temp/upload/20120500/20120500263200001.jpg
     */
    @NotAction
    public String getUploadFilePathWidthHttps(String objectId) {
        FileUploaded fileUpload = getFileUploadedByObjectId(objectId);
        return getUploadFilePathWidthHttps(fileUpload);
    }

    /**
     * 获取上传文件web地址,https://www.qinhaisenlin.com/temp/upload/20120500/20120500263200001.jpg
     */
    @NotAction
    public String getUploadFilePathWidthHttps(FileUploaded fileUpload) {
        if (fileUpload != null) {
            return WebContant.HTTPS + getUploadFilePath(fileUpload);
        }
        return null;
    }
}
