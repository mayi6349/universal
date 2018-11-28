package cn.simple.kw.config;

import java.io.File;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.wxaapp.WxaConfig;
import com.jfinal.wxaapp.WxaConfigKit;

import cn.simple.kw.BaseAPIController;
import cn.simple.kw.controller.ActivityController;
import cn.simple.kw.controller.BountyController;
import cn.simple.kw.controller.HomeController;
import cn.simple.kw.controller.SysController;
import cn.simple.kw.controller.UserController;
import cn.simple.kw.model._MappingKit;
import cn.simple.kw.service.thread.GameCreateThread;
import cn.simple.kw.service.thread.GameEnrollThread;
import cn.simple.kw.service.thread.GameOrderWindUpThread;
import cn.simple.kw.service.thread.GroupNoSetThread;
import cn.simple.kw.service.thread.StepReportThread;
import cn.simple.kw.validator.ParamsValidator;
import cn.simple.kwA.factory.LinkedHashMapContainerFactory;
import cn.simple.kwA.ioc.AutowiredInterceptor;
import cn.simple.kwA.ioc.Ioc;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwA.utils.ToolSqlXml;

/**
 * @author May
 */
public class APPConfig extends JFinalConfig {

	// 线程处理
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(15);
	private boolean threadFlg;

	/**
	 * 如果要支持多公众账号，只需要在此返回各个公众号对应的 ApiConfig 对象即可 可以通过在请求 url 中挂参数来动态从数据库中获取
	 * ApiConfig 属性值
	 */
	public ApiConfig getWxApiConfig() {
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
		// ApiConfigKit.setDevMode(me.getDevMode());
		// ApiConfigKit.putApiConfig(getWxApiConfig());
		WxaConfig wxa = new WxaConfig();
		wxa.setAppId(PropKit.get("weapp.AppId"));
		wxa.setAppSecret(PropKit.get("weapp.AppSecret"));
		WxaConfigKit.setDevMode(false);
		WxaConfigKit.setWxaConfig(wxa);
		threadFlg = PropKit.getBoolean("threadFlg");
		ToolSqlXml.init(true, APPConfig.class.getClassLoader().getResource("").getFile());
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		me.add("/", BaseAPIController.class);
		me.add("/home", HomeController.class);
		me.add("/bounty", BountyController.class);
		me.add("/user", UserController.class);
		me.add("/activity", ActivityController.class);
		me.add("/sys", SysController.class);
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {

		Boolean devMode = PropKit.getBoolean("devMode");

		// IOC注入
		Ioc ioc = Ioc.getIoc();
		me.add(ioc);
		// 自动扫描路径下有@Server注解的类，后一个参数为true时为single模式
		ioc.addPackage("cn.simple.kw.service", true);

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
//		// redis插件
//		me.add(new RedisPlugin(PropKit.get("redis.name"), PropKit.get("redis.server"), PropKit.getInt("redis.no")));
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {
		// IOC拦截器
		me.add(new AutowiredInterceptor());
		me.add(new ParamsValidator());
		// me.add(new IocInterceptor());
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
		if (threadFlg) {
			System.out.println("*****inpark thead start*****");
			GameCreateThread gameCreateThread = new GameCreateThread();
			scheduler.scheduleWithFixedDelay(gameCreateThread, DateUtils.getInitDelay("00:00:01"), 24 * 60 * 60 * 1000,
					TimeUnit.MILLISECONDS);

			GameOrderWindUpThread gameOrderWindUpThread = new GameOrderWindUpThread();
			// scheduler.scheduleWithFixedDelay(gameOrderWindUpThread, 0, 24 *
			// 60 * 60 * 1000, TimeUnit.MILLISECONDS);
			scheduler.scheduleWithFixedDelay(gameOrderWindUpThread, DateUtils.getInitDelay("00:00:01"),
					24 * 60 * 60 * 1000, TimeUnit.MILLISECONDS);

			StepReportThread stepReportThread = new StepReportThread();
			// scheduler.scheduleWithFixedDelay(stepReportThread, 0, 6 * 1000,
			// TimeUnit.MILLISECONDS);
			scheduler.scheduleWithFixedDelay(stepReportThread, 0, 78 * 60 * 1000, TimeUnit.MILLISECONDS);

			GameEnrollThread gameEnrollThread = new GameEnrollThread();
			// scheduler.scheduleWithFixedDelay(gameEnrollThread, 0, 6 * 1000,
			// TimeUnit.MILLISECONDS);
			scheduler.scheduleWithFixedDelay(gameEnrollThread, 0, 99* 60 * 1000, TimeUnit.MILLISECONDS);

			GroupNoSetThread groupNoSetThread = new GroupNoSetThread();
			// scheduler.scheduleWithFixedDelay(groupNoSetThread, 0, 24 * 60 *
			// 60 * 1000, TimeUnit.MILLISECONDS);
			scheduler.scheduleWithFixedDelay(groupNoSetThread, DateUtils.getInitDelay("00:00:01"), 24 * 60 * 60 * 1000,
					TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void beforeJFinalStop() {
		System.out.println("*****kw Provider end*****");
		scheduler.shutdownNow();
	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main
	 * 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("src/main/webapp", 8080, "/kw", 5);// 启动配置项
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
