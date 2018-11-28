package cn.simple.kw.service.thread;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import cn.simple.kw.service.SysService;
import cn.simple.kwA.utils.DateUtils;

/**
 * 自动参赛
 */
public class GameEnrollThread implements Runnable {

	Logger logger = Logger.getLogger(GameEnrollThread.class);

	/** 线程启动 */
	public void run() {
		try {

			String phaseNo = DateUtils.getTomorrowYYYYMMDD();
			SysService sysService = new SysService();
			// 获取今天系统发布的挑战赛
			List<Integer> gameIdList = Db.query("select game_id from game_info where phase_no=? and type=1 and del_flg=0", phaseNo);
			for (Integer gameId : gameIdList) {
				sysService.addSysUser(gameId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------自动参赛线程处理异常--------" + e.toString());
		}
	}
}