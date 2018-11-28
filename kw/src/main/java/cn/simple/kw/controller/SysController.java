package cn.simple.kw.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.ext.interceptor.POST;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.encrypt.WxaBizDataCrypt;
import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.wxaapp.api.WxaUserApi;

import cn.simple.kw.BaseAPIController;
import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model.UserAccountStream;
import cn.simple.kw.model.UserInfo;
import cn.simple.kw.service.GameService;
import cn.simple.kw.service.SysService;
import cn.simple.kw.service.UserGameService;
import cn.simple.kw.service.UserService;
import cn.simple.kw.utils.WXPayXmlUtil;
import cn.simple.kwA.ioc.Autowired;
import cn.simple.kwA.utils.DateUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Sys模块
 * 
 * @author may
 *
 */
public class SysController extends BaseAPIController {

	@Autowired
	UserService userService;

	@Autowired
	UserGameService userGameService;

	@Autowired
	GameService gameService;

	@Autowired
	SysService sysService;

	/**
	 * 首页触发
	 */
	public void index() {
		String authorizeURL = sysService.getAuthorizeURL();
		renderSuccess(authorizeURL, "认证地址获取成功");
	}

	/**
	 * @title 登陆
	 * @param code|微信code|String|必填
	 * @param fromUserId|来自用户Id|Integer|选填
	 * @param gameId|赛事Id|Integer|选填
	 * @respParam userId|用户Id|Integer|必返
	 * @respParam selectedMenu|选中菜单(0:首页，1:奖励金，2:报名页，3:详情页)|String|必返
	 * @respBody { "status": "0", "data": { "userId":1 }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void login() {

		// 返回结果
		Record result = new Record();
		String selectedMenu = "0";
		getHeader("code");
		// 获取code
		Integer gameId = getParaToInt("gameId");
		String code = getPara("code");
		if (StringUtils.isEmpty(code)) {
			code = getHeader("code");
			System.out.println(code);
		}
		if (code != null) {
			WxaUserApi wxaUserApi = new WxaUserApi();
			ApiResult sessionKey = wxaUserApi.getSessionKey(code);
			String openId = sessionKey.getStr("openid");
			String session_key = sessionKey.getStr("session_key");
			if (StringUtils.isEmpty(openId)) {
				renderError("openid获取失败,code无效");
				return;
			}

			// 用户Id
			Integer userId = userService.getUserIdByWxOpenId(openId);
			UserInfo userInfo = new UserInfo();
			userInfo.setLastLoginTime(new Date());
			// 追加用户信息
			if (userId == null) {
				String fromUserId = getPara("fromUserId");
				System.out.println(fromUserId);
				if (StringUtils.isNotEmpty(fromUserId)) {
					userInfo.setFromUserId(Integer.valueOf(fromUserId));
				}
				userInfo.setWxOpenId(openId);
				userInfo.setSessionKey(session_key);
				userInfo.setIconUrl("https://www.walkvalue.com/icon/w.png");
				userInfo.setNickName("神秘人-X");
				UserInfo addUserInfo = userService.addUserInfo(userInfo);
				userId = addUserInfo.getUserId();

				if (gameId != null) {
					// 报名页
					selectedMenu = "2";
				}

				// 更新用户登陆信息
			} else {
				userInfo.setUserId(userId);
				userInfo.setSessionKey(session_key);
				userService.updateUserInfo(userInfo);
				Integer ongoingGameOrderCount = userService.getOngoingGameOrderCount(userId);
				if (ongoingGameOrderCount > 0) {
					selectedMenu = "1";
				}

				if (gameId != null) {
					selectedMenu = "2";
					GameOrder userGameOrder = userGameService.getUserGameOrder(userId, gameId);
					if (userGameOrder != null) {
						// 报名页
						selectedMenu = "3";
					}
				}
			}

			result.set("userId", userId);
			result.set("selectedMenu", selectedMenu);
			result.set("version", Db.queryStr("select version from sys_info where del_flg=0 limit 1"));
			renderSuccess(result.toJson(), "登陆成功");
		} else {
			renderError("code不能为空");
		}
	}

	/**
	 * @title 获取当前微信步数
	 * @param userId|用户Id|Integer|必填
	 * @param encryptedData|加密内容|String|必填
	 * @param iv|加密算法的初始向量|String|必填
	 * @respBody {"status":"0","data":{step:100},"info":"处理成功"}
	 */
	@Before(POST.class)
	public void getUserRunInfo() {
		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");
		String sessionKey = userService.getSessionKey(userId);
		String encryptedData = getPara("encryptedData");
		String ivStr = getPara("iv");
		WxaBizDataCrypt dataCrypt = new WxaBizDataCrypt(sessionKey);
		String json = dataCrypt.decrypt(encryptedData, ivStr);
		JSONObject runInfo = null;
		try {
			runInfo = JSONObject.fromObject(json);
		} catch (Exception e) {
			json = "{'stepInfoList':" + json.substring(json.indexOf("["), json.length());
			try {
				runInfo = JSONObject.fromObject(json);
			} catch (Exception e2) {
				System.out.println("步数获取异常");
				return;
			}
		}
		JSONArray jsonArray = runInfo.getJSONArray("stepInfoList");
		if (jsonArray.size() > 0) {
			// Redis.use().setex("step" + userId, 3600, jsonArray);
			JSONObject jsonObject = jsonArray.getJSONObject(jsonArray.size() - 1);
			long timestamp = jsonObject.getLong("timestamp");
			Long dateBegin = DateUtils.getDateBegin();
			if (timestamp >= dateBegin) {
				Integer step = jsonObject.getInt("step");
				userGameService.reportStep(userId, step);
				result.set("step", step);
				renderSuccess(result.toJson(), "获取成功");
			} else {
				result.set("step", 0);
				renderSuccess(result.toJson(), "获取成功");
			}
		} else {
			result.set("step", 0);
			renderSuccess(result.toJson(), "获取成功");
		}
	}

