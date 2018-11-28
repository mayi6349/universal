package cn.simple.kw.controller;

import java.math.BigDecimal;
import java.util.List;

import com.jfinal.aop.Before;
import com.jfinal.ext.interceptor.GET;
import com.jfinal.plugin.activerecord.Record;

import cn.simple.kw.BaseAPIController;
import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model.UserAccountStream;
import cn.simple.kw.service.GameService;
import cn.simple.kw.service.GroupGameService;
import cn.simple.kw.service.SysService;
import cn.simple.kw.service.UserGameService;
import cn.simple.kw.service.UserService;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Autowired;
import cn.simple.kwA.utils.DateUtils;

/**
 * 奖励金模块
 * 
 * @author may
 *
 */
public class BountyController extends BaseAPIController {

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
	 * @title 奖励金初始页信息获取
	 * @param userId|用户Id|Integer|必填
	 * @param step|步数|Integer|必填
	 * @param enrollBack|报名跳转（1:报名跳转）|String|选填
	 * @respParam usableBalance|奖励金余额|BigDecimal|必返
	 * @respParam usableEnrollCount|可报名次数|Integer|必返
	 * @respParam tabType|选中tab类型（0:已报名、1:挑战中、2:奖励金明细）|String|必返
	 * @respParam streamList|明细|model|tabType=2时、必返
	 * @respParam ~ ioType|明细类型|String|必返
	 * @respParam ~ title|标题|String|必返
	 * @respParam ~ amount|金额|Integer|必返
	 * @respParam ~ createTime|支付时间|String|必返
	 * @respParam enrolledList|已报名列表|model|tabType=0时、必返
	 * @respParam ~ iconUrl|头像|String|必返
	 * @respParam ~ gameId|gameId|Integer|必返
	 * @respParam ~ title|标题|String|必返
	 * @respParam ~ reachStep|支付时间|Integer|必返
	 * @respParam ~ entryAmount|报名总额|BigDecimal|必返
	 * @respParam ~ entryCount|报名人数|Integer|必返
	 * @respParam ongoingList|进行中列表|model|tabType=1时、必返
	 * @respParam ~ iconUrl|头像|String|必返
	 * @respParam ~ gameId|gameId|Integer|必返
	 * @respParam ~ title|标题|String|必返
	 * @respParam ~ reachStep|达标步数|Integer|必返
	 * @respParam ~ amount|预期金额|Integer|必返
	 * @respParam ~ status|达标状态|Integer|必返
	 * @respBody { "status": "0", "data": { "usableBalance": 13,
	 *           "usableEnrollCount": 5, "tabType": "2", "streamList": [{
	 *           "ioType": "达标奖励金", "title": "哈哈", "amount": 2, "createTime":
	 *           "2018-08-15 22:47:25" }, { "ioType": "参赛报名费", "title": "哈哈",
	 *           "amount": 2, "createTime": "2018-08-14 19:12:40"
	 *           }],"enrolledList": [{ "iconUrl": "test", "gameId": 1, "title":
	 *           "哈哈", "reachStep": 5000, "entryAmount": 14, "entryCount": 7
	 *           }],"ongoingList": [{ "iconUrl": "test", "gameId": 1, "title":
	 *           "哈哈", "reachStep": 5000, "amount": 0, "status": "0" }] },
	 *           "info": "获取成功" }
	 */
	@Before(GET.class)
	public void index() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer step = getParaToInt("step");
		PageBean pageBean = new PageBean();
		pageBean.setIndex(1);
		pageBean.setCount(10);

		// header获取
		// 奖励金余额
		BigDecimal usableBalance = userService.getUsableBalance(userId);
		result.set("usableBalance", usableBalance);
		Integer usableEnrollCount = userService.getUsableEnrollCount(userId);
		result.set("usableEnrollCount", usableEnrollCount);

		// 挑战中条数判断
		Integer ongoingGameOrderCount = userService.getOngoingGameOrderCount(userId);
		if (ongoingGameOrderCount > 0 && !"1".equals(getPara("enrollBack"))) {
			List<GameOrder> ongoingGameOrderList = userGameService.getOngoingGameOrderList(userId, step, pageBean);
			result.set("tabType", "1");
			result.set("ongoingList", ongoingGameOrderList);
			renderSuccess(result.toJson(), "获取成功");
			return;
		}

