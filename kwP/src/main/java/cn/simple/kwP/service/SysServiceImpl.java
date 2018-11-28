package cn.simple.kwP.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.JsonUtils;

import cn.simple.kwA.model.GameInfoCity;
import cn.simple.kwA.model.GameInfoCountry;
import cn.simple.kwA.model.GameInfoTeam;
import cn.simple.kwA.model.GroupGameOrderCity;
import cn.simple.kwA.model.GroupGameOrderCountry;
import cn.simple.kwA.model.GroupGameOrderTeam;
import cn.simple.kwA.model.UserAccount;
import cn.simple.kwA.model.UserAccountStream;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;
import cn.simple.kwA.model.UserInfo;
import cn.simple.kwA.service.SysService;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwP.utils.BeanKit;
import cn.simple.kwP.utils.ConsistentHash;
import cn.simple.kwP.utils.TbConst;

public class SysServiceImpl implements SysService {

	@Override
	public String getAuthorizeURL() {
		String appId = ApiConfigKit.getApiConfig().getAppId();
		return SnsAccessTokenApi.getAuthorizeURL(appId, PropKit.get("redirect_uri"), true);
	}

	@Override
	public Boolean addUserInfo(UserInfo userInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean updateUserInfo(UserInfo userInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInfo getUserInfoByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInfo getUserInfoByWxOpenId(String wxOpenId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserInfo getUserInfoByMobile(String mobile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserAccount getUserAccountByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserAccountStream> getStreamByUserId(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean updateUserAccount(UserAccount userAccount) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRandomAvatar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reportStep(Integer userId, Integer step) {
		/*
		 * 步数上报根据user查到基本信息、再只更新group表
		 */
		String phaseNo = DateUtils.getTodayYYYYMMDD();
		String countryTb = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_COUNTRY);
		String cityTb = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_CITY);
		String teamTb = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_TEAM);

		// before数据获取
		String select_utb_before_country = "select id,phase_no,group_no from tb_x where user_id=? and phase_no<? and status=3";
		String select_utb_before_city = "select id,phase_no,city_id,group_no from tb_x where user_id=? and phase_no<? and status=3";
		String select_utb_before_team = "select id,phase_no,team_id,group_no from tb_x where user_id=? and phase_no<? and status=3";
		List<UserGameOrderCountry> beforeCountryList = UserGameOrderCountry.dao
				.find(select_utb_before_country.replace(TbConst.TB_X, countryTb), userId, phaseNo);
		List<UserGameOrderCity> beforeCityList = UserGameOrderCity.dao
				.find(select_utb_before_city.replace(TbConst.TB_X, cityTb), userId, phaseNo);
		List<UserGameOrderTeam> beforeTeamList = UserGameOrderTeam.dao
				.find(select_utb_before_team.replace(TbConst.TB_X, teamTb), userId, phaseNo);

		// 同步未同步的group数据到user
		updateBefore(userId, beforeCountryList, beforeCityList, beforeTeamList);

		String select_utb_country = "select id,group_no from tb_x where user_id=? and phase_no=?";
		String select_utb_city = "select id,city_id,group_no from tb_x where user_id=? and phase_no=?";
		String select_utb_team = "select id,team_id,group_no from tb_x where user_id=? and phase_no=?";

		List<UserGameOrderCountry> countryList = UserGameOrderCountry.dao
				.find(select_utb_country.replace(TbConst.TB_X, countryTb), userId, phaseNo);
		List<UserGameOrderCity> cityList = UserGameOrderCity.dao.find(select_utb_city.replace(TbConst.TB_X, cityTb),
				userId, phaseNo);
		List<UserGameOrderTeam> teamList = UserGameOrderTeam.dao.find(select_utb_team.replace(TbConst.TB_X, teamTb),
				userId, phaseNo);

		// 更新当天group数据
		updateToday(userId, phaseNo, step, countryList, cityList, teamList);
	}

	private void updateBefore(Integer userId, List<UserGameOrderCountry> beforeCountryList,
			List<UserGameOrderCity> beforeCityList, List<UserGameOrderTeam> beforeTeamList) {
		String beforePhaseNo = "";
		String groupNo = "";

		if (beforeCountryList != null) {
			String select_gtb_before = "select amount,status,walk_step from tb_x where user_id=? and phase_no=?";
			String tb = "";
			GroupGameOrderCountry before = new GroupGameOrderCountry();
			for (UserGameOrderCountry info : beforeCountryList) {
				beforePhaseNo = info.getPhaseNo();
				groupNo = info.getGroupNo();
				tb = ConsistentHash.getTbName(beforePhaseNo + groupNo, TbConst.G_G_O_COUNTRY);
				before = GroupGameOrderCountry.dao.findFirst(select_gtb_before.replace(TbConst.TB_X, tb), userId,
						beforePhaseNo);
				// 更新ucountry
				if (before != null) {
					info.setAmount(before.getAmount());
					info.setStatus(before.getStatus());
					info.setWalkStep(before.getWalkStep());
					info.update();
				}
			}
		}

		if (beforeCityList != null) {
			String select_gtb_before = "select amount,status,walk_step from tb_x where user_id=? and phase_no=? and city_id=?";
			String tb = "";
			Integer cityId = null;
			GroupGameOrderCity before = new GroupGameOrderCity();
			for (UserGameOrderCity info : beforeCityList) {
				beforePhaseNo = info.getPhaseNo();
				cityId = info.getCityId();
				groupNo = info.getGroupNo();
				tb = ConsistentHash.getTbName(beforePhaseNo + cityId + groupNo, TbConst.G_G_O_CITY);
				before = GroupGameOrderCity.dao.findFirst(select_gtb_before.replace(TbConst.TB_X, tb), userId,
						beforePhaseNo, cityId);
				// 更新ucountry
				if (before != null) {
					info.setAmount(before.getAmount());
					info.setStatus(before.getStatus());
					info.setWalkStep(before.getWalkStep());
					info.update();
				}
			}
		}

		if (beforeTeamList != null) {
			String select_gtb_before = "select amount,status,walk_step from tb_x where user_id=? and phase_no=? and team_id=?";
			String tb = "";
			Integer teamId = null;
			GroupGameOrderTeam before = new GroupGameOrderTeam();
			for (UserGameOrderTeam info : beforeTeamList) {
				beforePhaseNo = info.getPhaseNo();
				teamId = info.getTeamId();
				groupNo = info.getGroupNo();
				tb = ConsistentHash.getTbName(beforePhaseNo + teamId + groupNo, TbConst.G_G_O_TEAM);
				before = GroupGameOrderTeam.dao.findFirst(select_gtb_before.replace(TbConst.TB_X, tb), userId,
						beforePhaseNo, teamId);
				// 更新ucountry
				if (before != null) {
					info.setAmount(before.getAmount());
					info.setStatus(before.getStatus());
					info.setWalkStep(before.getWalkStep());
					info.update();
				}
			}
		}

	}

	private void updateToday(Integer userId, String phaseNo, Integer step, List<UserGameOrderCountry> countryList,
			List<UserGameOrderCity> cityList, List<UserGameOrderTeam> teamList) {

		if (countryList != null) {

			String groupNo = "";
			String tb = "";
			String select_gtb_count = "select count(0) from tb_x where phase_no=? and group_no=?";
			String select_gtb_reachcount = "select count(0) from tb_x where phase_no=? and group_no=? and status=1";
			String update_gtb_step = "update tb_x set walk_step=? where user_id=? and phase_no=?";
			String update_gtb_status = "update tb_x set amount=?, walk_step=case when user_id=? then ? else walk_step end , "
					+ "status=case when user_id=? then 1 else status end where phase_no=? and group_no=?";
			String update_reach_count = "update game_info_country set reach_count=reach_count+1 where phase_no=?";

			for (UserGameOrderCountry info : countryList) {
				groupNo = info.getGroupNo();
				tb = ConsistentHash.getTbName(phaseNo + groupNo, TbConst.G_G_O_COUNTRY);
				// 已达标或未达标，单做步数更新
				if ("1".equals(info.getStatus()) || step < info.getReachStep()) {
					// group表
					Db.update(update_gtb_step.replace("tb_x", tb), step, userId, phaseNo);
				} else {
					// 变达标、更新整个group的amount
					long groupCount = Db.queryFirst(select_gtb_count.replace("tb_x", tb), phaseNo, groupNo);
					long reachCount = Db.queryFirst(select_gtb_reachcount.replace("tb_x", tb), phaseNo, groupNo);
					BigDecimal total = info.getEntryFee().multiply(new BigDecimal(groupCount));
					BigDecimal amount = total.divide(new BigDecimal(reachCount + 1)).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					Db.update(update_gtb_status.replace("tb_x", tb), amount, userId, step, userId, phaseNo, groupNo);

					// 达标+1
					Db.update(update_reach_count, phaseNo);
				}
			}
		}

		if (cityList != null) {

			Integer cityId = null;
			String groupNo = "";
			String tb = "";
			String select_gtb_count = "select count(0) from tb_x where phase_no=? and city_id=? and group_no=?";
			String select_gtb_reachcount = "select count(0) from tb_x where phase_no=? and city_id=? and group_no=? and status=1";
			String update_gtb_step = "update tb_x set walk_step=? where user_id=? and phase_no=? and city_id=?";
			String update_gtb_status = "update tb_x set amount=?, walk_step=case when user_id=? then ? else walk_step end , "
					+ "status=case when user_id=? then 1 else status end where phase_no=? and city_id=? and group_no=?";
			String update_reach_count = "update game_info_city set reach_count=reach_count+1 where phase_no=? and city_id=?";

			for (UserGameOrderCity city : cityList) {
				groupNo = city.getGroupNo();
				cityId = city.getCityId();
				tb = ConsistentHash.getTbName(phaseNo + cityId + groupNo, TbConst.G_G_O_CITY);

				// 已达标或未达标，单做步数更新
				if ("1".equals(city.getStatus()) || step < city.getReachStep()) {
					// group表
					Db.update(update_gtb_step.replace("tb_x", tb), step, userId, phaseNo, cityId);
				} else {
					// 变达标、更新整个group的amount
					long groupCount = Db.queryFirst(select_gtb_count.replace("tb_x", tb), phaseNo, cityId, groupNo);
					long reachCount = Db.queryFirst(select_gtb_reachcount.replace("tb_x", tb), phaseNo, cityId,
							groupNo);
					BigDecimal total = city.getEntryFee().multiply(new BigDecimal(groupCount));
					BigDecimal amount = total.divide(new BigDecimal(reachCount + 1)).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					Db.update(update_gtb_status.replace("tb_x", tb), amount, userId, step, userId, phaseNo, cityId,
							groupNo);

					// 达标+1
					Db.update(update_reach_count, phaseNo, cityId);
				}
			}
		}

		if (teamList != null) {

			Integer teamId = null;
			String groupNo = "";
			String tb = "";
			String select_gtb_count = "select count(0) from tb_x where phase_no=? and team_id=? and group_no=?";
			String select_gtb_reachcount = "select count(0) from tb_x where phase_no=? and team_id=? and group_no=? and status=1";
			String update_gtb_step = "update tb_x set walk_step=? where user_id=? and phase_no=? and team_id=?";
			String update_gtb_status = "update tb_x set amount=?, walk_step=case when user_id=? then ? else walk_step end , "
					+ "status=case when user_id=? then 1 else status end where phase_no=? and team_id=? and group_no=?";
			String update_reach_count = "update tb_x set reach_count=reach_count+1 where phase_no=? and team_id=?";

			for (UserGameOrderTeam team : teamList) {
				groupNo = team.getGroupNo();
				teamId = team.getTeamId();
				tb = ConsistentHash.getTbName(phaseNo + teamId + groupNo, TbConst.G_G_O_TEAM);

				// 已达标或未达标，单做步数更新
				if ("1".equals(team.getStatus()) || step < team.getReachStep()) {
					// group表
					Db.update(update_gtb_step.replace("tb_x", tb), step, userId, phaseNo, teamId);
				} else {
					// 变达标、更新整个group的amount
					long groupCount = Db.queryFirst(select_gtb_count.replace("tb_x", tb), phaseNo, groupNo, teamId);
					long reachCount = Db.queryFirst(select_gtb_reachcount.replace("tb_x", tb), phaseNo, teamId,
							groupNo);
					BigDecimal total = team.getEntryFee().multiply(new BigDecimal(groupCount));
					BigDecimal amount = total.divide(new BigDecimal(reachCount + 1)).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					Db.update(update_gtb_status.replace("tb_x", tb), amount, userId, step, userId, phaseNo, teamId,
							groupNo);

					// 达标+1
					String groupTeamTbName = ConsistentHash.getTbName(phaseNo, TbConst.G_I_TEAM);
					Db.update(update_reach_count.replace(TbConst.TB_X, groupTeamTbName), phaseNo, teamId);
				}
			}
		}
	}

	@Override
	public String enroll(Integer userId, String type, Integer enrollId) {

		Record enrollResult = new Record();
		Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				String tomorrowYYYYMMDD = DateUtils.getTomorrowYYYYMMDD();
				String out_trade_no = "";
				String body = "";
				String price = "";

				// 生成待支付订单
				UserInfo userInfo = UserInfo.dao.findFirst("select nick_name,icon_url from user_info where user_id=?",
						userId);

				if ("0".equals(type)) {
					GameInfoCountry gameInfo = GameInfoCountry.dao
							.findFirst("select entry_fee,reach_step from game_info_country where id=?", enrollId);
					BigDecimal entryFee = gameInfo.getEntryFee();
					price = entryFee.multiply(new BigDecimal(100)).toString();

					body = "全国-" + tomorrowYYYYMMDD + "期";
					out_trade_no = "U" + userId + "QG" + tomorrowYYYYMMDD + "ID" + enrollId + "T"
							+ System.currentTimeMillis() / 1000;
					String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_COUNTRY);

					UserGameOrderCountry data = new UserGameOrderCountry();
					data.setUserId(userId);
					data.setNickName(userInfo.getNickName());
					data.setIconUrl(userInfo.getIconUrl());
					data.setPhaseNo(tomorrowYYYYMMDD);
					data.setOrderId(out_trade_no);
					data.setEntryFee(entryFee);
					data.setPayType("1");
					data.setReachStep(gameInfo.getReachStep());
					Db.save(tbName, data.toRecord());

				} else if ("1".equals(type)) {
					GameInfoCity gameInfo = GameInfoCity.dao.findFirst(
							"select city_id,city_name,entry_fee,reach_step from game_info_city where id=?", enrollId);
					BigDecimal entryFee = gameInfo.getEntryFee();
					price = entryFee.multiply(new BigDecimal(100)).toString();

					body = "城市-" + tomorrowYYYYMMDD + "期";
					out_trade_no = "U" + userId + "CS" + tomorrowYYYYMMDD + "ID" + enrollId + "T"
							+ System.currentTimeMillis() / 1000;
					String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_CITY);

					UserGameOrderCity data = new UserGameOrderCity();
					data.setUserId(userId);
					data.setNickName(userInfo.getNickName());
					data.setIconUrl(userInfo.getIconUrl());
					data.setPhaseNo(tomorrowYYYYMMDD);
					data.setCityId(gameInfo.getCityId());
					data.setCityName(gameInfo.getCityName());
					data.setOrderId(out_trade_no);
					data.setEntryFee(entryFee);
					data.setPayType("1");
					data.setReachStep(gameInfo.getReachStep());
					Db.save(tbName, data.toRecord());

				} else if ("2".equals(type)) {
					GameInfoTeam gameInfo = GameInfoTeam.dao.findFirst(
							"select team_name,entry_fee,reach_step from "
									+ ConsistentHash.getTbName(tomorrowYYYYMMDD, TbConst.G_I_TEAM) + " where id=?",
							enrollId);
					BigDecimal entryFee = gameInfo.getEntryFee();
					price = entryFee.multiply(new BigDecimal(100)).toString();
					StringBuilder insert_sql = new StringBuilder();

					body = "团队-" + tomorrowYYYYMMDD + "期";
					out_trade_no = "U" + userId + "TD" + tomorrowYYYYMMDD + "ID" + enrollId + "T"
							+ System.currentTimeMillis() / 1000;
					String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_TEAM);

					// 插入
					Db.update(insert_sql.toString());
					UserGameOrderTeam data = new UserGameOrderTeam();
					data.setUserId(userId);
					data.setNickName(userInfo.getNickName());
					data.setIconUrl(userInfo.getIconUrl());
					data.setPhaseNo(tomorrowYYYYMMDD);
					data.setTeamId(gameInfo.getId());
					data.setTeamName(gameInfo.getTeamName());
					data.setOrderId(out_trade_no);
					data.setEntryFee(entryFee);
					data.setPayType("1");
					data.setReachStep(gameInfo.getReachStep());
					Db.save(tbName, data.toRecord());

				}

