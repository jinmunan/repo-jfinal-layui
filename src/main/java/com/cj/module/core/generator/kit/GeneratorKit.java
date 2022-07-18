package com.cj.module.core.generator.kit;

import com.jfinal.kit.Kv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 代码生成器
 *
 * @author THINKPAD
 */
public class GeneratorKit {
    private static final String AUTHOR = "jinmunan";
    private static final String POINT = ".";
    private static final String SEPARATOR = "/";
    private static final String JAVA_RESOURCE = "src/main/java";
    private static final String HTML_RESOURCE = "src/main/webapp/WEB-INF/views";
    private static final String CONTROLLER = "controller";
    private static final String SERVICE = "service";

    private static String modular = "business";
    private static String basePackage = "com.cj";
    private static String controllerPackage = "";
    private static String servicePackage = "";
    private static String controllerPath = "";
    private static String servicePath = "";
    private static String modelPath = "";
    private static String baseModelPath = "";

    /**
     * 模板参数集
     */
    private static Kv kv = Kv.by("author", AUTHOR);

    public GeneratorKit() {
    }

    private static GeneratorKit single = new GeneratorKit();

    public static GeneratorKit getInstance() {
        return single;
    }

    /**
     * 设置基础包
     */
    public GeneratorKit setBasePackage(String basePackage) {
        // com.cj.module.core.portal.business
        GeneratorKit.basePackage = basePackage;
        return this;
    }

    /**
     * 设置模块包
     */
    public GeneratorKit setModular(String modular) {
        // business
        GeneratorKit.modular = modular;
        return this;
    }

    /**
     * 获得模块
     */
    public String getModular() {
        return modular;
    }

    /**
     * 获取Java模板参数集
     */
    public static Kv getJavaKv(String importModel) {
        // com.xxx.xxx modelName = xxx
        String modelName = getLastChar(importModel);
        // com.cj.module.core.portal.business.controller
        controllerPackage = basePackage + POINT + CONTROLLER;
        // com.cj.module.core.portal.business.service
        servicePackage = basePackage + POINT + SERVICE;
        kv.set("controllerPackage", controllerPackage);
        kv.set("servicePackage", servicePackage);
        kv.set("modelName", modelName);
        kv.set("lowercaseModelName", getLowercaseChar(modelName));
        kv.set("importModel", importModel);
        // /business/
        kv.set("actionKey", SEPARATOR + modular + SEPARATOR + getLowercaseChar(modelName));
        kv.set("date", getDate());
        return kv;
    }

    /**
     * 创建java文件
     */
    public static File createJavaFile(String modelClassName, String fileType, String content) throws IOException {
        String modelName = getLastChar(modelClassName);

        String modelPackage = modelClassName.substring(0, modelClassName.lastIndexOf(POINT));
        //controller类
        controllerPath = basePackage.replace(POINT, SEPARATOR) + SEPARATOR + modular + SEPARATOR + CONTROLLER;
        //service类
        servicePath = basePackage.replace(POINT, SEPARATOR) + SEPARATOR + modular + SEPARATOR + SERVICE;
        //model类
        modelPath = modelPackage.replace(POINT, SEPARATOR);
        //baseModel类
        baseModelPath = modelPackage.replace(POINT, SEPARATOR) + SEPARATOR + "base";

        String filePath = System.getProperty("user.dir") + SEPARATOR + JAVA_RESOURCE + SEPARATOR;
        String fileName = modelName + fileType;
        if ("Service.java".equalsIgnoreCase(fileType)) {
            filePath += servicePath + SEPARATOR;
        } else if ("Controller.java".equalsIgnoreCase(fileType)) {
            filePath += controllerPath + SEPARATOR;
        } else if ("Model.java".equals(fileType)) {
            filePath += modelPath + SEPARATOR;
            fileName = modelName + ".java";
        } else if ("BaseModel.java".equals(fileType)) {
            filePath += baseModelPath + SEPARATOR;
            fileName = "Base" + modelName + ".java";
        } else if ("_MappingKit.java".equals(fileType)) {
            filePath += modelPath + SEPARATOR;
            fileName = fileType;
        } else {
            filePath += fileType + SEPARATOR;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(filePath + fileName);
        writeContentToFile(file, content);
        return file;
    }

    /**
     * 创建html文件
     */
    public static File createHtmlFile(String className, String fName, String content) throws IOException {
        // 获取模型名称model
        String cName = getLastChar(className);
        String filePath = System.getProperty("user.dir") + SEPARATOR + HTML_RESOURCE + SEPARATOR + modular + SEPARATOR + getLowercaseChar(cName) + SEPARATOR;

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(filePath + fName);
        writeContentToFile(file, content);
        return file;
    }


    /**
     * 写内容入文件
     */
    public static void writeContentToFile(File file, String content) throws IOException {
        System.out.print("正在创建文件：" + file.getPath());
        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            out.write(content);
            System.out.println("   ：创建成功");
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    /**
     * 获取路径的最后面字符串
     */
    private static String getLastChar(String str) {
        if ((str != null) && (str.length() > 0)) {
            int dot = str.lastIndexOf('.');
            if ((dot > -1) && (dot < (str.length() - 1))) {
                return str.substring(dot + 1);
            }
        }
        return str;
    }

    /**
     * 把第一个字母变为小写
     */
    private static String getLowercaseChar(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 获取系统时间
     */
    private static String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }
}
