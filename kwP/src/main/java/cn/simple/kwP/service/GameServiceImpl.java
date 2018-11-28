package cn.simple.kwP.service;

import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.GameInfo;
import cn.simple.kwA.model.GameInfoCity;
import cn.simple.kwA.model.GameInfoCountry;
import cn.simple.kwA.model.GameInfoTeam;
import cn.simple.kwA.model.UserGameOrder;
import cn.simple.kwA.service.GameService;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwA.utils.ToolSqlXml;
import cn.simple.kwP.utils.ConsistentHash;
import cn.simple.kwP.utils.TbConst;

public class GameServiceImpl implements GameService {

	@Override
	public GameInfoCountry getGameInfoCountry(String phaseNo) {
		GameInfoCountry data = GameInfoCountry.dao.findFirst("select * from game_info_country where phase_no=?",
				phaseNo);
		return data;
	}

	@Override
	public List<GameInfoCity> getGameInfoCityList(String phaseNo, Integer userId) {
		String sql = ToolSqlXml.getSql("static.selectCityGameByUserId");
		List<GameInfoCity> dataList = GameInfoCity.dao.find(sql, userId, phaseNo);
		return dataList;
	}

	@Override
	public GameInfoCity getGameInfoCity(Integer id) {
		return GameInfoCity.dao.findById(id);
	}

