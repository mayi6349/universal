package cn.simple.kwC.config;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.druid.DruidStatViewHandler;
import com.jfinal.plugin.druid.IDruidStatViewAuth;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

import cn.simple.kwA.plugin.IocInterceptor;
import cn.simple.kwA.plugin.SpringPlugin;
import cn.simple.kwC.BaseAPIController;
import cn.simple.kwC.controller.GameController;
import cn.simple.kwC.controller.GroupGameOrderController;
import cn.simple.kwC.controller.UserController;
import cn.simple.kwC.controller.UserGameOrderController;
import cn.simple.kwC.router.APIRouter;

/**
 * @author May
 */
public class APPConfig extends JFinalConfig {

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
//		ApiConfigKit.setDevMode(me.getDevMode());
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		// 微信
		// 初始
		me.add("/",BaseAPIController.class);
		me.add(new APIRouter());
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		// 启动Spring插件
		me.add(new SpringPlugin("classpath:applicationContext.xml"));
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
		System.out.println("*****kw Consumer start*****");

	}

	@Override
	public void beforeJFinalStop() {
		System.out.println("*****kw Consumer end*****");
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

}
