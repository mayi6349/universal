package cn.simple.kwP.service.thread;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import cn.simple.kwA.model.CityInfo;
import cn.simple.kwA.model.GameInfoCountry;
import cn.simple.kwA.utils.DateUtils;

/**
 * 自动创建红包运动赛
 */
public class GameCreateThread implements Runnable {

	Logger logger = Logger.getLogger(GameCreateThread.class);

	/** 线程启动 */
	public void run() {
		try {

			String nextPhaseNo = DateUtils.getTomorrowYYYYMMDD();
			// 报名次数初始化
			Db.update("update user_info set enroll_count=0,max_enroll_count=share_count");
			Db.update("update user_account set pay_country_count=0,pay_city_count=0,pay_team_count=0 where from_user_id=1");

			// 全国
			GameInfoCountry country = new GameInfoCountry();
			country.setPhaseNo(nextPhaseNo);
			country.setEntryFee(new BigDecimal("2"));
			country.setReachStep(5000);
			country.save();

			// 城市
			List<CityInfo> cityList = CityInfo.dao.find("select city_id,city_name,entry_fee,reach_step from city_info");
			StringBuilder insert = new StringBuilder();
			String sql_insert = "";
			insert.append("insert into game_info_city (phase_no,city_id,city_name,entry_fee,reach_step) values (");
			if (cityList != null) {
				for (CityInfo info : cityList) {
					insert.append("'" + nextPhaseNo + "',");
					insert.append("'" + info.getCityId() + "',");
					insert.append("'" + info.getCityName() + "',");
					insert.append("'" + info.getEntryFee() + "',");
					insert.append("'" + info.getReachStep() + "'),");
				}
			}
			sql_insert = insert.toString();
			if (sql_insert.endsWith(",")) {
				sql_insert = sql_insert.substring(0, sql_insert.length() - 1);
			}
			if (!sql_insert.endsWith("values (")) {
				Db.update(sql_insert);
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------创建红包运动赛线程处理异常--------" + e.toString());
		}
	}
}