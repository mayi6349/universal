package cn.simple.kwC;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.weixin.sdk.jfinal.ApiController;

import net.sf.json.JSONObject;

/**
 * 基本的api 基于jfinal controller做一些封装
 * 
 * @author may
 * @param <T>
 */
public class BaseAPIController extends Controller {

	public static JSONObject params;

	public static double apiV = 0;

	@SuppressWarnings("static-access")
	public void setParams(JSONObject params) {
		this.params = params;
	}

	@Clear
	public void index() {
		render("/api.html");
	}

	/**
	 * 响应正常处理
	 * 
	 * @param data
	 * @param message
	 */
	public void renderSuccess(Object data, String message) {
		JSONObject result = new JSONObject();
		result.put("status", "0");
		result.put("data", data);
		result.put("info", message);
		renderJson(result.toString());
	}

	/**
	 * 响应无效请求
	 * 
	 * @param data
	 * @param message
	 */
	public void renderError(String message) {
		JSONObject result = new JSONObject();
		result.put("status", "1");
		result.put("info", message);
		renderJson(result.toString());
	}
}
