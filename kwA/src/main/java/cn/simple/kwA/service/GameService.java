package cn.simple.kwA.service;

import java.util.List;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GameInfo;
import cn.simple.kwA.model.GameInfoCity;
import cn.simple.kwA.model.GameInfoCountry;
import cn.simple.kwA.model.GameInfoTeam;
import cn.simple.kwA.model.UserGameOrder;

public interface GameService {

	/**
	 * 获取全国赛信息
	 * 
	 * @param phaseNo
	 * @return
	 */
	GameInfoCountry getGameInfoCountry(String phaseNo);

	/**
	 * 获取城市赛列表信息
	 * 
	 * @param phaseNo
	 * @return
	 */
	List<GameInfoCity> getGameInfoCityList(String phaseNo, Integer userId);

	/**
	 * 获取某城市赛信息
	 * 
	 * @param id
	 * @return
	 */
	GameInfoCity getGameInfoCity(Integer id);

	/**
	 * 获取团队赛列表信息
	 * 
	 * @param phaseNo
	 * @param pageBean
	 * @return
	 */
	List<GameInfoTeam> getGameInfoTeamList(String phaseNo, Integer userId, PageBean pageBean);

	/**
	 * 获取某团队赛信息
	 * 
	 * @param id
	 * @param phaseNo
	 * @return
	 */
	GameInfoTeam getGameInfoTeam(Integer id, String phaseNo);

	/**
	 * 获取挑战赛列表信息
	 * 
	 * @param userId
	 * @param type(0:进行中、1:报名中、2:已报名)
	 * @return
	 */
	List<GameInfo> getGameInfoList(Integer userId, String type, PageBean pageBean);

	/**
	 * 获取挑战赛详情信息
	 * 
	 * @param phaseNo
	 * @param gameId
	 * @return
	 */
	GameInfo getGameInfo(String phaseNo, Integer gameId);

	
	
	/**
	 * 获取挑战赛列表信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @return
	 */
	List<GameInfo> getGameList(String phaseNo, PageBean pageBean);

	/**
	 * 获取挑战赛列表信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @return
	 */
	List<UserGameOrder> getUserGameList(Integer userId, String phaseNo, PageBean pageBean);
}
