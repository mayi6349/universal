package cn.simple.kw.service.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model.SysAccount;
import cn.simple.kwA.utils.DateUtils;

/**
 * 自动结算红包运动赛
 */
public class GameOrderWindUpThread implements Runnable {

	Logger logger = Logger.getLogger(GameOrderWindUpThread.class);

	/** 线程启动 */
	public void run() {
		try {
			long start = System.currentTimeMillis();
			long usedTime;
			System.out.println("start");

			// 达标统计
			List<GameOrder> reachList = GameOrder.dao.find(
					"select user_id,phase_no,game_id,group_no,title,order_id,entry_fee,amount,type from game_order where phase_no=? and status=1",
					DateUtils.getYesterdayYYYYMMDD());

			Map<Integer, BigDecimal> userAccountMap = new HashMap<>();
			BigDecimal sysAmount = new BigDecimal(0);
			// 流水追加
			if (reachList != null) {
				StringBuilder insert_stream = new StringBuilder();
				insert_stream.append(
						"insert into user_account_stream (user_id,amount,io_type,pay_type,phase_no,title,order_id) values ");
				Integer userId = null;
				String insertValues = "";
				String type = "";
				BigDecimal addAmount = new BigDecimal(0);
				for (GameOrder order : reachList) {
					type = order.getType();
					userId = order.getUserId();
					// addAmount = order.getAmount();
					addAmount = order.getAmount();

					if ("0".equals(type)) {
						// 用户余额追加情况
						if (userAccountMap.containsKey(userId)) {
							// 达标奖励金累积
							userAccountMap.put(userId, userAccountMap.get(userId).add(addAmount));
						} else {
							userAccountMap.put(userId, addAmount);
						}

						insertValues = "(" + order.getUserId() + "," + order.getAmount() + ",'1','0','"
								+ order.getPhaseNo() + "','" + order.getTitle() + "','" + order.getOrderId() + "')";
						insert_stream.append(insertValues).append(",");
						if (insert_stream.length() >= 500000) {
							String sql = insert_stream.toString();
							if (sql.endsWith(",")) {
								sql = sql.substring(0, sql.length() - 1);
							}
							if (!sql.endsWith("values ")) {
								// TODO 线程
								Db.update(sql);
							}
							insert_stream = new StringBuilder();
							insert_stream.append(
									"insert into user_account_stream (user_id,amount,io_type,pay_type,phase_no,title,order_id) values ");
						}
					} else {
						sysAmount = sysAmount.add(addAmount);
					}
				}
				String sql = insert_stream.toString();
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				if (!sql.endsWith("values ")) {
					System.out.println(sql);
					Db.update(sql);
				}
			}

			// 系统账户收益
			SysAccount sysAccount = SysAccount.dao.findFirst("select usable_balance,frozen_balance from sys_account");
			BigDecimal frozenBalance = sysAccount.getFrozenBalance();
			BigDecimal usableBalance = sysAccount.getUsableBalance();

			if (sysAmount.compareTo(frozenBalance) > 0) {
				frozenBalance = new BigDecimal(0);
				sysAmount = sysAmount.subtract(frozenBalance);
				usableBalance = usableBalance.add(sysAmount).subtract(frozenBalance);
			} else {
				frozenBalance = frozenBalance.subtract(sysAmount);
			}
			Db.update("update sys_account set usable_balance=?, frozen_balance=?", usableBalance, frozenBalance);

			// 总体余额变更
			StringBuilder update_account = new StringBuilder();
			update_account.append("update user_account set usable_balance=case user_id");
			String updateValues = "";
			List<Integer> userList = new ArrayList<>();
			for (Entry<Integer, BigDecimal> map : userAccountMap.entrySet()) {
				userList.add(map.getKey());
				updateValues = " when " + map.getKey() + " then usable_balance +" + map.getValue();
				update_account.append(updateValues);
				if (update_account.length() >= 500000) {
					update_account.append(" end where user_id in ")
							.append(userList.toString().replace("[", "(").replace("]", ")"));
					String sql = update_account.toString();
					// TODO 线程
					Db.update(sql);
					update_account = new StringBuilder();
					update_account.append("update user_account set usable_balance=case user_id");
					userList.clear();
				}
			}

			if (userList.size() > 0) {
				update_account.append(" end where user_id in ")
						.append(userList.toString().replace("[", "(").replace("]", ")"));
				String sql = update_account.toString();
				System.out.println(sql);
				// TODO 线程
				Db.update(sql);
			}

			usedTime = (System.currentTimeMillis() - start) / 1000;
			System.out.println("总用时" + usedTime + " seconds.");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------结算线程处理异常--------" + e.toString());
		}
	}
}