	/**
	 * @title 更新头像昵称
	 * @param userId|用户Id|Integer|必填
	 * @param nickName|昵称|String|必填
	 * @param iconUrl|头像|String|必填
	 * @respBody {"status":"0","data":"","info":"处理成功"}
	 */
	@Before(POST.class)
	public void upUserInfo() {

		// 返回结果
		UserInfo userInfo = getBean(UserInfo.class, null);
		userInfo.setNickName(StringEscapeUtils.escapeJava(userInfo.getNickName()));
		userService.updateBaseInfo(userInfo);
		renderSuccess("", "处理成功");
	}

	/**
	 * @title 步数上传
	 * @param userId|用户Id|Integer|必填
	 * @param step|步数|Integer|必填
	 * @respBody
	 */
	@Before(POST.class)
	public void reportStep() {

		// 参数
		Integer userId = getParaToInt("userId");
		Integer step = getParaToInt("step");
		userGameService.reportStep(userId, step);
		renderSuccess("", "步数同步完成");
	}

	/**
	 * @title 发起挑战
	 * @param userId|用户Id|Integer|必填
	 * @param title|标题名称|String|必填
	 * @param reachStep|达标步数|Integer|必填
	 * @param entryFee|报名费用|Integer|必填
	 * @param isOpen|参赛权限（0:公开、1:自主邀请）|Integer|必填
	 * @respParam gameInfo|赛事详情|model|必返
	 * @respParam ~ gameId|赛事Id|Integer|必返
	 * @respParam ~ nickName|发起人昵称|String|必返
	 * @respParam ~ iconUrl|发起人头像|String|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ startTime|赛事开始时间|String|必返
	 * @respParam ~ endTime|赛事结束时间|String|必返
	 * @respParam ~ entryFee|报名费用|Decimal|必返
	 * @respParam ~ reachStep|达标步数|Integer|必返
	 * @respParam ~ entryCount|总报名人数|Integer|必返
	 * @respParam ~ entryAmount|总报名费用|Decimal|必返
	 * @respParam ~ reachCount|达标人数|Integer|必返
	 * @respParam ~ avgAmount|平均奖励金|Decimal|必返
	 * @respBody { "status": "0", "data": { "gameInfo": { "gameId": 4,
	 *           "nickName": "haha", "iconUrl": "test", "title": "自主挑战",
	 *           "phaseNo": "20180822", "startTime": "00:00:00", "endTime":
	 *           "23:59:59", "entryFee": 0, "reachStep": 12000, "entryCount": 0,
	 *           "entryAmount": 0, "reachCount": 0, "avgAmount": null } },
	 *           "info": "创建成功" }
	 */
	@Before(POST.class)
	public void createGame() {

		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		GameInfo gameInfo = getBean(GameInfo.class, null);
		Integer usableEnrollCount = userService.getUsableEnrollCount(userId);
		if (usableEnrollCount > 0) {
			String title = gameInfo.getTitle();
			// 明天挑战赛标题重复check（重复：true、没重复：false）
			if (gameService.checkTitle(title)) {
				renderError("挑战赛名称重复");
				return;
			}
			UserInfo userBaseInfo = userService.getUserBaseInfo(gameInfo.getUserId());
			gameInfo.setNickName(userBaseInfo.getNickName());
			gameInfo.setIconUrl(userBaseInfo.getIconUrl());
			gameInfo.setPhaseNo(DateUtils.getTomorrowYYYYMMDD());
			gameInfo.setStartTime("00:00:00");
			gameInfo.setEndTime("23:59:59");
			gameInfo.setDelFlg("1");
			Integer gameId = gameService.createGame(gameInfo);
			GameInfo game = gameService.getGameInfo(gameId);
			result.set("gameInfo", game);
			renderSuccess(result.toJson(), "创建成功");
		} else {
			renderError("明日报名次数已满，无法发起挑战");
		}
	}

