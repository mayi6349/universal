package cn.simple.kwA.service;

import java.util.List;

import cn.simple.kwA.model.UserAccount;
import cn.simple.kwA.model.UserAccountStream;
import cn.simple.kwA.model.UserInfo;

public interface SysService {

	/**
	 * 获取生成的Authorize链接
	 * 
	 * @return
	 */
	String getAuthorizeURL();

	/**
	 * 添加新用户信息
	 *
	 * @param
	 * @return
	 */
	Boolean addUserInfo(UserInfo userInfo);

	/**
	 * 更新用户信息
	 *
	 * @param
	 * @return
	 */
	Boolean updateUserInfo(UserInfo userInfo);

	/**
	 * 根据userId获取用户信息
	 *
	 * @param
	 * @return
	 */
	UserInfo getUserInfoByUserId(Integer userId);

	/**
	 * 根据wxOpenId获取用户信息
	 *
	 * @param
	 * @return
	 */
	UserInfo getUserInfoByWxOpenId(String wxOpenId);

	/**
	 * 通过手机号码获取用户信息
	 *
	 * @param mobile
	 * @return 用户信息
	 */
	UserInfo getUserInfoByMobile(String mobile);

	/**
	 * 获取用户账户信息
	 *
	 * @param
	 * @return
	 */
	UserAccount getUserAccountByUserId(Integer userId);

	/**
	 * 获取用户账户流水信息
	 *
	 * @param
	 * @return
	 */
	List<UserAccountStream> getStreamByUserId(Integer userId);

	/**
	 * 更新用户账户
	 *
	 * @param
	 * @return
	 */
	Boolean updateUserAccount(UserAccount userAccount);

	/**
	 * 获取随机头像
	 *
	 * @return
	 */
	String getRandomAvatar();

	/**
	 * 同步步数
	 * 
	 * @param userId
	 * @param step
	 */
	void reportStep(Integer userId, Integer step);
	
	/**
	 * 报名
	 * 
	 * @param userId
	 * @param type
	 * @param enrollId
	 */
	String enroll(Integer userId, String type, Integer enrollId);
}
