package cn.simple.kw.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.redis.Redis;

import cn.simple.kw.model.UserAccount;
import cn.simple.kw.model.UserAccountStream;
import cn.simple.kw.model.UserInfo;
import cn.simple.kwA.PageBean;
import cn.simple.kwA.ioc.Service;
import cn.simple.kwA.utils.DateUtils;

@Service
public class UserService {

	/**
	 * 添加用户信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public UserInfo addUserInfo(UserInfo userInfo) {
		userInfo.save();
		UserAccount userAccount = new UserAccount();
		userAccount.setUserId(userInfo.getUserId());
		userAccount.save();
		return userInfo;
	}

	/**
	 * 更新用户信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public void updateUserInfo(UserInfo userInfo) {
		userInfo.update();
	}

	/**
	 * 更新用户基本信息
	 * 
	 * @param userInfo
	 * @return
	 */
	public void updateBaseInfo(UserInfo userInfo) {
		userInfo.update();
		Db.update("update game_info set nick_name=?, icon_url=? where user_id=?", userInfo.getNickName(),
				userInfo.getIconUrl(), userInfo.getUserId());
		Db.update("update game_order set nick_name=?, icon_url=? where user_id=?", userInfo.getNickName(),
				userInfo.getIconUrl(), userInfo.getUserId());
	}

	/**
	 * 获取推荐人userId
	 * 
	 * @param userId
	 * @return
	 */
	public Integer getFromUserId(Integer userId) {
		return Db.queryInt("select from_user_id from user_info where user_id=?", userId);
	}

	/**
	 * 根据wxopenId获取userId
	 * 
	 * @param wxOpenId
	 * @return
	 */
	public Integer getUserIdByWxOpenId(String wxOpenId) {
		return Db.queryInt("select user_id from user_info where wx_open_id=?", wxOpenId);
	}

	/**
	 * 根据userId获取wxopenId
	 * 
	 * @param wxOpenId
	 * @return
	 */
	public String getWxOpenIdByUserId(Integer userId) {
		return Db.queryStr("select wx_open_id from user_info where user_id=?", userId);
	}

	/**
	 * 获取我的内容
	 * 
	 * @param userId
	 * @param phaseNo
	 * @param gameId
	 * @return
	 */
	public UserInfo getUserBaseInfo(Integer userId) {

		UserInfo userInfo = UserInfo.dao.findFirst(
				"select nick_name,icon_url,(select count(0) from user_info where from_user_id=?) help_user_count "
						+ "from user_info where user_id=?",
				userId, userId);
		userInfo.setNickName(StringEscapeUtils.unescapeJava(userInfo.getNickName()));
		return userInfo;
	}

	/**
	 * 获取用户剩余报名次数
	 * 
	 * @param userId
	 * @return
	 */
	public Integer getUsableEnrollCount(Integer userId) {
		// 报名次数信息
		UserInfo userEnrollCount = getUserEnrollCountInfo(userId);
		Integer sumEnrollCount = userEnrollCount.getBaseEnrollCount() + userEnrollCount.getHelpEnrollCount();
		// 今日已报名次数
		Integer enrolledCount = getEnrolledGameOrderCount(userId);
		return sumEnrollCount - enrolledCount;
	}

	/**
	 * 获取用户报名次数信息
	 * 
	 * @param userId
	 * @return
	 */
	public UserInfo getUserEnrollCountInfo(Integer userId) {
		return UserInfo.dao.findFirst("select base_enroll_count,help_enroll_count from user_info where user_id=?",
				userId);
	}

	/**
	 * 增加基础报名次数
	 * 
	 * @param userId
	 * @return
	 */
	public Boolean addBaseEnrollCount(Integer userId) {
		int count = Db.update("update user_info set base_enroll_count = base_enroll_count + 1 where user_id=?", userId);
		if (count > 0) {
//			Redis.use().flushAll();
			return true;
		}
		return false;
	}

	/**
	 * 获取用户挑战中条数
	 * 
	 * @param userId
	 * @return
	 */
	public Integer getOngoingGameOrderCount(Integer userId) {
		String phaseNo = DateUtils.getTodayYYYYMMDD();
		return Db.queryInt("select count(0) from game_order where user_id=? and phase_no=? and pay_status=1", userId,
				phaseNo);
	}

	/**
	 * 获取用户已报名次数
	 * 
	 * @param userId
	 * @return
	 */
	public Integer getEnrolledGameOrderCount(Integer userId) {
		String phaseNo = DateUtils.getTomorrowYYYYMMDD();
		return Db.queryInt("select count(0) from game_order where user_id=? and phase_no=? and pay_status=1", userId,
				phaseNo);
	}

	/**
	 * 获取用户参赛总条数
	 * 
	 * @param userId
	 * @return
	 */
	public Integer getGameOrderCount(Integer userId) {
		return Db.queryInt("select count(0) from game_order where user_id=? and pay_status=1", userId);
	}

	/**
	 * 获取用户达标总条数
	 * 
	 * @param userId
	 * @return
	 */
	public Integer getReachGameOrderCount(Integer userId) {
		return Db.queryInt("select count(0) from game_order where user_id=? and pay_status=1 and status = 1", userId);
	}

	/**
	 * 获取用户奖励金余额
	 * 
	 * @param userId
	 * @return
	 */
	public BigDecimal getUsableBalance(Integer userId) {
		return Db.queryBigDecimal("select usable_balance from user_account where user_id=?", userId);
	}

	/**
	 * 获取用户奖励金明细
	 * 
	 * @param userId
	 * @param pageBean
	 * @return
	 */
	public List<UserAccountStream> getStreamList(Integer userId, PageBean pageBean) {
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(
				"select case io_type when '0' then '参赛报名费' when '1' then '达标奖励金' when '2' then '奖励金提现' end as io_type,title,"
						+ "case io_type when '1' then CONCAT('+ ',amount) else CONCAT('- ',amount) end as amount,"
						+ "DATE_FORMAT(create_time, '%Y-%m-%d %k:%i:%s') as create_time "
						+ "from user_account_stream where user_id=? and pay_type=0 order by id desc");
		sqlPara.addPara(userId);
		Page<UserAccountStream> pageList = UserAccountStream.dao.paginate(pageBean.getIndex(), pageBean.getCount(),
				sqlPara);
		if (pageList != null) {
			return pageList.getList();
		}
		return new ArrayList<>();
	}

	/**
	 * 获取sessionkey
	 * 
	 * @param userId
	 */
	public String getSessionKey(Integer userId) {
		return Db.queryStr("select session_key from user_info where user_id=?", userId);
	}

	/**
	 * 判断当日是否提现
	 * 
	 * @param userId
	 * @param phaseNo
	 * @return
	 */
	public boolean exitTransfer(Integer userId, String phaseNo) {
		Integer queryInt = Db.queryInt(
				"select id from user_account_stream where user_id=? and io_type='2' and phase_no=?", userId, phaseNo);
		if (queryInt != null) {
			return true;
		}
		return false;
	}

	/**
	 * 追加流水
	 * 
	 * @param stream
	 * @return
	 */
	public boolean addStream(UserAccountStream stream) {
		Db.update("update user_account set usable_balance=usable_balance-? where user_id=?", stream.getAmount(),
				stream.getUserId());
		return stream.save();
	}
}