	/**
	 * @title 立即报名
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|挑战赛Id|Integer|必填
	 * @param payType|支付类型（0:余额、1:微信、2:支付宝）|String|必填
	 * @respParam payInfo|支付相关信息|payInfo|必返
	 * @respBody { "status": "0", "data": { "payInfo": { "timeStamp":
	 *           "1534863192", "package":
	 *           "prepayId=wx2122531045569275a3e615403325350778", "paySign":
	 *           "B740E29EF12B58BCD8D1E148CDDFF6AC", "appId":
	 *           "wx4d59fce1314a52af", "signType": "MD5", "nonceStr":
	 *           "1534863192012" } }, "info": "信息获取成功" }
	 */
	@Before(POST.class)
	public void enroll() {
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		String payType = getPara("payType");
		Integer usableEnrollCount = userService.getUsableEnrollCount(userId);
		// 报名次数判断
		if (usableEnrollCount > 0) {
			Map<String, String> payMap = new HashMap<>();
			GameOrder userGameOrder = userGameService.getUserGameOrder(userId, gameId);
			if (userGameOrder != null) {
				renderError("不能重复报名");
				return;
			}
			GameInfo gameBaseInfo = gameService.getGameBaseInfo(gameId);
			String today = DateUtils.getTodayYYYYMMDD();
			if (today.compareTo(gameBaseInfo.getPhaseNo()) >= 0) {
				renderError("不可报名当天或之前的挑战赛");
				return;
			}
			// 支付类型（0:余额支付）
			if ("0".equals(payType)) {
				payMap = sysService.enrollByBalance(userId, gameId);
				if (payMap.containsKey("error")) {
					renderError(payMap.get("error"));
				} else {
					renderSuccess("", "余额支付成功");
				}
				// 微信支付
			} else if ("1".equals(payType)) {
				payMap = sysService.enroll(userId, gameId);
				if (payMap.containsKey("error")) {
					renderError(payMap.get("error"));
				} else {
					result.set("payInfo", payMap);
					renderSuccess(result.toJson(), "信息获取成功");
				}
			}
		} else {
			renderError("明日报名次数已满，请提升明日报名次数后才能报名");
		}
	}

	/**
	 * @title 分享成功调用、满5次无用
	 * @param userId|用户Id|Integer|必填
	 * @respBody
	 */
	@Before(POST.class)
	public void addBaseEnrollCount() {

		// 参数
		Integer userId = getParaToInt("userId");

		UserInfo userEnrollCountInfo = userService.getUserEnrollCountInfo(userId);
		// 基础报名次数小于6的+1
		if (userEnrollCountInfo.getBaseEnrollCount() < 6) {
			userService.addBaseEnrollCount(userId);
		}
		renderSuccess("", "处理成功");
	}

