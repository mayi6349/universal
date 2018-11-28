package cn.simple.kw.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.GameOrder;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Service;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwA.utils.StringUtils;
import cn.simple.kwA.utils.ToolSqlXml;

@Service
public class GameService {

	UserService userService = new UserService();

	/**
	 * 获取赛事列表
	 * 
	 * @param phaseNo
	 * @param pageBean
	 * @return
	 */
	public List<GameInfo> getGameList(String phaseNo, String keyword, PageBean pageBean) {

		SqlPara sqlPara = new SqlPara();
		sqlPara.addPara(phaseNo);

		StringBuilder sql = new StringBuilder();
		sql.append("select game_id,icon_url,nick_name,title,entry_fee,reach_step,entry_amount,phase_No, "
				+ "entry_count,IFNULL(TRUNCATE(entry_amount/reach_count,2),entry_amount) as avg_amount "
				+ "from game_info where phase_no=? and del_flg=0 ");
		if (StringUtils.isNotEmpty(keyword)) {
			sql.append("and CONCAT('_',title,'_',reach_step,'_',entry_amount,'_') LIKE ? ");
			sqlPara.addPara("%" + keyword + "%");
		}
		sql.append(" order by type desc, entry_amount DESC,entry_count DESC,avg_amount DESC");
		sqlPara.setSql(sql.toString());
		Page<GameInfo> pageList = GameInfo.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
		if (pageList != null) {
			return pageList.getList();
		}
		return null;
	}

	/**
	 * 根据gameId获取赛事详情
	 * 
	 * @param gameId
	 * @return
	 */
	public GameInfo getGameInfo(Integer gameId) {
		return GameInfo.dao.findFirst(ToolSqlXml.getSql("static.selectGameInfoById"), gameId);
	}

	/**
	 * 根据gameId获取赛事总详情
	 * 
	 * @param gameId
	 * @return
	 */
	public GameInfo getGameHeadInfo(Integer gameId) {
		return GameInfo.dao.findFirst("select title,phase_no,entry_count,entry_amount,"
				+ "IFNULL(TRUNCATE(entry_amount/reach_count,2),entry_amount) as avg_amount "
				+ "from game_info where game_id=? and is_open=0", gameId);
	}

	/**
	 * 获取gameInfo基本信息
	 * 
	 * @param gameId
	 * @return
	 */
	public GameInfo getGameBaseInfo(Integer gameId) {
		return GameInfo.dao.findFirst(
				"select game_id,user_id,nick_name,icon_url,title,phase_no,entry_fee,reach_step,entry_count,is_open"
						+ " from game_info where game_id=?",
				gameId);
	}

	/**
	 * 创建挑战
	 * 
	 * @param gameInfo
	 * @return
	 */
	public Integer createGame(GameInfo gameInfo) {
		gameInfo.save();
		return gameInfo.getGameId();
	}

	/**
	 * 创建赛标题重复check
	 * 
	 * @param title
	 * @return
	 */
	public boolean checkTitle(String title) {
		String phaseNo = DateUtils.getTomorrowYYYYMMDD();
		Integer queryInt = Db.queryInt("select game_id from game_info where phase_no=? and title=? and del_flg=0",
				phaseNo, title);
		if (queryInt != null) {
			return true;
		}
		return false;
	}

	/**
	 * 获取最近报名信息
	 * 
	 * @param userId
	 * @param gameId
	 * @param pageBean
	 * @return
	 */
	public List<GameOrder> getEnrollUserList(Integer userId, Integer gameId, PageBean pageBean) {
		SqlPara sqlPara = new SqlPara();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select user_id,nick_name,icon_url,walk_step,round((unix_timestamp(now()) - unix_timestamp(pay_time))/60) enroll_time "
						+ "from game_order where game_id=? and pay_status=1 order by pay_time desc");
		sqlPara.setSql(sql.toString());
		sqlPara.addPara(gameId);
		Page<GameOrder> pageList = GameOrder.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
		if (pageList != null) {
			List<GameOrder> list = pageList.getList();
			String nickName = "";
			for (GameOrder gameOrder : list) {
				nickName = StringEscapeUtils.unescapeJava(gameOrder.getNickName());
//				nickName = gameOrder.getNickName();
				if (gameOrder.getUserId() != userId && !"神秘人".equals(nickName)) {
					gameOrder.setNickName(StringUtils.formatStr(nickName));
				} else {
					gameOrder.setNickName(nickName);
				}
			}
			return list;
		}
		return new ArrayList<>();
	}
}
