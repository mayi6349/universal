package cn.simple.kw.controller;

import java.text.NumberFormat;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.plugin.activerecord.Record;

import cn.simple.kw.BaseAPIController;
import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model.UserInfo;
import cn.simple.kw.service.GameService;
import cn.simple.kw.service.GroupGameService;
import cn.simple.kw.service.SysService;
import cn.simple.kw.service.UserGameService;
import cn.simple.kw.service.UserService;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Autowired;

/**
 * User模块
 * 
 * @author may
 *
 */
public class UserController extends BaseAPIController {

	@Autowired
	UserService userService;

	@Autowired
	UserGameService userGameService;

	@Autowired
	GroupGameService groupGameService;

	@Autowired
	GameService gameService;

	@Autowired
	SysService sysService;

	/**
	 * @title 跳转我的
	 * @param userId|用户Id|Integer|必填
	 * @respParam nickName|昵称|String|必返
	 * @respParam iconUrl|头像|String|必返
	 * @respParam usableBalance|奖励金余额|BigDecimal|必返
	 * @respParam helpUserCount|助力小伙伴人数|Integer|必返
	 * @respParam joinCount|总参赛次数|Integer|必返
	 * @respParam reachCount|总达标次数|Integer|必返
	 * @respParam per|达标率|String|必返
	 * @respParam usableEnrollcount|可报名次数|Integer|必返
	 * @respBody {"status":"0","data":{"nickName":null,"iconUrl":null,
	 *           "helpUserCount":null,"usablebalance":null,"joincount":0,
	 *           "reachcount":0,"per":"0","usableenrollcount":0},"info":"获取成功"}
	 */
	@Before(GET.class)
	public void index() {

		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");

		// 头像、昵称、助力小伙伴人数
		UserInfo userBaseInfo = userService.getUserBaseInfo(userId);
		result.setColumns(userBaseInfo);
		// 奖励金余额
		result.set("usableBalance", userService.getUsableBalance(userId));
		// 总参赛次数
		Integer gameOrderCount = userService.getGameOrderCount(userId);
		result.set("joinCount", gameOrderCount);
		// 总达标次数
		Integer reachGameOrderCount = userService.getReachGameOrderCount(userId);
		result.set("reachCount", reachGameOrderCount);
		// 达标率
		String percentum = "0";
		if (gameOrderCount > 0) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			// 设置精确到小数点后1位
			numberFormat.setMaximumFractionDigits(1);
			percentum = numberFormat.format((float) reachGameOrderCount / (float) gameOrderCount * 100) + "%";
		}
		result.set("per", percentum);
		// 可报名次数
		result.set("usableEnrollCount", userService.getUsableEnrollCount(userId));
		// 已报名次数
		result.set("enrolledCount", userService.getEnrolledGameOrderCount(userId));
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 报名次数详情获取
	 * @param userId|用户Id|Integer|必填
	 * @respParam baseEnrollCount|基本报名次数|Integer|必返
	 * @respParam helpEnrollCount|助力报名次数|Integer|必返
	 * @respParam EnrolledCount|已报名次数|Integer|必返
	 * @respBody { "status": "0", "data": { "baseEnrollCount": 5,
	 *           "helpEnrollCount": 0, "enrolledCount": 1 }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void enrollInfo() {
		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");

		// 报名次数基本信息
		result.setColumns(userService.getUserEnrollCountInfo(userId));
		// 已报名次数
		result.set("enrolledCount", userService.getEnrolledGameOrderCount(userId));
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 挑战记录初始获取
	 * @param userId|用户Id|Integer|必填
	 * @respParam usableBalance|奖励金余额|BigDecimal|必返
	 * @respParam joinCount|总参赛次数|Integer|必返
	 * @respParam reachCount|总达标次数|Integer|必返
	 * @respParam per|达标率|String|必返
	 * @respParam ongoingStatus|有无进行中挑战（0:无、1:有）|String|必返
	 * @respParam gameOrderList|挑战记录|model|必返
	 * @respParam ~ id|赛事Id|Integer|必返
	 * @respParam ~ gameId|挑战赛Id|Integer|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ groupNo|小组号|String|必返
	 * @respParam ~ walkStep|提交步数|Integer|必返
	 * @respParam ~ amount|达标奖励金|Becimal|必返
	 * @respParam ~ status|达标状态|Integer|必返
	 * @respBody { "status": "0", "data": { "usableBalance": 11, "joinCount": 3,
	 *           "reachCount": 0, "per": "0%", "ongoingStatus": "1",
	 *           "gameOrderList": [{ "id": 1000010, "gameId": 1, "phaseNo":
	 *           "20180820", "title": "哈哈", "groupNo": "1", "walkStep": 0,
	 *           "amount": 0, "status": "0" }] }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void orderIndex() {

		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");
		PageBean pageBean = new PageBean();
		pageBean.setIndex(1);
		pageBean.setCount(10);

		// 奖励金余额
		result.set("usableBalance", userService.getUsableBalance(userId));
		// 总参赛次数
		Integer gameOrderCount = userService.getGameOrderCount(userId);
		result.set("joinCount", gameOrderCount);
		// 总达标次数
		Integer reachGameOrderCount = userService.getReachGameOrderCount(userId);
		result.set("reachCount", reachGameOrderCount);
		// 达标率
		String percentum = "0";
		if (gameOrderCount > 0) {
			NumberFormat numberFormat = NumberFormat.getInstance();
			// 设置精确到小数点后1位
			numberFormat.setMaximumFractionDigits(1);
			percentum = numberFormat.format((float) reachGameOrderCount / (float) gameOrderCount * 100) + "%";
		}
		result.set("per", percentum);
		// 有无挑战中或已报名的挑战
		String ongoingStatus = "0";
		Integer ongoingGameOrderCount = userService.getOngoingGameOrderCount(userId);
		Integer enrolledGameOrderCount = userService.getEnrolledGameOrderCount(userId);
		if (ongoingGameOrderCount + enrolledGameOrderCount > 0) {
			ongoingStatus = "1";
		}
		result.set("ongoingStatus", ongoingStatus);

		// 今日之前挑战记录
		List<GameOrder> gameOrderList = userGameService.getUserGameOrderList(userId, pageBean);
		result.set("gameOrderList", gameOrderList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 挑战记录获取
	 * @param userId|用户Id|Integer|必填
	 * @param index|页数|Integer|必填，默认0开始
	 * @param count|每页条数|Integer|必填，默认20条每页
	 * @respParam gameOrderList|挑战记录|model|必返
	 * @respParam ~ id|赛事Id|Integer|必返
	 * @respParam ~ gameId|挑战赛Id|Integer|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ groupNo|小组号|String|必返
	 * @respParam ~ walkStep|提交步数|Integer|必返
	 * @respParam ~ amount|达标奖励金|Becimal|必返
	 * @respParam ~ status|达标状态|Integer|必返
	 * @respBody { "status": "0", "data": { "gameOrderList": [{ "id": 1000010,
	 *           "gameId": 1, "phaseNo": "20180820", "title": "哈哈", "groupNo":
	 *           "1", "walkStep": 0, "amount": 0, "status": "0" }] }, "info":
	 *           "获取成功" }
	 */
	@Before(GET.class)
	public void gameOrderList() {

		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");
		PageBean pageBean = getBean(PageBean.class, null);

		// 今日之前挑战记录
		List<GameOrder> gameOrderList = userGameService.getUserGameOrderList(userId, pageBean);
		result.set("gameOrderList", gameOrderList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 记录详情信息
	 * @param userId|用户Id|Integer|必填
	 * @param id|id|Integer|必填
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
	 * @respParam userGameOrder|赛况详情|model|必返
	 * @respParam ~ gameId|挑战赛Id|Integer|必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ groupNo|小组号|String|必返
	 * @respParam ~ walkStep|提交步数|Integer|必返
	 * @respParam ~ amount|达标奖励金|Becimal|必返
	 * @respParam ~ status|达标状态|Integer|必返
	 * @respParam iconList|参赛小组头像列表|List<String>|非必返
	 * @respBody { "status": "0", "data": { "gameInfo": { "gameId": 77,
	 *           "userId": 0, "nickName": "运动奖励金", "iconUrl":
	 *           "https://www.walkvalue.com/icon/w.png", "title":
	 *           "20180917-挑战不间断", "phaseNo": "20180917", "startTime":
	 *           "00:00:00", "endTime": "23:59:59", "entryFee": "2.00",
	 *           "reachStep": 5000, "entryCount": 1003, "entryAmount":
	 *           "2006.00", "reachCount": 1002, "avgAmount": "2.00" },
	 *           "iconList":
	 *           ["https://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83eoYVbuugBedndyE9XwoTTn0zh83nvKrdJBSgmsAMtWXVqz1n3ga4TIjz4AxCt5p22V4zxzsMacM1g/132"
	 *           ], "userGameOrder": { "phaseNo": "20180917", "gameId": 77,
	 *           "groupNo": "5", "walkStep": 1216, "amount": "0.00", "status":
	 *           "0" } }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void gameOrder() {

		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");
		Integer id = getParaToInt("id");

		GameOrder gameOrder = userGameService.getUserGameOrderById(userId, id);

		// 获取最新5个报名头像
		if (gameOrder != null) {
			Integer gameId = gameOrder.getGameId();
			String phaseNo = gameOrder.getPhaseNo();
			String groupNo = gameOrder.getGroupNo();
			GameInfo gameInfo = gameService.getGameInfo(gameId);
			List<String> iconList = groupGameService.getIconList(phaseNo, gameId, groupNo);
			result.set("gameInfo", gameInfo);
			result.set("iconList", iconList);
		}

		result.set("userGameOrder", gameOrder);
		renderSuccess(result.toJson(), "获取成功");
	}
}
