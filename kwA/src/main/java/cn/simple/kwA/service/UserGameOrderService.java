package cn.simple.kwA.service;

import java.util.List;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.UserGameOrder;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;

public interface UserGameOrderService {

	/**
	 * 获取已参赛的全国赛订单列表
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 */
	List<UserGameOrderCountry> getGameOrderCountryList(Integer userId, PageBean pageBean);

	/**
	 * 获取已参赛的全国参赛信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @return
	 */
	UserGameOrderCountry getGameOrderCountry(Integer userId, String phaseNo);

	/**
	 * 获取已参赛的全国参赛信息
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	UserGameOrderCountry getGameOrderCountry(Integer userId, Integer id);

	/**
	 * 获取已参赛的城市赛订单列表
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 */
	List<UserGameOrderCity> getGameOrderCityList(Integer userId, PageBean pageBean);

	/**
	 * 获取已参赛的城市参赛信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @param cityId
	 * @return
	 */
	UserGameOrderCity getGameOrderCity(Integer userId, String phaseNo, Integer cityId);

	/**
	 * 获取已参赛的城市参赛信息
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	UserGameOrderCity getGameOrderCity(Integer userId, Integer id);

	/**
	 * 获取已参赛的团队赛订单列表信息
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 */
	List<UserGameOrderTeam> getGameOrderTeamList(Integer userId, PageBean pageBean);

	/**
	 * 获取已参赛的团队赛信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @param teamId
	 * @return
	 */
	UserGameOrderTeam getGameOrderTeam(Integer userId, String phaseNo, Integer teamId);

	/**
	 * 获取已参赛的团队赛信息
	 * 
	 * @param userId
	 * @param id
	 * @return
	 */
	UserGameOrderTeam getGameOrderTeam(Integer userId, Integer id);

	/**
	 * 获取已参赛的信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @param gameId
	 * @return
	 */
	UserGameOrder getGameOrder(Integer userId, String phaseNo, Integer gameId);

	/**
	 * 获取挑战赛列表信息
	 * 
	 * @param userId
	 * @param phaseNo
	 * @param gameId
	 * @return
	 */
	List<UserGameOrder> getUserGameList(Integer userId, String phaseNo, PageBean pageBean);

	List<UserGameOrder> getUserGameOrderList(Integer userId, String phaseNo, PageBean pageBean);
}
