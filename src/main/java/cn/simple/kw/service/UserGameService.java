package cn.simple.kw.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model.UserInfo;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Service;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwA.utils.ToolSqlXml;

@Service
public class UserGameService {

	/**
	 * 根据gameId获取用户相关赛事
	 * 
	 * @param id
	 * @return
	 */
	public GameOrder getUserGameOrderById(Integer userId, Integer id) {
		return GameOrder.dao.findFirst(ToolSqlXml.getSql("static.selectUserGameOrderById"), id, userId);
	}

	/**
	 * 根据gameId获取用户相关赛事
	 * 
	 * @param userId
	 * @param gameId
	 * @return
	 */
	public GameOrder getUserGameOrder(Integer userId, Integer gameId) {
		return GameOrder.dao.findFirst(ToolSqlXml.getSql("static.selectUserGameOrderByGameId"), userId, gameId);

	}

	/**
	 * 获取用户挑战中列表信息
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 */
	public List<GameOrder> getOngoingGameOrderList(Integer userId, Integer step, PageBean pageBean) {
		reportStep(userId, step);
		// 当日期号
		String phaseNo = DateUtils.getTodayYYYYMMDD();
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(
				"select t1.icon_url,t1.game_id,t1.title,t1.reach_step,IF(t1.amount=0,t2.entry_amount,t1.amount) as amount,t1.status,"
						+ "case when t1.walk_step/t1.reach_step < 0.25 then 1 when t1.walk_step/t1.reach_step < 0.5 then 2 "
						+ "when t1.walk_step/t1.reach_step < 0.75 then 3 when t1.walk_step/t1.reach_step < 1 then 4 else 5 end as icon_type "
						+ "from game_order t1 left join game_info t2 on (t1.game_id=t2.game_id) where t1.user_id=? and t1.phase_no=? and t1.pay_status=1 order by t1.status asc, icon_type asc, t1.id desc");
		sqlPara.addPara(userId);
		sqlPara.addPara(phaseNo);
		Page<GameOrder> pageList = GameOrder.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
		if (pageList != null) {
			return pageList.getList();
		}
		return new ArrayList<>();
	}

	/**
	 * 获取用户已报名列表信息
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 */
	public List<GameOrder> getEnrolledGameInfoList(Integer userId, PageBean pageBean) {
		// 明天期号
		String phaseNo = DateUtils.getTomorrowYYYYMMDD();
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(
				"select t1.icon_url,t1.game_id,t1.title,t1.reach_step,t2.entry_amount,t2.entry_count from game_order t1 inner join game_info t2 "
						+ "on (t1.game_id=t2.game_id) where t1.user_id=? and t1.phase_no=? and pay_status=1 order by t1.id desc");
		sqlPara.addPara(userId);
		sqlPara.addPara(phaseNo);
		Page<GameOrder> pageList = GameOrder.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
		if (pageList != null) {
			return pageList.getList();
		}
		return new ArrayList<>();
	}

