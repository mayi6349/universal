package cn.simple.kwC.controller;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.SnsAccessToken;
import com.jfinal.weixin.sdk.api.SnsAccessTokenApi;
import com.jfinal.weixin.sdk.api.SnsApi;
import com.jfinal.weixin.sdk.api.UserApi;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GameInfo;
import cn.simple.kwA.model.UserGameOrder;
import cn.simple.kwA.model.UserInfo;
import cn.simple.kwA.plugin.Inject.BY_NAME;
import cn.simple.kwA.service.GameService;
import cn.simple.kwA.service.SysService;
import cn.simple.kwA.service.UserGameOrderService;
import cn.simple.kwA.service.UserService;
import cn.simple.kwC.BaseAPIController;

/**
 * 首页模块
 * 
 * @author may
 *
 */
public class HomeController extends BaseAPIController {

	@BY_NAME
	private UserService userService;
	
	@BY_NAME
	private UserGameOrderService userGameOrderService;

	@BY_NAME
	private GameService gameService;

	@BY_NAME
	private SysService sysService;

	/**
	 * 首页触发
	 */
	public void index() {
		String authorizeURL = sysService.getAuthorizeURL();
		renderSuccess(authorizeURL, "认证地址获取成功");
	}

	/**
	 * 登陆
	 */
	public void login() {

		// 返回结果
		Record result = new Record();

		int subscribe = 0;
		// 用户同意授权，获取code
		String code = getPara("code");
		// String state = getPara("state");
		if (code != null) {
			String appId = ApiConfigKit.getApiConfig().getAppId();
			String secret = ApiConfigKit.getApiConfig().getAppSecret();
			// 通过code换取网页授权access_token
			SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(appId, secret, code);
			// String json=snsAccessToken.getJson();
			String token = snsAccessToken.getAccessToken();
			String openId = snsAccessToken.getOpenid();
			UserInfo user = userService.getUserInfoByWxOpenId(openId);

			// 拉取用户信息(需scope为 snsapi_userinfo)
			ApiResult apiResult = SnsApi.getUserInfo(token, openId);
			if (apiResult.isSucceed()) {
				JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
				String nickName = jsonObject.getString("nickname");
				// 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
				int sex = jsonObject.getIntValue("sex");
				String headimgurl = jsonObject.getString("headimgurl");

				// String city = jsonObject.getString("city");// 城市
				// String province = jsonObject.getString("province");// 省份
				// String country = jsonObject.getString("country");// 国家
				// String unionid = jsonObject.getString("unionid");

				// 获取用户信息判断是否关注
				ApiResult userInfo = UserApi.getUserInfo(openId);
				if (userInfo.isSucceed()) {
					String userStr = userInfo.toString();
					subscribe = JSON.parseObject(userStr).getIntValue("subscribe");
				}

				if (user == null) {
					UserInfo newUser = new UserInfo();
					newUser.setNickName(nickName);
					newUser.setSex(sex);
					newUser.setIconUrl(headimgurl);
					newUser.setWxOpenId(openId);
					newUser.setWxAuthStatus("1");
					newUser.setLastLoginTime(new Date());
					newUser.setWxFollowStatus(String.valueOf(subscribe));
					userService.addUserInfo(newUser);
					user = userService.getUserInfoByWxOpenId(openId);

					PageBean pageBean = new PageBean();
					pageBean.setCount(10);
					pageBean.setIndex(1);
					List<GameInfo> gameInfoList = gameService.getGameInfoList(user.getUserId(), "1", pageBean);
					result.set("listType", "1");
					result.set("gameInfoList", gameInfoList);
				} else {
					user.setNickName(nickName);
					user.setSex(sex);
					user.setIconUrl(headimgurl);
					user.setWxOpenId(openId);
					user.setLastLoginTime(new Date());
					user.setWxFollowStatus(String.valueOf(subscribe));
					userService.updateUserInfo(user);

					PageBean pageBean = new PageBean();
					pageBean.setCount(10);
					pageBean.setIndex(1);
					List<GameInfo> gameInfoList = gameService.getGameInfoList(user.getUserId(), "0", pageBean);
					if (gameInfoList != null) {
						result.set("listType", "0");
						result.set("gameInfoList", gameInfoList);
					} else {
						gameInfoList = gameService.getGameInfoList(user.getUserId(), "1", pageBean);
						result.set("listType", "1");
						result.set("gameInfoList", gameInfoList);
					}
				}
			}
			renderSuccess(result, "登陆成功");
		} else {
			renderError("code不能为空");
		}
	}