		// 已报名条数判断
		Integer enrolledGameOrderCount = userService.getEnrolledGameOrderCount(userId);
		if (enrolledGameOrderCount > 0) {
			List<GameOrder> enrolledGameInfoList = userGameService.getEnrolledGameInfoList(userId, pageBean);
			result.set("tabType", "0");
			result.set("enrolledList", enrolledGameInfoList);
			renderSuccess(result.toJson(), "获取成功");
			return;
		}

		// 明细获取
		List<UserAccountStream> streamList = userService.getStreamList(userId, pageBean);
		result.set("tabType", "2");
		result.set("streamList", streamList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 奖励金明细列表获取
	 * @param userId|用户Id|Integer|必填
	 * @param index|页数|Integer|必填，默认1开始
	 * @param count|每页条数|Integer|必填，默认10条每页
	 * @respParam streamList|明细|model|必返
	 * @respParam ~ ioType|明细类型|String|必返
	 * @respParam ~ title|标题|String|必返
	 * @respParam ~ amount|金额|Integer|必返
	 * @respParam ~ createTime|支付时间|String|必返
	 * @respBody { "status": "0", "data": { "streamList": [{ "ioType": "达标奖励金",
	 *           "title": "哈哈", "amount": 2, "createTime": "2018-08-15 22:47:25"
	 *           }, { "ioType": "参赛报名费", "title": "哈哈", "amount": 2,
	 *           "createTime": "2018-08-14 19:12:40" }] }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void streamList() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		PageBean pageBean = getBean(PageBean.class, null);

		// 明细获取
		List<UserAccountStream> streamList = userService.getStreamList(userId, pageBean);
		result.set("streamList", streamList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 挑战中列表获取
	 * @param userId|用户Id|Integer|必填
	 * @param step|步数|Integer|必填
	 * @param index|页数|Integer|必填，默认1开始
	 * @param count|每页条数|Integer|必填，默认10条每页
	 * @respParam ongoingList|进行中列表|model|必返
	 * @respParam ~ iconUrl|明细类型|String|必返
	 * @respParam ~ gameId|标题|Integer|必返
	 * @respParam ~ title|金额|String|必返
	 * @respParam ~ reachStep|支付时间|Integer|必返
	 * @respParam ~ status|达标状态|Integer|必返
	 * @respParam ~ iconType|图标类型（1、2、3、4、5:全满蓝图）|String|必返
	 * @respBody { "status": "0", "data": { "ongoingList": [{ "iconUrl": "test",
	 *           "gameId": 1, "title": "哈哈", "reachStep": 5000, "amount": 0,
	 *           "status": "0" }] }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void ongoingList() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer step = getParaToInt("step");
		PageBean pageBean = getBean(PageBean.class, null);

		// 挑战中列表
		List<GameOrder> ongoingGameOrderList = userGameService.getOngoingGameOrderList(userId, step, pageBean);
		result.set("ongoingList", ongoingGameOrderList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 已报名列表获取
	 * @param userId|用户Id|Integer|必填
	 * @param index|页数|Integer|必填，默认1开始
	 * @param count|每页条数|Integer|必填，默认10条每页
	 * @respParam enrolledList|已报名列表|model|必返
	 * @respParam ~ iconUrl|明细类型|String|必返
	 * @respParam ~ gameId|标题|Integer|必返
	 * @respParam ~ title|金额|String|必返
	 * @respParam ~ reachStep|支付时间|Integer|必返
	 * @respParam ~ entryAmount|支付时间|BigDecimal|必返
	 * @respParam ~ entryCount|报名人数|Integer|必返
	 * @respBody { "status": "0", "data": { "tabType": "0", "enrolledList": [{
	 *           "iconUrl": "test", "gameId": 1, "title": "哈哈", "reachStep":
	 *           5000, "entryAmount": 14, "entryCount": 7 }] }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void enrolledList() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		PageBean pageBean = getBean(PageBean.class, null);

		// 已报名列表
		List<GameOrder> enrolledGameInfoList = userGameService.getEnrolledGameInfoList(userId, pageBean);
		result.set("tabType", "0");
		result.set("enrolledList", enrolledGameInfoList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 详情获取
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|赛事Id|Integer|必填
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
	 * @respBody { "status": "0", "data": { "gameInfo": { "gameId": 1,
	 *           "nickName": "hello", "iconUrl": "test", "title": "哈哈",
	 *           "phaseNo": "20180814", "startTime": "00:00:00", "endTime":
	 *           "23:59:59", "entryFee": 2, "reachStep": 5000, "entryCount": 7,
	 *           "entryAmount": 14, "reachCount": 1, "avgAmount": 14 },
	 *           "userGameOrder": { "phaseNo": "20180821", "gameId": 1,
	 *           "groupNo": "1", "walkStep": 0, "amount": 0, "status": "0" },
	 *           "iconList": ["test"] }, "info": "获取成功" }
	 */
	@Before(GET.class)
	public void detail() {

		// 返回结果
		Record result = new Record();

		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		GameInfo gameInfo = gameService.getGameInfo(gameId);

		// 获取用户相关信息
		GameOrder userGameOrder = userGameService.getUserGameOrder(userId, gameId);
		GameOrder order = new GameOrder().put(userGameOrder);
		// 获取最新5个报名头像
		List<String> iconList = groupGameService.getIconList(order.getPhaseNo(), gameId, order.getGroupNo());

		result.set("systemTime", DateUtils.getNowTime());
		result.set("gameInfo", gameInfo);
		result.set("userGameOrder", userGameOrder);
		result.set("iconList", iconList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 小组详情获取
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|赛事Id|Integer|必填
	 * @param groupNo|小组No|String|必填
	 * @respParam gameInfo|总赛事详情|model|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ entryCount|总报名人数|Integer|必返
	 * @respParam ~ entryAmount|总报名费用|Decimal|必返
	 * @respParam ~ avgAmount|平均奖励金|Decimal|必返
	 * @respParam groupInfo|小组详情|model|必返
	 * @respParam ~ entryCount|总报名人数|Integer|必返
	 * @respParam ~ entryAmount|总报名费用|Decimal|必返
	 * @respParam ~ avgAmount|平均奖励金|Decimal|必返
	 * @respParam ~ reachCount|小组达标人数|String|必返
	 * @respParam groupUserList|参赛小组用户列表|List<model>|必返
	 * @respParam ~ no|排名|Integer|必返
	 * @respParam ~ userId|用户Id|Integer|必返
	 * @respParam ~ nickName|发起人昵称|String|必返
	 * @respParam ~ iconUrl|发起人头像|String|必返
	 * @respParam ~ walkStep|提交步数|Integer|必返
	 * @respParam ~ reportTime|提交时间差|String|必返
	 * @respParam ~ status|达标状态（0:未达标，1:达标，2:未同步）|String|必返
	 * @respBody {"status":"0","data":{"gameInfo":{"title":"我的标语最燃","phaseNo":
	 *           "20180829","entryCount":5,"entryAmount":10,"avgAmount":5},
	 *           "groupInfo":{"entryCount":3,"entryAmount":6,"amount":3},
	 *           "userInfo":{"no":3,"nickName":"神秘人","iconUrl":
	 *           "https://www.walkvalue.com/icon/w.png","walkStep":0,
	 *           "reportTime":null},"userList":[{"no":1,"userId":1,"nickName":
	 *           "运**金","iconUrl":"https://www.walkvalue.com/icon/w.png",
	 *           "walkStep":6403,"reportTime":null},{"no":2,"userId":0,
	 *           "nickName":"运**金","iconUrl":
	 *           "https://www.walkvalue.com/icon/w.png","walkStep":5929,
	 *           "reportTime":null},{"no":3,"userId":6,"nickName":"神秘人",
	 *           "iconUrl":"https://www.walkvalue.com/icon/w.png","walkStep":0,
	 *           "reportTime":null}]},"info":"获取成功"}
	 */
	@Before(GET.class)
	public void groupIndex() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		String groupNo = getPara("groupNo");
		PageBean pageBean = new PageBean();
		pageBean.setIndex(1);
		pageBean.setCount(100);
		// 总赛况
		GameInfo gameInfo = gameService.getGameHeadInfo(gameId);
		// 小组赛况
		GameOrder groupInfo = groupGameService.getGroupHeadInfo(gameId, groupNo);
		Record record = groupInfo.toRecord();
		record.set("reachCount", "已达标" + groupGameService.getGroupReachCount(gameId, groupNo) + "人");
		// 个人赛况
		GameOrder userInfo = groupGameService.getUser(userId, gameId, groupNo);
		// 获取最新5个报名头像
		List<GameOrder> userList = groupGameService.getUserList(userId, gameId, groupNo, pageBean);
		result.set("gameInfo", gameInfo);
		result.set("groupInfo", record);
		result.set("userInfo", userInfo);
		result.set("userList", userList);
		renderSuccess(result.toJson(), "获取成功");

	}

	/**
	 * @title 小组用户列表获取
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|赛事Id|Integer|必填
	 * @param groupNo|小组No|String|必填
	 * @param index|页数|Integer|必填，默认1开始
	 * @param count|每页条数|Integer|必填，默认10条每页
	 * @respParam userList|参赛小组用户列表|List<model>|必返
	 * @respParam ~ no|排名|Integer|必返
	 * @respParam ~ userId|用户Id|Integer|必返
	 * @respParam ~ nickName|发起人昵称|String|必返
	 * @respParam ~ iconUrl|发起人头像|String|必返
	 * @respParam ~ walkStep|提交步数|Integer|必返
	 * @respParam ~ reportTime|提交时间差|String|必返
	 * @respBody
	 */
	@Before(GET.class)
	public void groupUserList() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		String groupNo = getPara("groupNo");
		PageBean pageBean = getBean(PageBean.class, null);

		// 获取用户相关信息
		List<GameOrder> userList = groupGameService.getUserList(userId, gameId, groupNo, pageBean);
		result.set("userList", userList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 最新报名成员初始数据获取
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|赛事Id|Integer|必填
	 * @respParam gameInfo|总赛事详情|model|必返
	 * @respParam ~ title|赛事标题|String|必返
	 * @respParam ~ phaseNo|期号|String|必返
	 * @respParam ~ entryCount|总报名人数|Integer|必返
	 * @respParam ~ entryAmount|总报名费用|Decimal|必返
	 * @respParam ~ avgAmount|平均奖励金|Decimal|必返
	 * @respParam userList|参赛小组用户列表|List<model>|必返
	 * @respParam ~ userId|用户Id|Integer|必返
	 * @respParam ~ nickName|发起人昵称|String|必返
	 * @respParam ~ iconUrl|发起人头像|String|必返
	 * @respParam ~ enrollTime|报名时间差|String|必返
	 * @respBody
	 */
	@Before(GET.class)
	public void groupEnrollIndex() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		PageBean pageBean = new PageBean();
		pageBean.setIndex(1);
		pageBean.setCount(20);

		// 总赛况
		GameInfo gameInfo = gameService.getGameHeadInfo(gameId);
		// 获取用户相关信息
		List<GameOrder> userList = gameService.getEnrollUserList(userId, gameId, pageBean);
		result.set("gameInfo", gameInfo);
		result.set("userList", userList);
		renderSuccess(result.toJson(), "获取成功");
	}

	/**
	 * @title 最新报名成员列表获取
	 * @param userId|用户Id|Integer|必填
	 * @param gameId|赛事Id|Integer|必填
	 * @param index|页数|Integer|必填，默认1开始
	 * @param count|每页条数|Integer|必填，默认10条每页
	 * @respParam enrollUserList|参赛小组用户列表|List<model>|必返
	 * @respParam ~ userId|用户Id|Integer|必返
	 * @respParam ~ nickName|发起人昵称|String|必返
	 * @respParam ~ iconUrl|发起人头像|String|必返
	 * @respParam ~ enrollTime|报名时间差|String|必返
	 * @respBody
	 */
	@Before(GET.class)
	public void groupEnrollUserList() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		Integer gameId = getParaToInt("gameId");
		PageBean pageBean = getBean(PageBean.class, null);

		// 获取用户相关信息
		List<GameOrder> userList = gameService.getEnrollUserList(userId, gameId, pageBean);
		result.set("userList", userList);
		renderSuccess(result.toJson(), "获取成功");
	}
}