	/**
	 * 挑战记录列表获取
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 * @throws ParseException
	 */
	public List<GameOrder> getUserGameOrderList(Integer userId, PageBean pageBean) {

		// Object stepInfo = Redis.use().get("step" + userId);
		Map<Long, Integer> stepMap = new HashMap<>();
		// if (stepInfo != null) {
		// JSONArray fromObject = JSONArray.fromObject(stepInfo);
		// JSONObject step = new JSONObject();
		// for (Object object : fromObject) {
		// step = JSONObject.fromObject(object);
		// stepMap.put(step.getLong("timestamp"), step.getInt("step"));
		// }
		// }

		String phaseNo = DateUtils.getTodayYYYYMMDD();
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql("select t1.id,t1.game_id,t1.phase_no,t1.title,t1.group_no,t1.walk_step,t1.reach_step,"
				+ "IF(t1.amount=0,t2.entry_amount,t1.amount) as amount,t1.status,IFNULL(t1.report_time,'未提交') report_time "
				+ "from game_order t1 left join game_info t2 on (t1.game_id=t2.game_id) where t1.user_id=? and t1.pay_status=1 and t1.phase_no < ? order by t1.id desc");
		sqlPara.addPara(userId);
		sqlPara.addPara(phaseNo);
		Page<GameOrder> pageList = GameOrder.dao.paginate(pageBean.getIndex(), pageBean.getCount(), sqlPara);
		if (pageList != null) {
			List<GameOrder> list = pageList.getList();
			try {
				// 有步数记录
				if (stepMap.size() > 0) {
					Long todayBeginTime = DateUtils.getBeginTime(phaseNo);
					for (GameOrder gameOrder : list) {
						Long beginTime = DateUtils.getBeginTime(gameOrder.getPhaseNo());
						// 3天内
						if (todayBeginTime - beginTime <= 86400 * 3) {
							if ("0".equals(gameOrder.getStatus()) && stepMap.get(beginTime) != null
									&& stepMap.get(beginTime) >= gameOrder.getReachStep()) {
								gameOrder.setStatus("2");
							}
						} else {
							break;
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			return pageList.getList();
		}
		return new ArrayList<>();
	}

	/**
	 * 获取订单基本信息
	 * 
	 * @param orderId
	 * @return
	 */
	public GameOrder getGameOrderByOrderId(String orderId) {
		return GameOrder.dao.findFirst(
				"select id,user_id,game_id,entry_fee from game_order where order_id=? and pay_status=0", orderId);
	}

	/**
	 * 获取用户当天所有订单信息
	 * 
	 * @param phaseNo
	 * 
	 * @param orderId
	 * @return
	 */
	public List<GameOrder> getGameOrderByUserId(Integer userId, String phaseNo) {
		List<GameOrder> orderList = GameOrder.dao.find(
				"select id,game_id,group_no,entry_fee,reach_step,status from game_order where user_id=? and phase_no=? and pay_status=1",
				userId, phaseNo);
		if (orderList != null) {
			return orderList;
		}
		return new ArrayList<>();
	}

	/**
	 * 步数同步
	 * 
	 * @param userId
	 * @param step
	 */
	public void reportStep(Integer userId, Integer step) {
		String phaseNo = DateUtils.getTodayYYYYMMDD();
		// 获取进行中所有订单
		List<GameOrder> gameOrderList = getGameOrderByUserId(userId, phaseNo);
		String reportTime = DateUtils.getNowTime();
		// 系统用户提交时间
		UserInfo userInfo = UserInfo.dao
				.findFirst("select ifnull(from_user_id,6) as from_user_id from user_info where user_id=?", userId);
		if (userInfo != null && 0 == userInfo.getFromUserId()) {
			Random randData = new Random();
			reportTime = DateUtils.getTime(DateUtils.getNowDateTime() - randData.nextInt(5000));
		}

		if (gameOrderList != null) {
			for (GameOrder gameOrder : gameOrderList) {
				// 已达标或未达标，单做步数更新
				if ("1".equals(gameOrder.getStatus())
						|| ("0".equals(gameOrder.getStatus()) && step < gameOrder.getReachStep())) {
					Db.update("update game_order set walk_step=?,report_time=? where id=?", step, reportTime,
							gameOrder.getId());
					// 变达标、更新整个group的amount
				} else {
					Integer gameId = gameOrder.getGameId();
					String groupNo = gameOrder.getGroupNo();
					// 变达标、更新整个group的amount
					Integer groupCount = Db.queryInt(
							"select count(0) from game_order where phase_no=? and game_id=? and group_no=?", phaseNo,
							gameId, groupNo);
					Integer reachCount = Db.queryInt(
							"select count(0) from game_order where phase_no=? and game_id=? and group_no=? and status=1",
							phaseNo, gameId, groupNo);
					BigDecimal total = gameOrder.getEntryFee().multiply(new BigDecimal(groupCount));
					BigDecimal amount = total.divide(new BigDecimal(reachCount + 1), 2, BigDecimal.ROUND_HALF_DOWN);

					if ("0".equals(gameOrder.getStatus()) && step >= gameOrder.getReachStep()) {
						// 达标人数+1
						Db.update(
								"update game_info set reach_count=?,avg_amount=IFNULL(TRUNCATE(entry_amount/reach_count,2),entry_amount) where game_id=? and reach_count < entry_count",
								reachCount + 1, gameId);

						Db.update(
								"update game_order set amount=? where phase_no=? and game_id=? and group_no=? and pay_status='1'",
								amount, phaseNo, gameId, groupNo);
						
						Db.update(
								"update game_order set amount=?,walk_step=?, report_time=?, status='1' where user_id=? and phase_no=? and game_id=? and group_no=? and pay_status='1'",
								amount, step, reportTime, userId, phaseNo, gameId, groupNo);

					}
				}
			}
		}
	}
}
