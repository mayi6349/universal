package cn.simple.kwC.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Record;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GroupGameOrderCity;
import cn.simple.kwA.model.GroupGameOrderCountry;
import cn.simple.kwA.model.GroupGameOrderTeam;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;
import cn.simple.kwA.plugin.Inject.BY_NAME;
import cn.simple.kwA.service.GameService;
import cn.simple.kwA.service.GroupGameOrderService;
import cn.simple.kwA.service.UserGameOrderService;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwC.BaseAPIController;

/**
 * Game小组模块
 * 
 * @author may
 *
 */
public class GroupGameOrderController extends BaseAPIController {

	@BY_NAME
	private GameService gameService;

	@BY_NAME
	private UserGameOrderService userGameOrderService;

	@BY_NAME
	private GroupGameOrderService groupGameOrderService;

	/**
	 * 获取全国赛个人小组列表信息
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|必填，默认当天yyyymmdd
	 * @param groupNo|小组号|String|必填
	 * @param index|页数|Integer|必填
	 * @param count|每页条数|Integer|必填
	 * @title 获取全国赛个人小组列表信息
	 * @respParam user|个人信息|user|index=0时返回
	 * @respParam groupList|小组列表信息|groupList|必返
	 * @respBody
	 */
	public void getCountryGroup() {
		// 返回结果
		Record result = new Record();

		// 参数
		PageBean pageBean = getBean(PageBean.class);
		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		String groupNo = getPara("groupNo");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		if (pageBean.getIndex() == 1) {
			UserGameOrderCountry user = userGameOrderService.getGameOrderCountry(userId, phaseNo);
			result.set("user", user);
		}

		// 赛事列表
		List<GroupGameOrderCountry> groupList = groupGameOrderService.getCountryList(phaseNo, groupNo, pageBean);
		result.set("groupList", groupList);
		renderSuccess(result, "获取成功");
	}

	/**
	 * 获取某城市赛个人小组列表信息
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|必填，默认当天yyyymmdd
	 * @param groupNo|小组号|String|必填
	 * @param cityId|城市Id|Integer|必填
	 * @param index|页数|Integer|必填
	 * @param count|每页条数|Integer|必填
	 * @title 获取全国赛个人小组列表信息
	 * @respParam user|个人信息|user|index=0时返回
	 * @respParam groupList|小组列表信息|groupList|必返
	 * @respBody
	 */
	public void getCityGroup() {
		// 返回结果
		Record result = new Record();

		// 参数
		PageBean pageBean = getBean(PageBean.class);
		Integer userId = getParaToInt("userId");
		Integer cityId = getParaToInt("cityId");
		String phaseNo = getPara("phaseNo");
		String groupNo = getPara("groupNo");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		if (pageBean.getIndex() == 1) {
			UserGameOrderCity user = userGameOrderService.getGameOrderCity(userId, phaseNo, cityId);
			result.set("user", user);
		}

		// 赛事列表
		List<GroupGameOrderCity> groupList = groupGameOrderService.getCityList(phaseNo, cityId, groupNo, pageBean);
		result.set("groupList", groupList);
		renderSuccess(result, "获取成功");
	}

	/**
	 * 获取某团赛个人小组列表信息
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|必填，默认当天yyyymmdd
	 * @param groupNo|小组号|String|必填
	 * @param teamId|团Id|Integer|必填
	 * @param index|页数|Integer|必填
	 * @param count|每页条数|Integer|必填
	 * @title 获取团赛个人小组列表信息
	 * @respParam user|个人信息|user|index=0时返回
	 * @respParam groupList|小组列表信息|groupList|必返
	 * @respBody
	 */
	public void getTeamGroup() {
		// 返回结果
		Record result = new Record();

		// 参数
		PageBean pageBean = getBean(PageBean.class);
		Integer userId = getParaToInt("userId");
		Integer teamId = getParaToInt("teamId");
		String phaseNo = getPara("phaseNo");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		if (pageBean.getIndex() == 1) {
			UserGameOrderTeam user = userGameOrderService.getGameOrderTeam(userId, phaseNo, teamId);
			result.set("user", user);
		}

		// 赛事列表
		List<GroupGameOrderTeam> groupList = groupGameOrderService.getTeamList(phaseNo, teamId, pageBean);
		result.set("groupList", groupList);
		renderSuccess(result, "获取成功");
	}
}
