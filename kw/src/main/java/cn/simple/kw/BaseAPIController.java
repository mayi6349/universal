package cn.simple.kw;

import java.math.BigDecimal;
import java.util.Iterator;

import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import net.sf.json.JSONArray;
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
		renderJson(keyToCamelCase(result));
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

	/**
	 * json大写转小写
	 * 
	 * @param jSONArray1
	 *            jSONArray1
	 * @return JSONObject
	 */
	public static JSONObject keyToCamelCase(JSONObject json) {
		JSONObject json1 = new JSONObject();
		Iterator<?> it = json.keys();
		String type = "";
		while (it.hasNext()) {
			String key = (String) it.next();
			Object object = json.get(key);
			type = object.getClass().toString();
			if (type.endsWith("JSONObject")) {
				json1.accumulate(StrKit.toCamelCase(key), keyToCamelCase(JSONObject.fromObject(object)));
			} else if (type.endsWith("JSONArray")) {
				json1.accumulate(StrKit.toCamelCase(key), transToArray(json.getJSONArray(key)));
			} else if (type.endsWith("Double") || type.endsWith("BigDecimal")) {
				json1.accumulate(StrKit.toCamelCase(key),
						String.valueOf(new BigDecimal(object.toString()).setScale(2)));
			} else {
				json1.accumulate(StrKit.toCamelCase(key), object);
			}
		}
		return json1;
	}

	/**
	 * jsonArray转jsonArray
	 * 
	 * @param jSONArray1
	 *            jSONArray1
	 * @return JSONArray
	 */
	public static JSONArray transToArray(JSONArray jSONArray1) {
		JSONArray jSONArray2 = new JSONArray();
		String type = "";
		for (int i = 0; i < jSONArray1.size(); i++) {
			Object object = jSONArray1.get(i);
			type = object.getClass().toString();
			if (type.endsWith("JSONObject")) {
				jSONArray2.add(keyToCamelCase(JSONObject.fromObject(object)));
			} else if (type.endsWith("JSONArray")) {
				jSONArray2.add(transToArray(JSONArray.fromObject(object)));
			} else if (type.endsWith("Double") || type.endsWith("BigDecimal")) {
				jSONArray2.add(String.valueOf(new BigDecimal(object.toString()).setScale(2)));
			} else {
				jSONArray2.add(object);
			}
		}
		return jSONArray2;
	}
}