				// 不用设置授权目录域名
				String appid = PropKit.get("appId");
				String partner = PropKit.get("mch_id");
				String paternerKey = PropKit.get("paternerKey");
				String notify_url = PropKit.get("domain") + "/pay/pay_notify";

				// 统一下单地址
				// https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1#
				Map<String, String> params = new HashMap<String, String>();
				params.put("appid", appid);
				params.put("mch_id", partner);
				params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
				params.put("body", body);
				params.put("attach", "custom json");
				params.put("out_trade_no", out_trade_no);
				params.put("total_fee", price);
				params.put("spbill_create_ip", "127.0.0.1");
				params.put("notify_url", notify_url);
				params.put("trade_type", "JSAPI");
				String sign = PaymentKit.createSign(params, paternerKey);
				params.put("sign", sign);

				String xmlResult = PaymentApi.pushOrder(params);

				System.out.println(xmlResult);
				Map<String, String> result = PaymentKit.xmlToMap(xmlResult);

				String return_code = result.get("return_code");
				if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
					String return_msg = result.get("return_msg");
					System.out.println(return_msg);
					enrollResult.set("result", "ERROR" + return_msg);
					return false;
				}

				// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
				String prepay_id = result.get("prepay_id");
				// 封装调起微信支付的参数
				// https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12
				Map<String, String> packageParams = new HashMap<String, String>();
				packageParams.put("appid", appid);
				packageParams.put("partnerid", partner);
				packageParams.put("prepayid", prepay_id);
				packageParams.put("package", "Sign=WXPay");
				packageParams.put("noncestr", System.currentTimeMillis() + "");
				packageParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
				String packageSign = PaymentKit.createSign(packageParams, paternerKey);
				packageParams.put("sign", packageSign);
				String jsonStr = JsonUtils.toJson(packageParams);
				enrollResult.set("result", jsonStr);
				return true;
			}
		});
		return enrollResult.getStr("result");
	}

	public static void main(String[] args) {

		String appid = "wx4d59fce1314a52af";
		String partner = "1509461511";
		String paternerKey = "SIA8SbNby2aZGFkrBrzeuQEX5eSuuL6B";
		String notify_url = "https://walkvalue.com/wk/enrollCallback";

		// 统一下单地址
		// https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1#
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", appid);
		params.put("mch_id", partner);
		params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
		params.put("body", "test");
		params.put("attach", "custom json");
		params.put("out_trade_no", "20180712test001");
		params.put("total_fee", "1");
		params.put("spbill_create_ip", "127.0.0.1");
		params.put("notify_url", notify_url);
		params.put("openid", "oX53W5UenL3X12jLbqZV_pmX0kz0");
		params.put("trade_type", "JSAPI");
		String sign = PaymentKit.createSign(params, paternerKey);
		params.put("sign", sign);

		String xmlResult = PaymentApi.pushOrder(params);

		System.out.println(xmlResult);
		Map<String, String> result = PaymentKit.xmlToMap(xmlResult);

		String return_code = result.get("return_code");
		if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
			String return_msg = result.get("return_msg");
			System.out.println(return_msg);
		}

		// 以下字段在return_code 和result_code都为SUCCESS的时候有返回
		String prepay_id = result.get("prepay_id");
		System.out.println(prepay_id);
		// 封装调起微信支付的参数
		// https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_12
		Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appid", appid);
		packageParams.put("partnerid", partner);
		packageParams.put("prepayid", prepay_id);
		packageParams.put("package", "Sign=WXPay");
		packageParams.put("noncestr", System.currentTimeMillis() + "");
		packageParams.put("timestamp", System.currentTimeMillis() / 1000 + "");
		String packageSign = PaymentKit.createSign(packageParams, paternerKey);
		packageParams.put("sign", packageSign);
		String jsonStr = JsonUtils.toJson(packageParams);
		System.out.println(jsonStr);
	}

	public boolean enrollCallback(String out_trade_no, String transaction_id, String time_end) {

		boolean tx = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				try {
					String userId = "";
					String phaseNo = "";
					String id = "";
					String tbName = "";

					if (out_trade_no.contains("QG")) {
						userId = out_trade_no.substring(1, out_trade_no.indexOf("QG"));
						phaseNo = out_trade_no.substring(out_trade_no.indexOf("QG") + 2, out_trade_no.indexOf("ID"));
						id = out_trade_no.substring(out_trade_no.indexOf("ID") + 2, out_trade_no.length());
						tbName = ConsistentHash.getTbName(userId, TbConst.U_G_O_COUNTRY);
						// gameInfo
						GameInfoCountry gameInfo = GameInfoCountry.dao.findByIdLoadColumns(id,
								"id,entry_fee,sum_count,sum_amount");
						if (gameInfo == null) {
							return false;
						}
						// 分组
						int groupNo = (gameInfo.getSumCount() - 1) / 200 + 1;
						// U更新
						UserGameOrderCountry update = new UserGameOrderCountry();
						update.setGroupNo(String.valueOf(groupNo));
						update.setTradeNo(transaction_id);
						update.setPayTime(time_end);
						update.setPayStatus("1");
						update.setOrderId(out_trade_no);
						Db.update(tbName, "order_id", update.toRecord());

						UserGameOrderCountry uOrder = UserGameOrderCountry.dao
								.findFirst("select * from " + tbName + " where order_id=?", out_trade_no);
						GroupGameOrderCountry gOrder = new GroupGameOrderCountry();

						BeanKit.copyProperties(uOrder, gOrder);
						gOrder.setId(null);
						gOrder.setStatus("0");
						String gtbName = ConsistentHash.getTbName(phaseNo + groupNo, TbConst.G_G_O_COUNTRY);
						Db.save(gtbName, gOrder.toRecord());
						// 总报名人数+1，金额+
						GameInfoCountry updateGameInfo = new GameInfoCountry();
						Integer sumCount = gameInfo.getSumCount() + 1;
						updateGameInfo.setId(Integer.valueOf(id));
						updateGameInfo.setSumAmount(gameInfo.getSumAmount().add(gameInfo.getEntryFee()));
						updateGameInfo.setSumCount(sumCount);
						updateGameInfo.update();

						// 用户账户更新
						UserAccount userAccount = UserAccount.dao
								.findFirst("select id,sum_pay_count,sum_pay_amount,pay_country_count,pay_country_amount"
										+ " from user_account where user_id=?", userId);
						userAccount.setSumPayCount(userAccount.getSumPayCount() + 1);
						userAccount.setSumPayAmount(userAccount.getSumPayAmount().add(gameInfo.getEntryFee()));
						userAccount.setPayCountryCount(userAccount.getPayCountryCount() + 1);
						userAccount.setPayCountryAmount(userAccount.getPayCountryAmount().add(gameInfo.getEntryFee()));
						userAccount.update();

						// 系统用户追加
						addSysUser(sumCount, "QG", phaseNo, id);

					} else if (out_trade_no.contains("CS")) {
						userId = out_trade_no.substring(1, out_trade_no.indexOf("CS"));
						phaseNo = out_trade_no.substring(out_trade_no.indexOf("CS") + 2, out_trade_no.indexOf("ID"));
						id = out_trade_no.substring(out_trade_no.indexOf("ID") + 2, out_trade_no.length());
						tbName = ConsistentHash.getTbName(userId, TbConst.U_G_O_CITY);
						// gameInfo
						GameInfoCity gameInfo = GameInfoCity.dao.findByIdLoadColumns(id,
								"id,entry_fee,sum_count,sum_amount");
						if (gameInfo == null) {
							return false;
						}
						// 分组
						int groupNo = (gameInfo.getSumCount() - 1) / 200 + 1;

						// U更新
						UserGameOrderCity update = new UserGameOrderCity();
						update.setGroupNo(String.valueOf(groupNo));
						update.setTradeNo(transaction_id);
						update.setPayTime(time_end);
						update.setPayStatus("1");
						update.setOrderId(out_trade_no);
						Db.update(tbName, "order_id", update.toRecord());

						UserGameOrderCity uOrder = UserGameOrderCity.dao
								.findFirst("select * from " + tbName + " where order_id=?", out_trade_no);
						GroupGameOrderCity gOrder = new GroupGameOrderCity();
						BeanKit.copyProperties(uOrder, gOrder);
						gOrder.setId(null);
						gOrder.setStatus("0");
						String gtbName = ConsistentHash.getTbName(phaseNo + gOrder.getCityId() + groupNo,
								TbConst.G_G_O_CITY);
						Db.save(gtbName, gOrder.toRecord());
						// 总报名人数+1，金额+
						Integer sumCount = gameInfo.getSumCount() + 1;
						GameInfoCity updateGameInfo = new GameInfoCity();
						updateGameInfo.setId(Integer.valueOf(id));
						updateGameInfo.setSumAmount(gameInfo.getSumAmount().add(gameInfo.getEntryFee()));
						updateGameInfo.setSumCount(sumCount);
						updateGameInfo.update();

						// 用户账户更新
						UserAccount userAccount = UserAccount.dao
								.findFirst("select id,sum_pay_count,sum_pay_amount,pay_city_count,pay_city_amount"
										+ " from user_account where user_id=?", userId);
						userAccount.setSumPayCount(userAccount.getSumPayCount() + 1);
						userAccount.setSumPayAmount(userAccount.getSumPayAmount().add(gameInfo.getEntryFee()));
						userAccount.setPayCityCount(userAccount.getPayCityCount() + 1);
						userAccount.setPayCityAmount(userAccount.getPayCityAmount().add(gameInfo.getEntryFee()));
						userAccount.update();

						addSysUser(sumCount, "CS", phaseNo, id);
					} else if (out_trade_no.contains("TD")) {
						userId = out_trade_no.substring(1, out_trade_no.indexOf("TD"));
						phaseNo = out_trade_no.substring(out_trade_no.indexOf("TD") + 2, out_trade_no.indexOf("ID"));
						id = out_trade_no.substring(out_trade_no.indexOf("ID") + 2, out_trade_no.length());
						tbName = ConsistentHash.getTbName(userId, TbConst.U_G_O_TEAM);
						// gameInfo
						String teamTbName = ConsistentHash.getTbName(phaseNo, TbConst.G_I_TEAM);
						GameInfoTeam gameInfo = GameInfoTeam.dao.findFirst(
								"select entry_fee,sum_count,sum_amount from " + teamTbName + " where id=?", id);
						if (gameInfo == null) {
							return false;
						}
						// 分组
						int groupNo = (gameInfo.getSumCount() - 1) / 200 + 1;

						// U更新
						UserGameOrderTeam update = new UserGameOrderTeam();
						update.setGroupNo(String.valueOf(groupNo));
						update.setTradeNo(transaction_id);
						update.setPayTime(time_end);
						update.setPayStatus("1");
						update.setOrderId(out_trade_no);
						Db.update(tbName, "order_id", update.toRecord());

						UserGameOrderTeam uOrder = UserGameOrderTeam.dao
								.findFirst("select * from " + tbName + " where order_id=?", out_trade_no);
						GroupGameOrderTeam gOrder = new GroupGameOrderTeam();
						BeanKit.copyProperties(uOrder, gOrder);
						gOrder.setId(null);
						gOrder.setStatus("0");
						String gtbName = ConsistentHash.getTbName(phaseNo + gOrder.getTeamId() + groupNo,
								TbConst.G_G_O_TEAM);
						Db.save(gtbName, gOrder.toRecord());
						// 总报名人数+1，金额+
						Integer sumCount = gameInfo.getSumCount() + 1;
						GameInfoTeam updateGameInfo = new GameInfoTeam();
						updateGameInfo.setId(Integer.valueOf(id));
						updateGameInfo.setSumAmount(gameInfo.getSumAmount().add(gameInfo.getEntryFee()));
						updateGameInfo.setSumCount(sumCount);
						Db.update(teamTbName, updateGameInfo.toRecord());

						// 用户账户更新
						UserAccount userAccount = UserAccount.dao
								.findFirst("select id,sum_pay_count,sum_pay_amount,pay_team_count,pay_team_amount"
										+ " from user_account where user_id=?", userId);
						userAccount.setSumPayCount(userAccount.getSumPayCount() + 1);
						userAccount.setSumPayAmount(userAccount.getSumPayAmount().add(gameInfo.getEntryFee()));
						userAccount.setPayTeamCount(userAccount.getPayTeamCount() + 1);
						userAccount.setPayTeamAmount(userAccount.getPayTeamAmount().add(gameInfo.getEntryFee()));
						userAccount.update();
						
						addSysUser(sumCount, "TD", phaseNo, id);
					}

					// 报名次数+1
					UserInfo userInfo = UserInfo.dao.findFirst(
							"select id,enroll_count,max_enroll_count,from_user_id from user_info where user_id=?",
							userId);
					if (userInfo != null) {
						userInfo.setEnrollCount(userInfo.getEnrollCount() + 1);
						userInfo.update();

						Integer fromUserId = userInfo.getFromUserId();
						if (fromUserId != null) {
							Db.update("update user_info set max_enroll_count=max_enroll_count+1 where user_id=?",
									userInfo.getFromUserId());
						}
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});

		return tx;
	}

	private String getNm(Integer num) {
		while (num > 9) {
			num = num % 10; // 所报数的个位数
		}
		return num.toString();
	}

	private void addSysUser(Integer sumCount, String type, String phaseNo, String id) {
		String nm = getNm(sumCount);
		if ("3".equals(nm)) {
			Integer sysUserId = Db
					.queryFirst("select user_id from user_account where from_user_id=1 and pay_country_count=0");
			String orderId = "U" + sysUserId + "QG" + phaseNo + "ID" + id;
			// TODO 
			enrollCallback(orderId, "sys", DateUtils.getNowTime());
		}
	}
}
