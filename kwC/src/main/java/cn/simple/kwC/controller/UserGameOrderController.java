package cn.simple.kwC.controller;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;
import cn.simple.kwA.plugin.Inject.BY_NAME;
import cn.simple.kwA.service.GameService;
import cn.simple.kwA.service.UserGameOrderService;
import cn.simple.kwC.BaseAPIController;

/**
 * UserGame模块
 * 
 * @author may
 *
 */
public class UserGameOrderController extends BaseAPIController {

	@BY_NAME
	private GameService gameService;

	@BY_NAME
	private UserGameOrderService userGameOrderService;

	/**
	 * 个人进行中列表
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param index|页数|Integer|必填，默认0开始
	 * @param count|每页条数|Integer|必填，默认20条每页
	 * @title 个人进行中列表
	 * @respParam gameList|进行中列表|gameList|无记录:null
	 * @respBody
	 */
	public void ongoingList() {
		// 返回结果
		Record result = new Record();
		// 参数
		PageBean pageBean = getBean(PageBean.class);
		Integer userId = getParaToInt("userId");

//		// 1:全国、2:城市、3:团队
//		if ("1".equals(type)) {
//			List<UserGameOrderCountry> list = userGameOrderService.getGameOrderCountryList(userId, pageBean);
//			result.set("list", list);
//		} else if ("2".equals(type)) {
//			List<UserGameOrderCity> list = userGameOrderService.getGameOrderCityList(userId, pageBean);
//			result.set("list", list);
//		} else if ("3".equals(type)) {
//			List<UserGameOrderTeam> list = userGameOrderService.getGameOrderTeamList(userId, pageBean);
//			result.set("list", list);
//		}
		renderSuccess(result, "获取成功");
	}
	
	
	/**
	 * 参赛记录
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param type|类型（1:全国、2:城市、3:团队）|String|必填
	 * @param index|页数|Integer|必填，默认0开始
	 * @param count|每页条数|Integer|必填，默认20条每页
	 * @title 参赛记录
	 * @respParam list|参赛记录|list|无记录:null
	 * @respBody
	 */
	public void list() {
		// 返回结果
		Record result = new Record();
		// 参数
		PageBean pageBean = getBean(PageBean.class);
		Integer userId = getParaToInt("userId");
		String type = getPara("type");
		// 1:全国、2:城市、3:团队
		if ("1".equals(type)) {
			List<UserGameOrderCountry> list = userGameOrderService.getGameOrderCountryList(userId, pageBean);
			result.set("list", list);
		} else if ("2".equals(type)) {
			List<UserGameOrderCity> list = userGameOrderService.getGameOrderCityList(userId, pageBean);
			result.set("list", list);
		} else if ("3".equals(type)) {
			List<UserGameOrderTeam> list = userGameOrderService.getGameOrderTeamList(userId, pageBean);
			result.set("list", list);
		}
		renderSuccess(result, "获取成功");
	}

	/**
	 * 赛事详情
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param type|类型（1:全国、2:城市、3:团队）|String|必填
	 * @param id|id|Integer|必填
	 * @title 赛事详情
	 * @respParam info|赛事详情|info|无记录:null
	 * @respBody
	 */
	public void info() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		String type = getPara("type");
		Integer id = getParaToInt("id");
		// 1:全国、2:城市、3:团队
		if ("1".equals(type)) {
			UserGameOrderCountry info = userGameOrderService.getGameOrderCountry(userId, id);
			result.set("info", info);
		} else if ("2".equals(type)) {
			UserGameOrderCity info = userGameOrderService.getGameOrderCity(userId, id);
			result.set("info", info);
		} else if ("3".equals(type)) {
			UserGameOrderTeam info = userGameOrderService.getGameOrderTeam(userId, id);
			result.set("info", info);
		}
		renderSuccess(result, "获取成功");
	}
}
