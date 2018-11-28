package cn.simple.kw.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kw.model.GameOrder;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Service;
import cn.simple.kwA.utils.StringUtils;

@Service
public class GroupGameService {

	/**
	 * 获取小组赛最新的5个头像
	 * 
	 * @param phaseNo
	 * @param gameId
	 * @param groupNo
	 * @return
	 */
	public List<String> getIconList(String phaseNo, Integer gameId, String groupNo) {

		List<String> iconList = Db.query(
				"select icon_url from game_order where phase_no=? and game_id=? and group_no=? and pay_status=1 order by id desc limit 5",
				phaseNo, gameId, groupNo);

		return iconList;
	}

	/**
	 * 获取小组赛赛况
	 * 
	 * @param gameId
	 * @param groupNo
	 * @return
	 */
	public GameOrder getGroupHeadInfo(Integer gameId, String groupNo) {

		return GameOrder.dao
				.findFirst("select count(0) entry_count,SUM(entry_fee) as entry_amount, IF(amount=0,sum(entry_fee),amount) as amount from game_order "
						+ "where game_id=? and group_no=? and pay_status=1", gameId, groupNo);
	}

	/**
	 * 获取小组赛达标人数赛况
	 * 
	 * @param gameId
	 * @param groupNo
	 * @return
	 */
	public Integer getGroupReachCount(Integer gameId, String groupNo) {
		return Db.queryInt(
				"select count(0) from game_order where game_id=? and group_no=? and pay_status=1 and status=1", gameId,
				groupNo);
	}

	/**
	 * 获取本人在小组赛的信息
	 * 
	 * @param gameId
	 * @param groupNo
	 * @return
	 */
	public GameOrder getUser(Integer userId, Integer gameId, String groupNo) {
		// round((unix_timestamp(now()) - unix_timestamp(a.report_time))/60)
		String sql = "select CAST((@i:=@i+1) as SIGNED) no, a.user_id,a.nick_name,a.icon_url,a.walk_step,IFNULL(DATE_FORMAT(report_time,'%H:%i:%S'),'') report_time,a.status "
				+ "from game_order a ,(select @i:=0) t where a.game_id=? and group_no=? and pay_status=1 order by a.walk_step desc";
		sql = "select t1.no,t1.nick_name,t1.icon_url,t1.walk_step,t1.report_time from (" + sql + ") t1 where user_id=?";
		GameOrder userInfo = GameOrder.dao.findFirst(sql, gameId, groupNo, userId);
		if (userInfo != null) {
			userInfo.setNickName(StringEscapeUtils.unescapeJava(userInfo.getNickName()));
		}
		return userInfo;
	}

	/**
	 * 获取小组赛列表信息
	 * 
	 * @param gameId
	 * @param groupNo
	 * @return
	 */
	public List<GameOrder> getUserList(Integer userId, Integer gameId, String groupNo, PageBean pageBean) {
		SqlPara sqlPara = new SqlPara();
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select CAST((@i:=@i+1) as SIGNED) no, a.user_id,a.nick_name,a.icon_url,a.walk_step,IFNULL(DATE_FORMAT(report_time,'%H:%i:%S'),'') report_time,status "
						+ "from game_order a ,(select @i:=0) t where a.game_id=? and group_no=? and pay_status=1 order by a.walk_step desc");
		sqlPara.setSql(sql.toString());
		sqlPara.addPara(gameId);
		sqlPara.addPara(groupNo);
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
