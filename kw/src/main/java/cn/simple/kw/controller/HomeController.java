package cn.simple.kw.controller;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cn.simple.kw.BaseAPIController;
import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.GameOrder;
import cn.simple.kw.service.GameService;
import cn.simple.kw.service.GroupGameService;
import cn.simple.kw.service.SysService;
import cn.simple.kw.service.UserGameService;
import cn.simple.kw.service.UserService;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Autowired;
import cn.simple.kwA.utils.DateUtils;

/**
 * 首页模块
 * 
 * @author may
 *
 */
public class HomeController extends BaseAPIController {

	@Autowired
	UserService userService;

	@Autowired
	GameService gameService;

	@Autowired
	UserGameService userGameService;

	@Autowired
	GroupGameService groupGameService;

	@Autowired
	SysService sysService;

	/**
	 * @title 首页信息获取
	 * @param userId|用户Id|Integer|选填
	 * @respParam gameStatus|赛事状态（0:报名中、1:进行中、2:已结束）|String|必返
	 * @respParam gameList|赛事列表|List<model>|必返
	 * @respParam ~ gameId|赛事id|Integer|必返
	 * @respParam ~ iconUrl|赛事发起人头像|String|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ entryFee|报名费|List|必返
	 * @respParam ~ reachStep|达标步数|Integer|必返
	 * @respParam ~ entryAmount|总报名费|Integer|必返
	 * @respParam ~ entryCount|总报名人数|Integer|必返
	 * @respParam ~ avgAmount|平均红包|Decimal|必返
	 * @respBody { "status": "0", "data": { "gameStatus": "0", "gameList": [{
	 *           "gameId": 2, "iconUrl": "test", "title": "test", "entryFee": 2,
	 *           "reachStep": 10000, "entryAmount": 12, "entryCount": 13,
	 *           "avgAmount": 1.2 }] }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void index() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		PageBean pageBean = new PageBean();
		pageBean.setIndex(1);
		pageBean.setCount(10);

		// 根据userId判断报名次数
		// >0选中报名中、<=0选中进行中
		String gameStatus = "0";
		List<GameInfo> gameList = new ArrayList<>();
		Integer usableEnrollCount = 1;
		if (userId != null) {
			usableEnrollCount = userService.getUsableEnrollCount(userId);
		}
		
		if (usableEnrollCount > 0) {
			gameList = gameService.getGameList(DateUtils.getTomorrowYYYYMMDD(), null, pageBean);

		} else {
			gameStatus = "1";
			gameList = gameService.getGameList(DateUtils.getTodayYYYYMMDD(), null, pageBean);
		}

		// 返回值
		result.set("gameStatus", gameStatus);
		result.set("gameList", gameList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 赛事列表获取
	 * @param gameStatus|赛事状态（0:报名中、1:进行中、2:已结束）|String|必填
	 * @param keyword|搜索关键字，标题、步数、报名费|String|选填
	 * @param index|页数，默认1开始|Integer|必填
	 * @param count|每页条数，默认10条每页|Integer|必填
	 * @respParam gameList|赛事列表|List<model>|必返
	 * @respParam ~ gameId|赛事id|Integer|必返
	 * @respParam ~ iconUrl|赛事发起人头像|String|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ entryFee|报名费|List|必返
	 * @respParam ~ reachStep|达标步数|Integer|必返
	 * @respParam ~ entryAmount|总报名费|Integer|必返
	 * @respParam ~ entryCount|总报名人数|Integer|必返
	 * @respParam ~ avgAmount|平均红包|Decimal|必返
	 * @respBody { "status": "0", "data": { "gameList": [{ "gameId": 2,
	 *           "iconUrl": "test", "title": "test", "entryFee": 2, "reachStep":
	 *           10000, "entryAmount": 12, "entryCount": 13, "avgAmount": 1.2 }]
	 *           }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void gameList() {

		// 返回结果
		Record result = new Record();

		// 参数
		String gameStatus = getPara("gameStatus");
		String keyword = getPara("keyword");
		PageBean pageBean = getBean(PageBean.class, null);
		String phaseNo = "";
		// 0:报名中、1:进行中、2:已结束
		switch (gameStatus) {
		case "0":
			phaseNo = DateUtils.getTomorrowYYYYMMDD();
			break;
		case "1":
			phaseNo = DateUtils.getTodayYYYYMMDD();
			break;
		case "2":
			phaseNo = DateUtils.getYesterdayYYYYMMDD();
			break;
		default:
			phaseNo = DateUtils.getTomorrowYYYYMMDD();
			break;
		}

		// 获取进行中列表
		List<GameInfo> gameList = gameService.getGameList(phaseNo, keyword, pageBean);
		result.set("gameList", gameList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 赛事详情获取
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|赛事Id|Integer|必填
	 * @respParam enrollStatus|是否参赛（0:未参赛、1:参赛）|String|必返
	 * @respParam systemTime|系统时间|String|必返
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
	 * @respParam userGameOrder|用户参赛详情|model|非必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ gameId|赛事Id|Integer|必返
	 * @respParam ~ groupNo|小组号|String|必返
	 * @respParam ~ walkStep|提交步数|Integer|必返
	 * @respParam ~ amount|平均奖励金|String|必返
	 * @respParam ~ status|达标状态|String|必返
	 * @respParam iconList|参赛小组头像列表|List<String>|非必返
	 * @respBody { "status": "0", "data": { "userGameOrder": { "phaseNo":
	 *           "20180814", "gameId": 1, "groupNo": "1", "walkStep": 5300,
	 *           "amount": 2, "STATUS": "1" }, "iconList": ["头像"], "gameInfo": {
	 *           "gameId": 1, "nickName": "hello", "iconUrl": "test", "title":
	 *           "哈哈", "phaseNo": "20180814", "startTime": "00:00:00",
	 *           "endTime": "23:59:59", "entryFee": 2, "reachStep": 5000,
	 *           "entryCount": 5, "entryAmount": 10, "reachCount": 1,
	 *           "avgAmount": 10 }, "enrollStatus": "1" }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void gameInfo() {

		// 返回结果
		Record result = new Record();

		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		String enrollStatus = "0";
		GameInfo gameInfo = gameService.getGameInfo(gameId);
		// 获取用户相关信息
		GameOrder userGameOrder = userGameService.getUserGameOrder(userId, gameId);
		// 获取最新5个报名头像
		if (userGameOrder != null) {
			enrollStatus = "1";
			GameOrder gameOrder = new GameOrder().put(userGameOrder);
			List<String> iconList = groupGameService.getIconList(gameOrder.getPhaseNo(), gameId,
					gameOrder.getGroupNo());

			result.set("userGameOrder", userGameOrder);
			result.set("iconList", iconList);
		}
		result.set("gameInfo", gameInfo);
		result.set("enrollStatus", enrollStatus);
		result.set("systemTime", DateUtils.getNowTime());

		renderSuccess(result.toJson(), "获取成功");
	}
}
