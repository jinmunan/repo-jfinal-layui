
package com.cj.common.config;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.cj.common.directive.MyNowDirective;
import com.cj.common.filter.MyDruidFilter;
import com.cj.common.interceptor.ExceptionInterceptor;
import com.cj.common.interceptor.SessionInterceptor;
import com.cj.common.interceptor.TokenInterceptor;
import com.cj.common.model._MappingKit;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.handler.UrlSkipHandler;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.json.FastJsonFactory;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;


/**
 * @author THINKPAD
 */
public class MainConfig extends JFinalConfig {

    private static Prop prop;

    /**
     * 先加载开发环境配置，再追加生产环境的少量配置覆盖掉开发环境配置
     */
    static void loadConfig() {
        if (prop == null) {
            prop = PropKit.use("config-dev.txt").appendIfExists("config-pro.txt");
        }
    }

    /**
     * 运行main方法启动项目
     */
    public static void main(String[] args) {
        // 加载配置文件
        loadConfig();
        // 启动服务器
        UndertowServer.start(MainConfig.class);
    }

    /**
     * 配置JFinal常量
     */
    @Override
    public void configConstant(Constants me) {
        // 加载配置文件
        loadConfig();
        // 设置当前是否为开发模式
        me.setDevMode(prop.getBoolean("devMode"));
        // 设置默认上传文件保存路径 getFile等使用
//        me.setBaseUploadPath(WebContant.BASE_UPLOAD_PATH);
        // 设置默认下载文件路径 renderFile使用
//        me.setBaseDownloadPath(WebContant.BASE_DOWNLOAD_PATH);
        // 设置error渲染视图
        me.setError403View(WebContant.ERROR403_VIEW);
        me.setError404View(WebContant.ERROR404_VIEW);
        me.setError500View(WebContant.ERROR500_VIEW);
        // 设置 Json 转换工厂实现类
        me.setJsonFactory(FastJsonFactory.me());
        // 开启依赖注入
        me.setInjectDependency(true);
        // 附件上传大小设置100M
//        me.setMaxPostSize(WebContant.MAX_POST_SIZE);
    }

    /**
     * 配置JFinal路由映射
     */
    @Override
    public void configRoute(Routes me) {
        me.scan(WebContant.SCAN_PACKAGE_NAME);
        me.setBaseViewPath(WebContant.BASE_VIEW_PATH);
    }

    /**
     * 获取数据库插件
     * 抽取成独立的方法，便于重用该方法，减少代码冗余
     */
    public static DruidPlugin createDruidPlugin() {
        loadConfig();
        return new DruidPlugin(prop.get("jdbcUrl"), prop.get("user"), prop.get("password").trim());
    }

    /**
     * 配置JFinal插件 数据库连接池 ORM 缓存等插件 自定义插件
     */
    @Override
    public void configPlugin(Plugins me) {
        // 配置数据库连接池插件
        DruidPlugin dbPlugin = createDruidPlugin();
        /** 配置druid监控 **/
        dbPlugin.addFilter(new StatFilter());
        WallFilter wall = new WallFilter();
        wall.setDbType(prop.get("dbType"));
        dbPlugin.addFilter(wall);

        // orm映射 配置ActiveRecord插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dbPlugin);
        //sql模板
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate(WebContant.SQL_TEMPLATE);
        // 代码器模板
        arp.addSqlTemplate(WebContant.CODE_TEMPLATE);

        //处理SQL参数
        dbPlugin.addFilter(new MyDruidFilter());

        // mysql
        arp.setDialect(new MysqlDialect());

        /**
         * 在此添加数据库 表-Model 映射
         * */
        _MappingKit.mapping(arp);
        // 添加到插件列表中
        me.add(dbPlugin);
        me.add(arp);

        // 配置缓存插件
        me.add(new EhCachePlugin());
    }

    /**
     * 配置全局拦截器
     */
    @Override
    public void configInterceptor(Interceptors me) {
//		me.addGlobalActionInterceptor(new SessionInViewInterceptor());
        // 1.重定向到登录页面 2.
        me.addGlobalActionInterceptor(new SessionInterceptor());
        // 异常拦截器
        me.addGlobalActionInterceptor(new ExceptionInterceptor());
        // 生成和校验token
        me.addGlobalActionInterceptor(new TokenInterceptor());
//		me.addGlobalActionInterceptor(new LoggerInterceptor());
    }

    /**
     * 配置全局处理器
     */
    @Override
    public void configHandler(Handlers me) {
//		/** 配置druid监控 **/
//		me.add(DruidKit.getDruidStatViewHandler());
//		// 路由处理
//		me.add(new CommonHandler());
//		// XSS过滤
//		me.add(new XssHandler("^\\/portal/form/view.*"));
//		// 放开/ureport/开头的请求
//		me.add(new UrlSkipHandler("^\\/ureport.*", true));
    }

    /**
     * 配置模板引擎
     */
    @Override
    public void configEngine(Engine me) {
        // 这里只有选择JFinal TPL的时候才用
        me.setDevMode(prop.getBoolean("engineDevMode"));
        // 当前时间指令
        me.addDirective("now", MyNowDirective.class);
        // 项目根路径
        me.addSharedObject("path", JFinal.me().getContextPath());
        // 项目名称
        me.addSharedObject("projectName", prop.get("projectName"));
        // 项目版权
        me.addSharedObject("copyright", prop.get("copyright"));
        // 配置共享函数模板
        me.addSharedFunction(WebContant.FUNCTION_TEMP);
//		// 附件在线预览服务
//		me.addSharedObject("onlinePreview", p.getBoolean("onlinePreview"));
//		me.addSharedObject("onlinePreviewUrl", p.get("onlinePreviewUrl"));
//		//允许上传文件类型
//		me.addSharedObject("allowUploadFile", WebContant.allowUploadFile);
//		//用于前端调用静态方法，创建id
//		me.addSharedStaticMethod(IdKit.class);
    }
}