	@Override
	public List<GameInfoTeam> getGameInfoTeamList(String phaseNo, Integer userId, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(phaseNo, TbConst.G_I_TEAM);
		String sql = ToolSqlXml.getSql("static.selectTeamGameByUserId");
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql.replace(TbConst.G_I_TEAM, tbName));
		sqlPara.addPara(userId);
		sqlPara.addPara(phaseNo);
		Page<GameInfoTeam> paginate = GameInfoTeam.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);

		if (paginate != null) {
			return paginate.getList();
		}
		return null;
	}

	@Override
	public GameInfoTeam getGameInfoTeam(Integer id, String phaseNo) {
		String tbName = ConsistentHash.getTbName(phaseNo, TbConst.G_I_TEAM);
		String sql = "select * from game_info_team where id=?";
		return GameInfoTeam.dao.findFirst(sql.replace(TbConst.G_I_TEAM, tbName), id);
	}

	@Override
	public List<GameInfo> getGameInfoList(Integer userId, String type, PageBean pageBean) {

		String tbUgo = ConsistentHash.getTbName(userId.toString(), TbConst.USER_GAME_ORDER);

		// 进行中
		if ("0".equals(type)) {
			String phaseNo = DateUtils.getTodayYYYYMMDD();
			String tbGi = ConsistentHash.getTbName(phaseNo, TbConst.GAME_INFO);
			String sql = "select t2.id,t2.icon_url,t2.title,t2.entry_fee,t2.reach_step,t2.entry_count,t2.entry_amount "
					+ "from user_game_order t1 inner join game_info t2 on (t1.game_id=t2.id) "
					+ "where t1.user_id=? and t1.phase_no=? and t1.pay_status=1";
			SqlPara sqlPara = new SqlPara();
			sqlPara.setSql(sql.replace(TbConst.USER_GAME_ORDER, tbUgo).replace(TbConst.GAME_INFO, tbGi));
			sqlPara.addPara(userId);
			sqlPara.addPara(phaseNo);
			Page<GameInfo> paginate = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
			if (paginate != null) {
				return paginate.getList();
			}
			// 报名中
		} else if ("1".equals(type)) {
			String phaseNo = DateUtils.getTomorrowYYYYMMDD();

			// 已报名的gameId获取
			// String enrolledSql = "select game_id from user_game_order where
			// user_id=? and phase_no=? and pay_status=1";
			// List<Integer> enrolledList =
			// Db.query(enrolledSql.replace(TbConst.USER_GAME_ORDER, tbUgo),
			// userId, phaseNo);

			String tbGi = ConsistentHash.getTbName(phaseNo, TbConst.GAME_INFO);
			String sql = "select id,icon_url,title,entry_fee,reach_step,entry_count,entry_amount from game_info where phase_no=? ";
			SqlPara sqlPara = new SqlPara();

			// if (enrolledList != null && enrolledList.size() > 0) {
			// sql = sql + "and id not in (?)";
			// sqlPara.setSql(sql.replace(TbConst.GAME_INFO, tbGi));
			// sqlPara.addPara(phaseNo);
			// sqlPara.addPara(enrolledList.toString().replace("[",
			// "").replace("]", ""));
			// } else {

			sqlPara.setSql(sql.replace(TbConst.GAME_INFO, tbGi));
			sqlPara.addPara(phaseNo);
			// }
			Page<GameInfo> paginate = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
			if (paginate != null) {
				return paginate.getList();
			}

			// 已报名
		} else if ("2".equals(type)) {
			String phaseNo = DateUtils.getTomorrowYYYYMMDD();
			String tbGi = ConsistentHash.getTbName(phaseNo, TbConst.GAME_INFO);
			String sql = "select t2.id,t2.icon_url,t2.title,t2.entry_fee,t2.reach_step,t2.entry_count,t2.entry_amount "
					+ "from user_game_order t1 inner join game_info t2 on (t1.game_id=t2.id) "
					+ "where t1.user_id=? and t1.phase_no=? and t1.pay_status=1";
			SqlPara sqlPara = new SqlPara();
			sqlPara.setSql(sql.replace(TbConst.USER_GAME_ORDER, tbUgo).replace(TbConst.GAME_INFO, tbGi));
			sqlPara.addPara(userId);
			sqlPara.addPara(phaseNo);
			Page<GameInfo> paginate = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
			if (paginate != null) {
				return paginate.getList();
			}
		}
		return null;
	}

	@Override
	public GameInfo getGameInfo(String phaseNo, Integer gameId) {
		String tbGi = ConsistentHash.getTbName(phaseNo, TbConst.GAME_INFO);
		String sql = "select nick_name,icon_url,title,phase_no,entry_fee,reach_step,entry_count,entry_amount,reach_count,agv_amount "
				+ "from game_info where id=? and phase_no=?";
		return GameInfo.dao.findFirst(sql.replace(TbConst.GAME_INFO, tbGi), gameId, phaseNo);
	}

	@Override
	public List<GameInfo> getGameList(String phaseNo, PageBean pageBean) {
		String tbGi = ConsistentHash.getTbName(phaseNo, TbConst.GAME_INFO);
		SqlPara sqlPara = new SqlPara();
		if (DateUtils.getTodayYYYYMMDD().equals(phaseNo)) {
			sqlPara.setSql(ToolSqlXml.getSql("static.selectGameOngoing").replace(TbConst.GAME_INFO, tbGi));
			sqlPara.addPara(phaseNo);
			Page<GameInfo> pageList = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
			if (pageList != null) {
				return pageList.getList();
			}
		} else if (DateUtils.getTomorrowYYYYMMDD().equals(phaseNo)) {
			sqlPara.setSql(ToolSqlXml.getSql("static.selectEnrolling").replace(TbConst.GAME_INFO, tbGi));
			sqlPara.addPara(phaseNo);
			Page<GameInfo> pageList = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
			if (pageList != null) {
				return pageList.getList();
			}

		} else if (DateUtils.getYesterdayYYYYMMDD().equals(phaseNo)) {
			sqlPara.setSql(ToolSqlXml.getSql("static.selectGameHistory").replace(TbConst.GAME_INFO, tbGi));
			sqlPara.addPara(phaseNo);
			Page<GameInfo> pageList = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
			if (pageList != null) {
				return pageList.getList();
			}

		}
		return null;
	}

	@Override
	public List<UserGameOrder> getUserGameList(Integer userId, String phaseNo, PageBean pageBean) {
		// TODO Auto-generated method stub
		return null;
	}
}
