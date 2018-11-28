package cn.simple.kwC.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Record;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GameInfo;
import cn.simple.kwA.model.GameInfoCity;
import cn.simple.kwA.model.GameInfoCountry;
import cn.simple.kwA.model.GameInfoTeam;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;
import cn.simple.kwA.plugin.Inject.BY_NAME;
import cn.simple.kwA.service.GameService;
import cn.simple.kwA.service.UserGameOrderService;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwC.BaseAPIController;

/**
 * Game模块
 * 
 * @author may
 *
 */
public class GameController extends BaseAPIController {

	@BY_NAME
	private GameService gameService;

	@BY_NAME
	private UserGameOrderService userGameOrderService;

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
	 * 获取全国赛信息、个人参赛信息
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|选填，默认当天yyyymmdd
	 * @title 获取全国赛信息、个人参赛信息
	 * @respParam gameInfo|全国赛信息|GameInfoCountry|必返
	 * @respParam userGameInfo|用户参赛信息|userGameInfo|null显示下期报名
	 * @respBody
	 */
	public void gameCountryInfo() {
		// 返回结果
		Record result = new Record();

		// 参数
		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		// 挑战信息
		GameInfoCountry gameInfoCountry = gameService.getGameInfoCountry(phaseNo);

		// 相关个人挑战信息
		UserGameOrderCountry userGameOrderCountry = userGameOrderService.getGameOrderCountry(userId, phaseNo);

		result.set("gameInfo", gameInfoCountry);
		result.set("userGameInfo", userGameOrderCountry);
		renderSuccess(result, "获取成功");
	}

	/**
	 * 获取城市赛列表信息、个人参赛列表信息
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|选填，默认当天yyyymmdd
	 * @title 获取城市赛列表信息、个人参赛列表信息
	 * @respParam gameList|城市赛列表信息|List<GameInfoCity>|必返
	 * @respBody
	 */
	public void gameCityList() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		// 赛事列表
		List<GameInfoCity> gameInfoList = gameService.getGameInfoCityList(phaseNo, userId);

		result.set("gameList", gameInfoList);
		renderSuccess(result, "获取成功");
	}

	/**
	 * 获取某城市赛信息、个人参赛信息
	 * 
	 * @param id|城市赛表主键|Integer|必填
	 * @param userId|用户Id|Integer|必填
	 * @title 获取某城市赛信息、个人参赛信息
	 * @respParam gameInfo|某城市赛信息|GameInfoCity|必返
	 * @respParam userGameInfo|用户参赛信息|UserGameOrderCity|如果没有对象就是未参赛
	 * @respBody
	 */
	public void getCityGameInfo() {
		// 返回结果
		Record result = new Record();
		// 参数
		Integer id = getParaToInt("id");
		Integer userId = getParaToInt("userId");

		GameInfoCity gameInfo = gameService.getGameInfoCity(id);

		UserGameOrderCity userGameInfo = userGameOrderService.getGameOrderCity(userId, gameInfo.getPhaseNo(),
				gameInfo.getCityId());

		result.set("gameInfo", gameInfo);
		result.set("userGameInfo", userGameInfo);
		renderSuccess(result, "获取成功");
	}

	/**
	 * 获取团赛列表信息、个人参赛列表信息
	 * 
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|选填，默认当天yyyymmdd
	 * @param index|页数|Integer|必填，默认0开始
	 * @param count|每页条数|Integer|必填，默认20条每页
	 * @title 获取团赛列表信息、个人参赛列表信息
	 * @respParam gameList|城市赛列表信息|List<GameInfoCity>|必返
	 * @respBody
	 */
	public void getTeamGameList() {
		// 返回结果
		Record result = new Record();
		// 参数
		PageBean pageBean = getBean(PageBean.class);
		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		// 赛事列表
		List<GameInfoTeam> gameInfoList = gameService.getGameInfoTeamList(phaseNo, userId, pageBean);

		result.set("gameList", gameInfoList);
		renderSuccess(result, "获取成功");
	}

	/**
	 * 获取某团赛信息、个人参赛信息
	 * 
	 * @param id|团赛表主键|Integer|必填
	 * @param userId|用户Id|Integer|必填
	 * @param phaseNo|期号|String|选填，默认当天yyyymmdd
	 * @title 获取某城市赛信息、个人参赛信息
	 * @respParam gameInfo|某城市赛信息|GameInfoCity|必返
	 * @respParam userGameInfo|用户参赛信息|UserGameOrderCity|如果没有对象就是未参赛
	 * @respBody
	 */
	public void getTeamGameInfo() {
		// 返回结果
		Record result = new Record();
		Integer userId = getParaToInt("userId");
		String phaseNo = getPara("phaseNo");
		Integer id = getParaToInt("id");
		if (StringUtils.isEmpty(phaseNo)) {
			phaseNo = DateUtils.getCurrentDate();
		}

		GameInfoTeam gameInfo = gameService.getGameInfoTeam(id, phaseNo);
		UserGameOrderTeam userGameInfo = userGameOrderService.getGameOrderTeam(userId, phaseNo, gameInfo.getId());

		result.set("gameInfo", gameInfo);
		result.set("userGameInfo", userGameInfo);
		renderSuccess(result, "获取成功");
	}
}
