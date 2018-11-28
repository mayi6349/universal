package cn.simple.kw.service.thread;

import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import cn.simple.kw.model.GameOrder;
import cn.simple.kw.service.UserGameService;
import cn.simple.kwA.utils.DateUtils;

/**
 * 自动提交步数
 */
public class StepReportThread implements Runnable {

	Logger logger = Logger.getLogger(StepReportThread.class);

	/** 线程启动 */
	public void run() {
		try {

			long currentTimeMillis = System.currentTimeMillis() / 1000;
			Long dateBegin = DateUtils.getDateBegin();
			// 早6:08到23:16
			if ((currentTimeMillis - dateBegin > 6 * 60 * 60 + 480)
					&& ((currentTimeMillis - dateBegin < 23 * 60 * 60 + 960))) {
				String phaseNo = DateUtils.getTodayYYYYMMDD();
				// 获取今天参赛的系统用户
				List<GameOrder> userWalkStepList = GameOrder.dao.find(
						"select DISTINCT user_id,walk_step,reach_step from game_order "
								+ "where phase_no=? and pay_status=1 and type=1 and (walk_step-reach_step < 666)",
						phaseNo);
				Random randData = new Random();
//				for (GameOrder userWalkStep : userWalkStepList) {
//					Integer lower = userWalkStep.getReachStep() / 14;
//					Integer step = userWalkStep.getWalkStep() + lower + randData.nextInt(1000);
//					UserGameService userGameService = new UserGameService();
//					userGameService.reportStep(userWalkStep.getUserId(), step);
//				}
				
				if (userWalkStepList != null) {
					for (int i = 0; i < userWalkStepList.size(); i++) {
						if (i < userWalkStepList.size() * 0.2) {
							Integer lower = userWalkStepList.get(i).getReachStep() / 14;
							Integer step = userWalkStepList.get(i).getWalkStep() + lower + randData.nextInt(2000);
							UserGameService userGameService = new UserGameService();
							userGameService.reportStep(userWalkStepList.get(i).getUserId(), step);
						} else if (i < userWalkStepList.size() * 0.5 && ((currentTimeMillis - dateBegin > 18 * 60 * 60 + 960)
								&& ((currentTimeMillis - dateBegin < 17 * 60 * 60 + 480)))) {
							Integer lower = userWalkStepList.get(i).getReachStep();
							Integer step = userWalkStepList.get(i).getWalkStep() + lower + randData.nextInt(3000);
							UserGameService userGameService = new UserGameService();
							userGameService.reportStep(userWalkStepList.get(i).getUserId(), step);
						} else if (i < userWalkStepList.size() * 0.8 && ((currentTimeMillis - dateBegin > 20 * 60 * 60 + 960)
								&& ((currentTimeMillis - dateBegin < 22 * 60 * 60 + 480)))) {
							Integer lower = userWalkStepList.get(i).getReachStep();
							Integer step = userWalkStepList.get(i).getWalkStep() + lower + randData.nextInt(4000);
							UserGameService userGameService = new UserGameService();
							userGameService.reportStep(userWalkStepList.get(i).getUserId(), step);
						} else if ((currentTimeMillis - dateBegin > 22 * 60 * 60 + 480)
								&& ((currentTimeMillis - dateBegin < 23 * 60 * 60 + 960))) {
							Integer lower = userWalkStepList.get(i).getReachStep();
							Integer step = userWalkStepList.get(i).getWalkStep() + lower + randData.nextInt(5000);
							UserGameService userGameService = new UserGameService();
							userGameService.reportStep(userWalkStepList.get(i).getUserId(), step);
						}
					}
				}
//				Thread reportStep = new Thread(new Runnable() {
//					@Override
//					public void run() {
//						// TIDO
//					}
//				});
//				reportStep.start();
//				Thread.sleep(10);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------自动提交步数线程处理异常--------" + e.toString());
		}
	}
}