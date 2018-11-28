package cn.simple.kwP.service;

import java.sql.SQLException;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.UserAccount;
import cn.simple.kwA.model.UserAccountStream;
import cn.simple.kwA.model.UserInfo;
import cn.simple.kwA.service.UserService;
import cn.simple.kwP.utils.ConsistentHash;
import cn.simple.kwP.utils.TbConst;

public class UserServiceImpl implements UserService {

	@Override
	public Boolean addUserInfo(UserInfo userInfo) {
		boolean tx = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				userInfo.save();
				UserAccount account = new UserAccount();
				account.setUserId(userInfo.getUserId());
				account.save();
				return true;
			}
		});
		return tx;
	}

	@Override
	public Boolean updateUserInfo(UserInfo userInfo) {
		return userInfo.update();
	}

	@Override
	public UserInfo getUserInfoByUserId(Integer userId) {
		return UserInfo.dao.findById(userId);
	}

	@Override
	public UserInfo getUserInfoByWxOpenId(String wxOpenId) {
		return UserInfo.dao.findFirst("select * from user_info where wx_open_id=?", wxOpenId);
	}

	@Override
	public UserInfo getUserInfoByMobile(String mobile) {
		return UserInfo.dao.findFirst("select * from user_info where mobile=?", mobile);
	}

	@Override
	public UserAccount getUserAccountByUserId(Integer userId) {
		return UserAccount.dao.findFirst("select * from user_account where user_id=?", userId);
	}

	@Override
	public List<UserAccountStream> getStreamByUserId(Integer userId, PageBean pageBean) {
		String tbName = ConsistentHash.getTbName(userId.toString(), TbConst.U_C_STREAM);
		String sql = "select * from user_account_stream where user_id=? order by id desc";
		sql = sql.replace(TbConst.U_C_STREAM, tbName);
		SqlPara sqlPara = new SqlPara();
		sqlPara.setSql(sql);
		sqlPara.addPara(userId);
		Page<UserAccountStream> pageData = UserAccountStream.dao.paginate(pageBean.getIndex(), pageBean.getCount(),
				sqlPara);
		if (pageData != null) {
			return pageData.getList();
		}
		return null;
	}

	@Override
	public Boolean updateUserAccount(UserAccount userAccount) {
		return userAccount.update();
	}

	@Override
	public Record enrollCheck(Integer userId) {
		Record result = new Record();
		int count = Db.queryInt(
				"select count(0) as count from user_info where user_id=? and enroll_count < max_enroll_count", userId);
		if (count > 0) {
			return result.set("result", true);
		} else {
			result.set("result", false);
			result.set("msg", "你每天的报名次数已满，可通过分享、邀请朋友参加活动增加每天的报名上限");
		}
		return result;
	}
}
