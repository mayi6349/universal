package cn.simple.kwP.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GroupGameOrderCity;
import cn.simple.kwA.model.GroupGameOrderCountry;
import cn.simple.kwA.model.GroupGameOrderTeam;
import cn.simple.kwA.model.UserGameOrder;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;
import cn.simple.kwA.service.UserGameOrderService;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwP.utils.ConsistentHash;
import cn.simple.kwP.utils.TbConst;

public class UserGameOrderServiceImpl implements UserGameOrderService {

	@Override
	public List<UserGameOrderCountry> getGameOrderCountryList(Integer userId, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_COUNTRY);
		String sql = "select id,phase_no,group_no,amount,status from user_game_order_country where user_id=? order by phase_no desc,id desc";
		sql = sql.replace(TbConst.U_G_O_COUNTRY, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(userId);
		Page<UserGameOrderCountry> pageData = UserGameOrderCountry.dao.paginate(pageBean.getIndex(),
				pageBean.getCount(), sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

	@Override
	public UserGameOrderCountry getGameOrderCountry(Integer userId, String phaseNo) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_COUNTRY);
		String sql = "select * from user_game_order_country where user_id=? and phase_no=?";
		sql = sql.replace(TbConst.U_G_O_COUNTRY, tbName);
		UserGameOrderCountry info = UserGameOrderCountry.dao.findFirst(sql, userId, phaseNo);

		String todayYYYYMMDD = DateUtils.getTodayYYYYMMDD();
		// group最新状态取得
		if (info != null && todayYYYYMMDD.equals(phaseNo)) {
			tbName = ConsistentHash.getTbName(todayYYYYMMDD + info.getGroupNo(), TbConst.G_G_O_COUNTRY);
			sql = "select * from group_game_order_country where user_id=? and phase_no=?";
			GroupGameOrderCountry ginfo = GroupGameOrderCountry.dao
					.findFirst(sql.replace(TbConst.G_G_O_COUNTRY, tbName), userId, todayYYYYMMDD);
			if (ginfo != null) {
				info.setAmount(ginfo.getAmount());
				info.setStatus(ginfo.getStatus());
				info.setWalkStep(ginfo.getWalkStep());
			}
		}
		return info;
	}

