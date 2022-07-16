package com.cj.common.config;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;


/**
 * 系统配置常量
 *
 * @author THINKPAD
 */
@SuppressWarnings("all")
public interface WebContant {

    Prop prop = PropKit.getProp();
    /**
     * 视图基础目录
     **/
    String BASE_VIEW_PATH = "/WEB-INF/views";
    /**
     * 项目名称
     **/
    String PROJECT_NAME = prop.get("projectName", "JFinal极速开发世界");
    /**
     * 上传目录
     **/
    String BASE_UPLOAD_PATH = prop.get("baseUploadPath", "WEB-INF/temp/upload");
    /**
     * 下载目录
     **/
    String BASE_DOWNLOAD_PATH = prop.get("baseDownloadPath", "WEB-INF/temp/download");
    /**
     * 上传最大限制
     **/
    Integer MAX_POST_SIZE = 1024 * 1024 * 500;
    /**
     * 允许登录
     **/
    Integer ALLOW_LOGIN = 0;
    /**
     * 是否开启登陆锁定功能
     */
    Integer IS_LOCK_USER = 1;
    /**
     * 登陆失败次数，将锁定账号
     */
    Integer FAIL_NUM = 5;
    /**
     * 错误页面
     **/
    String ERROR403_VIEW = BASE_VIEW_PATH + "/common/error/403.html";
    String ERROR404_VIEW = BASE_VIEW_PATH + "/common/error/404.html";
    String ERROR500_VIEW = BASE_VIEW_PATH + "/common/error/500.html";
    /**
     * 前端函数模板
     **/
    String FUNCTION_TEMP = BASE_VIEW_PATH + "/common/templete/_layout.html";
    /**
     * sql模板
     **/
    String SQL_TEMPLATE = "/sql/all_sqls.sql";
    /**
     * 代码模板
     **/
    String CODE_TEMPLATE = "/code/_all_code.sql";
    /**
     * 自动扫描的Controller、Model所在的包，仅扫描该包及其子包下面的路由,默认com.cj
     */
    String SCAN_PACKAGE_NAME = prop.get("scanPackageName", "com.cj");
    /**
     * 允许上传文件类型
     */
    String ALLOW_UPLOAD_FILE = ".jpg .png .ico .gif .jpeg .xls .xlsx .doc .docx .pdf .ppt .pptx .txt .zip .rar .html .css .mp3 .mp4 .avi .wmv";
    /**
     * 服务器地址
     */
    String HTTPS = prop.get("https", "https://www.??????.com/");
}
