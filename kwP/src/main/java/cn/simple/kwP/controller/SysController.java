package cn.simple.kwP.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.jfinal.ApiController;
import com.jfinal.weixin.sdk.kit.PaymentKit;

import cn.simple.kwP.service.SysServiceImpl;

/**
 * Sys模块
 * 
 * @author may
 *
 */
public class SysController extends ApiController {

	public void pay_notify() {
		// 获取所有的参数
		StringBuffer sbf = new StringBuffer();

		Enumeration<String> en = getParaNames();
		while (en.hasMoreElements()) {
			Object o = en.nextElement();
			sbf.append(o.toString() + "=" + getPara(o.toString()));
		}

		// 支付结果通用通知文档:
		// https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
		String xmlMsg = HttpKit.readData(getRequest());
		System.out.println("支付通知=" + xmlMsg);
		Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
		String result_code = params.get("result_code");
		// 微信支付订单号
		String transaction_id = params.get("transaction_id");
		// 商户订单号
		String out_trade_no = params.get("out_trade_no");
		// 支付完成时间，格式为yyyyMMddHHmmss
		String time_end = params.get("time_end");

		///////////////////////////// 以下是附加参数///////////////////////////////////

		// 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
		// 避免已经成功、关闭、退款的订单被再次更新
		// 根据transaction_id获取订单信息
		String paternerKey = PropKit.get("paternerKey");
		if (PaymentKit.verifyNotify(params, paternerKey)) {
			if (("SUCCESS").equals(result_code)) {
				// 更新订单信息
				SysServiceImpl callback = new SysServiceImpl();
				boolean enrollCallback = callback.enrollCallback(out_trade_no, transaction_id, time_end);
				if (enrollCallback) {
					// 发送通知等
					Map<String, String> xml = new HashMap<String, String>();
					xml.put("return_code", "SUCCESS");
					xml.put("return_msg", "OK");
					renderText(PaymentKit.toXml(xml));
					return;
				}
			}
		}
		renderText("");
	}
}
