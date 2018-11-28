package cn.simple.kwA.service;

import java.util.List;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GroupGameOrderCity;
import cn.simple.kwA.model.GroupGameOrderCountry;
import cn.simple.kwA.model.GroupGameOrderTeam;

public interface GroupGameOrderService {

	/**
	 * 获取全国赛个人小组列表信息
	 * 
	 * @param phaseNo
	 * @param groupNo
	 * @param pageBean
	 * @return
	 */
	List<GroupGameOrderCountry> getCountryList(String phaseNo, String groupNo, PageBean pageBean);

	/**
	 * 获取城市赛个人小组列表信息
	 * 
	 * @param phaseNo
	 * @param cityId
	 * @param groupNo
	 * @param pageBean
	 * @return
	 */
	List<GroupGameOrderCity> getCityList(String phaseNo, Integer cityId, String groupNo, PageBean pageBean);

	/**
	 * 获取团赛个人小组列表信息
	 * 
	 * @param phaseNo
	 * @param teamId
	 * @param pageBean
	 * @return
	 */
	List<GroupGameOrderTeam> getTeamList(String phaseNo, Integer teamId, PageBean pageBean);
}
