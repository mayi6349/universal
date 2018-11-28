package cn.simple.kw.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.redis.Redis;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.utils.PaymentException;
import com.jfinal.wxaapp.api.WxaOrder;
import com.jfinal.wxaapp.api.WxaPayApi;

import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model.UserAccountStream;
import cn.simple.kw.model.UserInfo;
import cn.simple.kwA.ioc.Service;
import cn.simple.kwA.utils.DateUtils;

@Service
public class SysService {
	UserService userService = new UserService();
	GameService gameService = new GameService();

	/**
	 * 授权地址获取
	 * 
	 * @return
	 */
	public String getAuthorizeURL() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		return SnsAccessTokenApi.getAuthorizeURL(appId, PropKit.get("redirect_uri"), true);
	}

	/**
	 * 微信支付信息获取
	 * 
	 * @param userId
	 * @param gameId
	 * @return
	 */
	public Map<String, String> enrollByBalance(Integer userId, Integer gameId) {

		Map<String, String> result = new HashMap<>();
		// 挑战赛信息
		GameInfo gameInfo = gameService.getGameInfo(gameId);
		BigDecimal usableBalance = userService.getUsableBalance(userId);
		// 判断余额是否够
		if (usableBalance.compareTo(gameInfo.getEntryFee()) < 0) {
			result.put("error", "余额不足");
			return result;
		}

		GameOrder order = new GameOrder();
		// 订单编号
		String orderId = "TZ_" + userId + "_" + gameId + "_" + DateUtils.getNowTimeStamp();
		order.setUserId(userId);
		order.setGameId(gameId);
		order.setOrderId(orderId);
		order.setPayStatus("1");
		order.setPayType("0");
		order.setPayTime(DateUtils.getNowTime());

		// 判断有无临时报名
		Integer id = Db.queryInt("select id from game_order where user_id=? and game_id=? and pay_status=0", userId,
				gameId);

		// 更新
		if (id != null) {
			order.setId(id);
			order.setCreateTime(new Date());
		} else {
			// 用户基本信息
			UserInfo userBaseInfo = userService.getUserBaseInfo(userId);
			order.setNickName(userBaseInfo.getNickName());
			order.setIconUrl(userBaseInfo.getIconUrl());
			order.setOrderId(orderId);
			order.setTitle(gameInfo.getTitle());
			order.setPhaseNo(gameInfo.getPhaseNo());
			order.setEntryFee(gameInfo.getEntryFee());
			order.setReachStep(gameInfo.getReachStep());
		}

		if (enrollCallback(order)) {
			BigDecimal entryFee = gameInfo.getEntryFee();
			// 账户扣款
			int update = Db.update("update user_account set usable_balance=usable_balance-? where user_id=?", entryFee,
					userId);
			if (update > 0) {
				// 明细追加
				UserAccountStream stream = new UserAccountStream();
				stream.setUserId(userId);
				stream.setAmount(entryFee);
				stream.setIoType("0");
				stream.setPayType("0");
				stream.setPhaseNo(gameInfo.getPhaseNo());
				stream.setOrderId(orderId);
				stream.setTitle(gameInfo.getTitle());
				stream.save();
			}
		}
//		Redis.use().flushAll();
		return result;
	}

	/**
	 * 微信支付信息获取
	 * 
	 * @param userId
	 * @param gameId
	 * @return
	 */
	public Map<String, String> enroll(Integer userId, Integer gameId) {

		GameOrder order = new GameOrder();
		// 订单编号
		String orderId = "TZ_" + userId + "_" + gameId + "_" + DateUtils.getNowTimeStamp();
		// 判断有无临时报名
		Integer id = Db.queryInt("select id from game_order where user_id=? and game_id=? and pay_status=0", userId,
				gameId);
		// 更新
		if (id != null) {
			order.setId(id);
			order.setOrderId(orderId);
			order.setCreateTime(new Date());
			order.update();
			order = GameOrder.dao.findFirst("select order_id,title,entry_fee from game_order where id=?", id);
		} else {
			// 用户基本信息
			UserInfo userBaseInfo = userService.getUserBaseInfo(userId);
			order.setUserId(userId);
			order.setNickName(userBaseInfo.getNickName());
			order.setIconUrl(userBaseInfo.getIconUrl());
			// 挑战赛信息
			GameInfo gameInfo = gameService.getGameInfo(gameId);
			order.setOrderId(orderId);
			order.setGameId(gameId);
			order.setTitle(gameInfo.getTitle());
			order.setPhaseNo(gameInfo.getPhaseNo());
			order.setEntryFee(gameInfo.getEntryFee());
			order.setReachStep(gameInfo.getReachStep());
			order.save();
		}

		WxaOrder wxaOrder = new WxaOrder(PropKit.get("weapp.AppId"), PropKit.get("wechat.mchID"),
				PropKit.get("wechat.key"));

		// 商品名
		wxaOrder.setBody(order.getTitle());
		// 金额
		wxaOrder.setTotalFee(String
				.valueOf(order.getEntryFee().multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP)));
		// 微信openId
		wxaOrder.setOpenId(userService.getWxOpenIdByUserId(userId));
		// 支付订单号
		wxaOrder.setOutTradeNo(orderId);
		wxaOrder.setSpbillCreateIp("127.0.0.1");
		wxaOrder.setNotifyUrl(PropKit.get("wechat.notifyUrl"));
		WxaPayApi wxaPayApi = new WxaPayApi();
		Map<String, String> unifiedOrder = new HashMap<>();
		try {
			unifiedOrder = wxaPayApi.unifiedOrder(wxaOrder);
		} catch (PaymentException e) {
			unifiedOrder.put("error", e.getMessage());
		}