	/**
	 * 首页
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @title 跳转首页
	 * @respParam listType|列表初始选中|listType|0:进行中，1:报名中，2:已报名
	 * @respParam gameInfoList|列表内容|gameInfoList|列表
	 * @respBody
	 */
	public void home() {

		// 返回结果
		Record result = new Record();

		Integer userId = getParaToInt("userId");

		PageBean pageBean = new PageBean();
		pageBean.setCount(10);
		pageBean.setIndex(1);
		// 获取进行中列表
		List<GameInfo> gameInfoList = gameService.getGameInfoList(userId, "0", pageBean);
		if (gameInfoList != null) {
			result.set("listType", "0");
			result.set("gameInfoList", gameInfoList);
			// 获取报名中列表
		} else {
			gameInfoList = gameService.getGameInfoList(userId, "1", pageBean);
			result.set("listType", "1");
			result.set("gameInfoList", gameInfoList);
		}

		renderSuccess(result, "获取成功");
	}

	/**
	 * 列表获取
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param listType|列表类型|String|必填
	 * @param index|页数|Integer|必填，默认0开始
	 * @param count|每页条数|Integer|必填，默认20条每页
	 * @title 列表获取
	 * @respParam gameInfoList|列表内容|gameInfoList|列表
	 * @respBody
	 */
	public void gameList() {

		// 返回结果
		Record result = new Record();

		Integer userId = getParaToInt("userId");
		String listType = getPara("listType");
		PageBean pageBean = getBean(PageBean.class);

		// 获取进行中列表
		List<GameInfo> gameInfoList = gameService.getGameInfoList(userId, listType, pageBean);
		result.set("gameInfoList", gameInfoList);
		renderSuccess(result, "获取成功");
	}
	
	/**
	 * 挑战详情
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|必填
	 * @param gameId|挑战id|Integer|必填
	 * @title 详情获取
	 * @respParam enrollStatus|报名状态|enrollStatus|是否报名（0:未报名，1:已报名）
	 * @respParam gameInfo|详情内容|gameInfo|详情
	 * @respBody
	 */
	public void gameInfo() {

		// 返回结果
		Record result = new Record();

		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		Integer gameId = getParaToInt("gameId");

		// 获取进行中列表
		GameInfo gameInfo = gameService.getGameInfo(phaseNo, gameId);
		result.set("gameInfo", gameInfo);
		UserGameOrder gameOrder = userGameOrderService.getGameOrder(userId, phaseNo, gameId);
		result.set("gameOrder", gameOrder);
		renderSuccess(result, "获取成功");
	}
	
	/**
	 * 小组详情
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|必填
	 * @param gameId|挑战id|Integer|必填
	 * @title 详情获取
	 * @respParam enrollStatus|报名状态|enrollStatus|是否报名（0:未报名，1:已报名）
	 * @respParam gameInfo|详情内容|gameInfo|详情
	 * @respBody
	 */
	public void groupInfo() {

		// 返回结果
		Record result = new Record();

		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		Integer gameId = getParaToInt("gameId");

		// 获取进行中列表
		GameInfo gameInfo = gameService.getGameInfo(phaseNo, gameId);
		result.set("gameInfo", gameInfo);
		UserGameOrder gameOrder = userGameOrderService.getGameOrder(userId, phaseNo, gameId);
		result.set("gameOrder", gameOrder);
		renderSuccess(result, "获取成功");
	}
	
	/**
	 * 步数上传
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param step|步数|Integer|必填
	 * @title 获取个人参赛信息
	 * @respParam streamList|红包明细|streamList|必返
	 * @respBody
	 */
	public void reportStep() {

		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer step = getParaToInt("step");
		sysService.reportStep(userId, step);
		renderSuccess(result, "处理成功");
	}

	/**
	 * 报名
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param type|类型（0:全国，1:城市，2:团队）|String|必填
	 * @param enrollId|报名Id|String|必填
	 * @title 获取个人参赛信息
	 * @respParam streamList|红包明细|streamList|必返
	 * @respBody
	 */
	public void enroll() {

		// 参数
		Integer userId = getParaToInt("userId");
		Integer enrollId = getParaToInt("id");
		String type = getPara("type");

		Record enrollCheck = userService.enrollCheck(userId);
		if (!enrollCheck.getBoolean("result")) {
			renderError(enrollCheck.getStr("msg"));
			return;
		}

		String payInfo = sysService.enroll(userId, type, enrollId);
		if (payInfo.contains("ERROR")) {
			renderError(payInfo);
		} else {
			renderSuccess(payInfo, "信息获取成功");
		}
	}
}
