package cn.simple.kw.service.thread;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cn.simple.kw.model.GameInfo;
import cn.simple.kw.model.UserInfo;
import cn.simple.kw.service.UserService;
import cn.simple.kwA.utils.DateUtils;

/**
 * 自动创建红包运动赛
 */
public class GameCreateThread implements Runnable {

	Logger logger = Logger.getLogger(GameCreateThread.class);

	/** 线程启动 */
	public void run() {
		try {

			UserService userService = new UserService();
			UserInfo userBaseInfo = userService.getUserBaseInfo(0);
			GameInfo gameInfo = new GameInfo();
			gameInfo.setUserId(0);
			gameInfo.setType("1");
			gameInfo.setNickName(userBaseInfo.getNickName());
			gameInfo.setIconUrl(userBaseInfo.getIconUrl());
			Record findFirst = Db.findFirst("select id,title from title_info order by use_count asc,id asc");
			gameInfo.setTitle(DateUtils.getTomorrowYYYYMMDD().substring(4, 8) + "期-" + findFirst.getStr("title"));
			gameInfo.setPhaseNo(DateUtils.getTomorrowYYYYMMDD());
			gameInfo.setStartTime("00:00:00");
			gameInfo.setEndTime("23:59:59");
			gameInfo.setEntryFee(new BigDecimal(2));
			gameInfo.setReachStep(5000);
			gameInfo.save();
			Db.update("update title_info set use_count=use_count+1 where id=?",findFirst.getInt("id"));
			// 报名次数初始化
			Db.update("update user_info set help_enroll_count=0");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------创建红包运动赛线程处理异常--------" + e.toString());
		}
	}
}