package cn.simple.kwP.config;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import cn.simple.kwA.factory.LinkedHashMapContainerFactory;
import cn.simple.kwA.model._MappingKit;
import cn.simple.kwA.plugin.IocInterceptor;
import cn.simple.kwA.plugin.SpringPlugin;
import cn.simple.kwA.utils.ToolSqlXml;
import cn.simple.kwP.controller.SysController;

/**
 * @author May
 */
public class APPConfig extends JFinalConfig {

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getApiConfig() {
		ApiConfig ac = new ApiConfig();

		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("wechat.Token"));
		ac.setAppId(PropKit.get("wechat.AppId"));
		ac.setAppSecret(PropKit.get("wechat.AppSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(false);
		ac.setEncodingAesKey(PropKit.get("wechat.EncodingAesKey"));
		return ac;
	}

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("resources.properties");
		me.setDevMode(PropKit.getBoolean("devMode"));
		me.setEncoding("utf-8");
		me.setViewType(ViewType.JSP);
		// 设置上传文件保存的路径
		me.setBaseUploadPath(PathKit.getWebRootPath() + File.separator + "myupload");
		// ApiConfigKit 设为开发模式可以在开发阶段输出请求交互的 xml 与 json 数据
		ApiConfigKit.setDevMode(me.getDevMode());
		ApiConfigKit.putApiConfig(getApiConfig());

		ToolSqlXml.init(true, APPConfig.class.getClassLoader().getResource("").getFile());
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		// 微信支付回调
		me.add("/pay",SysController.class);
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 启动Spring插件
		me.add(new SpringPlugin("classpath:applicationContext.xml"));

		Boolean devMode = PropKit.getBoolean("devMode");

		// 客户端
		// 初始化连接池插件 DruidPlugin
		DruidPlugin dp = createDruidPlugin(PropKit.get("connection.jdbcUrl"), PropKit.get("connection.user"),
				PropKit.get("connection.password"));
		me.add(dp);
		// ActiveRecordPlugin
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		// 根据sql顺序显示字段
		arp.setContainerFactory(new LinkedHashMapContainerFactory());
		arp.setShowSql(devMode);
		_MappingKit.mapping(arp);
		me.add(arp);

		SqlReporter.setLog(devMode);
		// redis插件
//		me.add(new RedisPlugin(PropKit.get("redis.name"), PropKit.get("redis.server"), PropKit.get("redis.no")));

		// dubbo
		SpringPlugin spDubbo = new SpringPlugin("classpath:applicationContext.xml");
		me.add(spDubbo);
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		me.add(new IocInterceptor());
	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {
		// Druid监控
		DruidStatViewHandler dvh = new DruidStatViewHandler("/druid", new IDruidStatViewAuth() {

			@Override
			public boolean isPermitted(HttpServletRequest request) {
				return true;
			}
		});
		me.add(dvh);
	}

	@Override
	public void afterJFinalStart() {
		System.out.println("*****kw Provider start*****");

	}

	@Override
	public void beforeJFinalStop() {
		System.out.println("*****kw Provider end*****");
	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main
	 * 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8081, "/", 5);// 启动配置项
	}

	@Override
	public void configEngine(Engine arg0) {

	}

	private DruidPlugin createDruidPlugin(String jdbcUrl, String user, String password) {
		// 初始化连接池插件 DruidPlugin
		DruidPlugin dp = new DruidPlugin(jdbcUrl, user, password);

		Slf4jLogFilter sql_log_filter = new Slf4jLogFilter();
		sql_log_filter.setConnectionLogEnabled(false);
		sql_log_filter.setStatementLogEnabled(false);
		sql_log_filter.setStatementExecutableSqlLogEnable(true);
		sql_log_filter.setResultSetLogEnabled(false);
		dp.addFilter(sql_log_filter);

		// 慢sql记录
		StatFilter sql_stat_filter = new StatFilter();
		sql_stat_filter.setSlowSqlMillis(3 * 1000);
		sql_stat_filter.setLogSlowSql(true);
		dp.addFilter(sql_stat_filter);

		WallFilter wall = new WallFilter();
		wall.setDbType(JdbcConstants.MYSQL);
		WallConfig config = new WallConfig();
		config.setFunctionCheck(false); // 支持数据库函数
		wall.setConfig(config);

		dp.addFilter(wall);
		dp.setFilters("stat,wall");
		dp.setInitialSize(10).setMaxActive(50);
		dp.setTestWhileIdle(true);
		dp.setTestOnBorrow(true);
		dp.setTestOnReturn(true);
		dp.setMaxPoolPreparedStatementPerConnectionSize(20);
		return dp;
	}
}