	@Override
	public UserGameOrderCountry getGameOrderCountry(Integer userId, Integer id) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_COUNTRY);
		String sql = "select * from user_game_order_country where user_id=? and id=?";
		sql = sql.replace(TbConst.U_G_O_COUNTRY, tbName);
		UserGameOrderCountry info = UserGameOrderCountry.dao.findFirst(sql, userId, id);

		String todayYYYYMMDD = DateUtils.getTodayYYYYMMDD();
		if (info != null && todayYYYYMMDD.equals(info.getPhaseNo())) {
			// group最新状态取得
			tbName = ConsistentHash.getTbName(todayYYYYMMDD + info.getGroupNo(), TbConst.G_G_O_COUNTRY);
			sql = "select * from group_game_order_country where user_id=? and phase_no=?";
			GroupGameOrderCountry ginfo = GroupGameOrderCountry.dao
					.findFirst(sql.replace(TbConst.G_G_O_COUNTRY, tbName), userId, todayYYYYMMDD);
			if (ginfo != null) {
				info.setAmount(ginfo.getAmount());
				info.setStatus(ginfo.getStatus());
				info.setWalkStep(ginfo.getWalkStep());
			}
		}
		return info;
	}

	@Override
	public List<UserGameOrderCity> getGameOrderCityList(Integer userId, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_CITY);
		String sql = "select id,phase_no,city_id,group_no,amount,status from user_game_order_city where user_id=? order by phase_no desc,id desc";
		sql = sql.replace(TbConst.U_G_O_CITY, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(userId);
		Page<UserGameOrderCity> pageData = UserGameOrderCity.dao.paginate(pageBean.getIndex(), pageBean.getCount(),
				sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

	@Override
	public UserGameOrderCity getGameOrderCity(Integer userId, String phaseNo, Integer cityId) {

		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_CITY);
		String sql = "select * from user_game_order_city where user_id=? and phase_no=? and city_id=?";
		sql = sql.replace(TbConst.U_G_O_CITY, tbName);
		UserGameOrderCity info = UserGameOrderCity.dao.findFirst(sql, userId, phaseNo, cityId);

		String todayYYYYMMDD = DateUtils.getTodayYYYYMMDD();
		if (info != null && todayYYYYMMDD.equals(phaseNo)) {
			// group最新状态取得
			tbName = ConsistentHash.getTbName(todayYYYYMMDD + cityId + info.getGroupNo(), TbConst.G_G_O_CITY);
			sql = "select * from group_game_order_city where user_id=? and phase_no=? and city_id=?";
			GroupGameOrderCity ginfo = GroupGameOrderCity.dao.findFirst(sql.replace(TbConst.G_G_O_CITY, tbName), userId,
					todayYYYYMMDD, cityId);
			if (ginfo != null) {
				info.setAmount(ginfo.getAmount());
				info.setStatus(ginfo.getStatus());
				info.setWalkStep(ginfo.getWalkStep());
			}
		}
		return info;
	}

	@Override
	public UserGameOrderCity getGameOrderCity(Integer userId, Integer id) {

		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_CITY);
		String sql = "select * from user_game_order_city where user_id=? and id=?";
		sql = sql.replace(TbConst.U_G_O_CITY, tbName);
		UserGameOrderCity info = UserGameOrderCity.dao.findFirst(sql, userId, id);

		String todayYYYYMMDD = DateUtils.getTodayYYYYMMDD();
		if (info != null && todayYYYYMMDD.equals(info.getPhaseNo())) {
			Integer cityId = info.getCityId();
			// group最新状态取得
			tbName = ConsistentHash.getTbName(todayYYYYMMDD + cityId + info.getGroupNo(), TbConst.G_G_O_CITY);
			sql = "select * from group_game_order_city where user_id=? and phase_no=? and city_id=?";
			GroupGameOrderCity ginfo = GroupGameOrderCity.dao.findFirst(sql.replace(TbConst.G_G_O_CITY, tbName), userId,
					todayYYYYMMDD, cityId);
			if (ginfo != null) {
				info.setAmount(ginfo.getAmount());
				info.setStatus(ginfo.getStatus());
				info.setWalkStep(ginfo.getWalkStep());
			}
		}
		return info;
	}

	@Override
	public List<UserGameOrderTeam> getGameOrderTeamList(Integer userId, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_TEAM);
		String sql = "select * from user_game_order_team where user_id=? order by phase_no desc,id desc";
		sql = sql.replace(TbConst.U_G_O_TEAM, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(userId);
		Page<UserGameOrderTeam> pageData = UserGameOrderTeam.dao.paginate(pageBean.getIndex(), pageBean.getCount(),
				sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

	@Override
	public UserGameOrderTeam getGameOrderTeam(Integer userId, String phaseNo, Integer teamId) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_TEAM);
		String sql = "select * from user_game_order_team where user_id=? and phase_no=? and team_id=?";
		sql = sql.replace(TbConst.U_G_O_TEAM, tbName);
		UserGameOrderTeam info = UserGameOrderTeam.dao.findFirst(sql, userId, phaseNo, teamId);

		String todayYYYYMMDD = DateUtils.getTodayYYYYMMDD();
		if (info != null && todayYYYYMMDD.equals(info.getPhaseNo())) {
			// group最新状态取得
			tbName = ConsistentHash.getTbName(todayYYYYMMDD + teamId, TbConst.G_G_O_TEAM);
			sql = "select * from group_game_order_city where user_id=? and phase_no=? and team_id=?";
			GroupGameOrderTeam ginfo = GroupGameOrderTeam.dao.findFirst(sql.replace(TbConst.G_G_O_TEAM, tbName), userId,
					todayYYYYMMDD, teamId);
			if (ginfo != null) {
				info.setAmount(ginfo.getAmount());
				info.setStatus(ginfo.getStatus());
				info.setWalkStep(ginfo.getWalkStep());
			}
		}
		return info;
	}

	@Override
	public UserGameOrderTeam getGameOrderTeam(Integer userId, Integer id) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_G_O_TEAM);
		String sql = "select * from user_game_order_team where user_id=? and id=?";
		sql = sql.replace(TbConst.U_G_O_TEAM, tbName);
		UserGameOrderTeam info = UserGameOrderTeam.dao.findFirst(sql, userId, id);

		String todayYYYYMMDD = DateUtils.getTodayYYYYMMDD();
		if (info != null && todayYYYYMMDD.equals(info.getPhaseNo())) {
			Integer teamId = info.getTeamId();
			// group最新状态取得
			tbName = ConsistentHash.getTbName(todayYYYYMMDD + teamId, TbConst.G_G_O_TEAM);
			sql = "select * from group_game_order_city where user_id=? and phase_no=? and team_id=?";
			GroupGameOrderTeam ginfo = GroupGameOrderTeam.dao.findFirst(sql.replace(TbConst.G_G_O_TEAM, tbName), userId,
					todayYYYYMMDD, teamId);
			if (ginfo != null) {
				info.setAmount(ginfo.getAmount());
				info.setStatus(ginfo.getStatus());
				info.setWalkStep(ginfo.getWalkStep());
			}
		}
		return info;
	}

	@Override
	public UserGameOrder getGameOrder(Integer userId, String phaseNo, Integer gameId) {
		String todayPhaseNo = DateUtils.getTodayYYYYMMDD();
		String tbUgo = ConsistentHash.getTbName(userId.toString(), TbConst.USER_GAME_ORDER);
		String sql = "select * from user_game_order where user_id=? and phase_no=? and game_id=? and pay_status=1";
		UserGameOrder userGameOrder = UserGameOrder.dao.findFirst(sql.replace(TbConst.USER_GAME_ORDER, tbUgo), userId, phaseNo, gameId);
		if (userGameOrder != null) {
			if (todayPhaseNo.equals(phaseNo)) {
				String groupNo = userGameOrder.getGroupNo();
				// TODO 
				
			} else if (todayPhaseNo.compareTo(phaseNo) < 0) {
				return userGameOrder;
			}
		}
		return null;
	}

	@Override
	public List<UserGameOrder> getUserGameOrderList(Integer userId, String phaseNo, PageBean pageBean) {
		String tbUgo = ConsistentHash.getTbName(userId.toString(), TbConst.USER_GAME_ORDER);
		String sql = "select * from user_game_order where user_id=? and phase_no=? and pay_status=1 order by id desc";
		
		return null;
	}

	@Override
	public List<UserGameOrder> getUserGameList(Integer userId, String phaseNo, PageBean pageBean) {
		// TODO Auto-generated method stub
		return null;
	}
}
