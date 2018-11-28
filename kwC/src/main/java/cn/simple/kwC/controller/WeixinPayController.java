package cn.simple.kwC.controller;

import com.jfinal.weixin.sdk.jfinal.ApiController;

/**
 * @author Javen 2016年3月19日
 */
public class WeixinPayController extends ApiController {

//	private AjaxResult ajax = new AjaxResult();
//	// 商户相关资料
//	String appid = PropKit.get("appId");
//	String partner = PropKit.get("mch_id");
//	String paternerKey = PropKit.get("paternerKey");
//	String notify_url = PropKit.get("domain") + "/pay/pay_notify";
//
//	public void pay_notify() {
//		// 获取所有的参数
//		StringBuffer sbf = new StringBuffer();
//
//		Enumeration<String> en = getParaNames();
//		while (en.hasMoreElements()) {
//			Object o = en.nextElement();
//			sbf.append(o.toString() + "=" + getPara(o.toString()));
//		}
//
//		// 支付结果通用通知文档:
//		// https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
//		String xmlMsg = HttpKit.readData(getRequest());
//		System.out.println("支付通知=" + xmlMsg);
//		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
//
//		String appid = params.get("appid");
//		// 商户号
//		String mch_id = params.get("mch_id");
//		String result_code = params.get("result_code");
//		String openId = params.get("openid");
//		// 交易类型
//		String trade_type = params.get("trade_type");
//		// 付款银行
//		String bank_type = params.get("bank_type");
//		// 总金额
//		String total_fee = params.get("total_fee");
//		// 现金支付金额
//		String cash_fee = params.get("cash_fee");
//		// 微信支付订单号
//		String transaction_id = params.get("transaction_id");
//		// 商户订单号
//		String out_trade_no = params.get("out_trade_no");
//		// 支付完成时间，格式为yyyyMMddHHmmss
//		String time_end = params.get("time_end");
//
//		///////////////////////////// 以下是附加参数///////////////////////////////////
//
//		String attach = params.get("attach");
//		String fee_type = params.get("fee_type");
//		String is_subscribe = params.get("is_subscribe");
//		String err_code = params.get("err_code");
//		String err_code_des = params.get("err_code_des");
//
//		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
//		// 避免已经成功、关闭、退款的订单被再次更新
//		// 根据transaction_id获取订单信息
//		if (PaymentKit.verifyNotify(params, paternerKey)) {
//			if (("SUCCESS").equals(result_code)) {
//				// 更新订单信息
//
//				// 发送通知等
//
//				Map<String, String> xml = new HashMap<String, String>();
//				xml.put("return_code", "SUCCESS");
//				xml.put("return_msg", "OK");
//				renderText(PaymentKit.toXml(xml));
//				return;
//			}
//		}
//		renderText("");
//	}
//
//	/**
//	 * 支付报名费信息获取
//	 * 
//	 * @param userId|用户Id|Integer|必填
//	 * @param type|类型|String|必填
//	 * @param id|编号|Integer|必填
//	 * @title 获取支付信息
//	 * @respParam payInfo|支付信息|payInfo|支付相关
//	 * @respBody
//	 */
//	public void appPay() {
//		// 不用设置授权目录域名
//		// 统一下单地址
//		// https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1#
//		
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("appid", appid);
//		params.put("mch_id", partner);
//		params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
//		params.put("body", "Javen微信支付测试");
//		String out_trade_no = System.currentTimeMillis() + "";
//		params.put("attach", "custom json");
//		params.put("out_trade_no", out_trade_no);
//		int price = 10000;
//		params.put("total_fee", price + "");
//
//		String ip = IpKit.getRealIp(getRequest());
//		if (StrKit.isBlank(ip)) {
//			ip = "127.0.0.1";
//		}
//
//		params.put("spbill_create_ip", ip);
//		params.put("notify_url", notify_url);
//		params.put("trade_type", "APP");
//
//		String sign = PaymentKit.createSign(params, paternerKey);
//		params.put("sign", sign);
//
//		String xmlResult = PaymentApi.pushOrder(params);
//
//		System.out.println(xmlResult);
//		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
//
//		String return_code = result.get("return_code");
//		String return_msg = result.get("return_msg");
//		if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
//			ajax.addError(return_msg);
//			renderJson(ajax);
//			return;
//		}
//		String result_code = result.get("result_code");
//		if (StrKit.isBlank(result_code) || !"SUCCESS".equals(result_code)) {
//			ajax.addError(return_msg);
//			renderJson(ajax);
//			return;
//		}
//		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
//		String prepay_id = result.get("prepay_id");
//		// 封装调起微信支付的参数
//		// https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12
//		Map<String, String> packageParams = new HashMap<String, String>();
//		packageParams.put("appid", appid);
//		packageParams.put("partnerid", partner);
//		packageParams.put("prepayid", prepay_id);
//		packageParams.put("package", "Sign=WXPay");
//		packageParams.put("noncestr", System.currentTimeMillis() + "");
//		packageParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
//		String packageSign = PaymentKit.createSign(packageParams, paternerKey);
//		packageParams.put("sign", packageSign);
//
//		String jsonStr = JsonUtils.toJson(packageParams);
//		System.out.println("最新返回apk的参数:" + jsonStr);
//		renderJson(jsonStr);
//	}
}
