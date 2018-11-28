package cn.simple.kwP.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GroupGameOrderCity;
import cn.simple.kwA.model.GroupGameOrderCountry;
import cn.simple.kwA.model.GroupGameOrderTeam;
import cn.simple.kwA.service.GroupGameOrderService;
import cn.simple.kwP.utils.ConsistentHash;
import cn.simple.kwP.utils.TbConst;

public class GroupGameOrderServiceImpl implements GroupGameOrderService {

	@Override
	public List<GroupGameOrderCountry> getCountryList(String phaseNo, String groupNo, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(phaseNo + groupNo, TbConst.G_G_O_COUNTRY);
		String sql = "select * from group_game_order_country where phase_no=? and group_no=? order by walk_step desc";
		sql = sql.replace(TbConst.G_G_O_COUNTRY, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(phaseNo);
		sqlPara.addPara(groupNo);
		Page<GroupGameOrderCountry> pageData = GroupGameOrderCountry.dao.paginate(pageBean.getIndex(),
				pageBean.getCount(), sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

	@Override
	public List<GroupGameOrderCity> getCityList(String phaseNo, Integer cityId, String groupNo, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(phaseNo + cityId + groupNo, TbConst.G_G_O_CITY);
		String sql = "select * from group_game_order_city where phase_no=? and city_id=? and group_no=? order by walk_step desc";
		sql = sql.replace(TbConst.G_G_O_CITY, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(phaseNo);
		sqlPara.addPara(cityId);
		sqlPara.addPara(groupNo);
		Page<GroupGameOrderCity> pageData = GroupGameOrderCity.dao.paginate(pageBean.getIndex(), pageBean.getCount(),
				sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

	@Override
	public List<GroupGameOrderTeam> getTeamList(String phaseNo, Integer teamId, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(phaseNo + teamId, TbConst.G_G_O_TEAM);
		String sql = "select * from group_game_order_team where phase_no=? and team_id=? order by walk_step desc";
		sql = sql.replace(TbConst.G_G_O_TEAM, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(phaseNo);
		sqlPara.addPara(teamId);
		Page<GroupGameOrderTeam> pageData = GroupGameOrderTeam.dao.paginate(pageBean.getIndex(), pageBean.getCount(),
				sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

}