//		Redis.use().flushAll();
		return unifiedOrder;
	}

	/**
	 * 微信支付成功后回调
	 * 
	 * @param gameOrder
	 */
	public boolean enrollCallback(GameOrder gameOrder) {

		boolean flg = false;
		Integer gameId = gameOrder.getGameId();
		Integer userId = gameOrder.getUserId();
		// gameInfo
		GameInfo gameInfo = gameService.getGameBaseInfo(gameId);
		if (gameInfo == null) {
			return false;
		}

		if (String.valueOf(userId).equals(String.valueOf(gameInfo.getUserId()))) {
			Db.update("update game_info set del_flg=0 where game_id=?", gameInfo.getGameId());
		}

		Integer enrollCount = gameInfo.getEntryCount();
		// // 分组
		// int groupNo = (enrollCount - 1) / 200 + 1;
		// gameOrder.setGroupNo(String.valueOf(groupNo));

		if (gameOrder.getId() == null) {
			gameOrder.setTitle(gameInfo.getTitle());
			gameOrder.setPhaseNo(gameInfo.getPhaseNo());
			gameOrder.setEntryFee(gameInfo.getEntryFee());
			gameOrder.setReachStep(gameInfo.getReachStep());
			flg = gameOrder.save();
			Db.update("update sys_account set frozen_balance=frozen_balance+?", gameInfo.getEntryFee());
		} else {
			flg = gameOrder.update();
		}

		if (flg) {
			// 报名人数+1
			Db.update(
					"update game_info set entry_count=entry_count+1, entry_amount=entry_amount+entry_fee where game_id=?",
					gameId);
			// 个人账户更新
			Db.update(
					"update user_account set sum_pay_count=sum_pay_count+1, sum_pay_amount=sum_pay_amount+? where user_id=?",
					gameOrder.getEntryFee(), userId);

			// 助力小伙伴报名+1
			Integer fromUserId = userService.getFromUserId(userId);
			if (fromUserId != null && fromUserId != 0) {
				Db.update("update user_info set help_enroll_count=help_enroll_count+1 where user_id=?", fromUserId);
			}
			// 判断是否公开,是否各位数是8
			if ("0".equals(gameInfo.getIsOpen()) && "8".equals(getNm(enrollCount))) {
				addSysUser(gameId);
			}
		}
//		Redis.use().flushAll();
		return flg;
	}

	/**
	 * 机器人
	 * 
	 * @param gameId
	 */
	public void addSysUser(Integer gameId) {
		// 获取未报名的系统用户信息
		UserInfo userInfo = UserInfo.dao.findFirst(
				"select t1.user_id,t1.nick_name,t1.icon_url from user_info t1 left join game_order t2 "
						+ "on (t1.user_id=t2.user_id and t2.game_id=?) where t2.user_id is null and t1.from_user_id=0 order by rand()",
				gameId);
		GameOrder sysGameOrder = new GameOrder();
		if (userInfo != null) {
			Integer userId = userInfo.getUserId();
			String orderId = "TZ_" + userId + "_" + gameId + "_" + DateUtils.getNowTimeStamp();
			sysGameOrder.setUserId(userId);
			sysGameOrder.setNickName(userInfo.getNickName());
			sysGameOrder.setIconUrl(userInfo.getIconUrl());
			// 挑战赛信息
			sysGameOrder.setOrderId(orderId);
			sysGameOrder.setGameId(gameId);
			sysGameOrder.setPayStatus("1");
			sysGameOrder.setPayTime(DateUtils.getNowTime());
			sysGameOrder.setType("1");
			enrollCallback(sysGameOrder);
		}
	}

	private String getNm(Integer num) {
		while (num > 9) {
			num = num % 10; // 所报数的个位数
		}
		return num.toString();
	}
}
