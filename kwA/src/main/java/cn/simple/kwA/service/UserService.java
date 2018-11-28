package cn.simple.kwA.service;

import java.util.List;

import com.jfinal.plugin.activerecord.Record;

import cn.simple.kwA.PageBean;
import cn.simple.kwA.model.UserAccount;
import cn.simple.kwA.model.UserAccountStream;
import cn.simple.kwA.model.UserInfo;

public interface UserService {

	/**
	 * 添加新用户信息
	 * 
	 * @param userInfo
	 * @return
	 */
	Boolean addUserInfo(UserInfo userInfo);

	/**
	 * 更新用户信息
	 * 
	 * @param userInfo
	 * @return
	 */
	Boolean updateUserInfo(UserInfo userInfo);

	/**
	 * 根据userId获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	UserInfo getUserInfoByUserId(Integer userId);

	/**
	 * 根据wxOpenId获取用户信息
	 * 
	 * @param wxOpenId
	 * @return
	 */
	UserInfo getUserInfoByWxOpenId(String wxOpenId);

	/**
	 * 根据mobile获取用户信息
	 * 
	 * @param wxOpenId
	 * @return
	 */
	UserInfo getUserInfoByMobile(String mobile);

	/**
	 * 通过手机号码获取用户信息
	 * 
	 * @param userId
	 * @return
	 */
	UserAccount getUserAccountByUserId(Integer userId);

	/**
	 * 获取用户账户流水信息
	 *
	 * @param
	 * @return
	 */
	List<UserAccountStream> getStreamByUserId(Integer userId, PageBean pageBean);

	/**
	 * 更新用户账户
	 *
	 * @param
	 * @return
	 */
	Boolean updateUserAccount(UserAccount userAccount);

	/**
	 * 报名验证
	 *
	 * @param
	 * @return
	 */
	Record enrollCheck(Integer userId);
}