	/**
	 * 微信支付回调
	 * 
	 * @throws Exception
	 */
	public void payNotify() throws Exception {

		// 支付结果通用通知文档:
		// https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
		String xmlMsg = HttpKit.readData(getRequest());
//		System.out.println("支付通知=" + xmlMsg);
		try {
			Map<String, String> params = WXPayXmlUtil.xmlToMap(xmlMsg);
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
			if (PaymentKit.verifyNotify(params, PropKit.get("wechat.key"))) {
				if (("SUCCESS").equals(result_code)) {

					GameOrder gameOrder = userGameService.getGameOrderByOrderId(out_trade_no);
					// 未支付过
					if (gameOrder != null) {
						gameOrder.setTradeNo(transaction_id);
						gameOrder.setPayType("1");
						gameOrder.setPayTime(DateUtils.formatData(time_end));
						gameOrder.setPayStatus("1");
						// 更新订单信息
						sysService.enrollCallback(gameOrder);
					} else {
						System.out.println("无订单或已支付");
					}

					// 发送通知等
					Map<String, String> xml = new HashMap<String, String>();
					xml.put("return_code", "SUCCESS");
					xml.put("return_msg", "OK");
					renderText(PaymentKit.toXml(xml));
				}
			} else {
				renderText("");
			}
		} catch (Exception e) {
			System.out.println("支付回调异常，可忽略");
		}
		
	}

	/**
	 * @title 提现至微信零钱
	 * @param userId|用户Id|Integer|必填
	 * @param amount|提现金额|String|必填
	 * @respBody { "status": "0", "data": "", "info": "提现成功" }
	 */
	@Before(POST.class)
	public void transfers() {

		// 参数
		Integer userId = getParaToInt("userId");
		Integer amount = getParaToInt("amount");
		String phaseNo = DateUtils.getTodayYYYYMMDD();

		BigDecimal usableBalance = userService.getUsableBalance(userId);
		// 余额判断
		if (usableBalance.compareTo(new BigDecimal(amount)) < 0) {
			renderError("可提现余额不足");
			return;
		}

		// 当天提现判断
		if (userService.exitTransfer(userId, phaseNo)) {
			renderError("今日已提现");
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		// 收款用户在wxappid下的openid
		String openid = userService.getWxOpenIdByUserId(userId);

		// 订单号
		String orderNo = "TX" + userId + "T" + DateUtils.getNowTimeStamp();
		// 真实姓名（可选）
		// String reUserName = "Javen205";
		// 金额 单位：分
		params.put("amount", String.valueOf(amount * 100));
		// 是否验证姓名
		// NO_CHECK：不校验真实姓名
		// FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
		// OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）
		params.put("check_name", "NO_CHECK");
		// 描述
		String title = "运动奖励金提现";
		params.put("desc", title);
		params.put("mch_appid", PropKit.get("weapp.AppId"));
		params.put("mchid", PropKit.get("wechat.mchID"));
		// 随机字符串
		params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
		params.put("openid", openid);
		params.put("partner_trade_no", orderNo);
		// 收款用户真实姓名。
		// 如果check_name设置为FORCE_CHECK或OPTION_CHECK，则必填用户真实姓名
		// params.put("re_user_name", reUserName);
		String ip = IpKit.getRealIp(getRequest());
		if (StrKit.isBlank(ip)) {
			ip = "127.0.0.1";
		}
		params.put("spbill_create_ip", ip);
		String sign = PaymentKit.createSign(params, PropKit.get("wechat.key"));
		params.put("sign", sign);
		String xml = PaymentKit.toXml(params);
		System.out.println(xml);
		String xmlResult = HttpUtils.postSSL(PropKit.get("wechat.transfers"), xml, PropKit.get("wechat.cert"),
				PropKit.get("wechat.mchID"));
		System.out.println(xmlResult);
		Map<String, String> resultXML = PaymentKit.xmlToMap(xmlResult.toString());
		String return_code = resultXML.get("return_code");
		String return_msg = resultXML.get("return_msg");
		if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
			renderError(return_msg);
			return;
		}
		String result_code = resultXML.get("result_code");
		if (StrKit.isBlank(result_code) || !"SUCCESS".equals(result_code)) {
			renderError(return_msg);
			return;
		}

		// 明细追加
		UserAccountStream stream = new UserAccountStream();
		stream.setUserId(userId);
		stream.setAmount(new BigDecimal(amount));
		stream.setPhaseNo(phaseNo);
		stream.setIoType("2");
		stream.setPayType("0");
		stream.setOrderId(orderNo);
		stream.setTitle(title);
		userService.addStream(stream);
		renderSuccess("", "提现成功");
	}
}
