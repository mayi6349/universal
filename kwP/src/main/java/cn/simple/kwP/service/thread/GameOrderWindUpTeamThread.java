package cn.simple.kwP.service.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import cn.simple.kwA.model.GroupGameOrderTeam;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwP.utils.ConsistentHash;
import net.sf.json.JSONObject;

/**
 * 自动结算红包运动赛
 */
public class GameOrderWindUpTeamThread implements Runnable {

	Logger logger = Logger.getLogger(GameOrderWindUpTeamThread.class);

	/** 线程启动 */
	@SuppressWarnings("unchecked")
	public void run() {
		try {

			/*
			 * 1、group未达标红包清0； 2、达标的账户信息更新； 3、流水记录追加
			 */
			long start = System.currentTimeMillis();
			System.out.println("start");

			String beforePhaseNo = DateUtils.getYesterdayYYYYMMDD();
			String teamG = "group_game_order_team";
			String no = "";
			String update_gtb = "update XXX set amount=0 where status=0 and phase_no=?";
			String select_gtb = "select user_id,group_no,amount from XXX where status=1 and phase_no=?";

			Map<String, JSONObject> userAccountMap = new HashMap<>();
			Map<String, List<String>> userAccountStreamMap = new HashMap<>();
			long usedTime;
			// 未达标红包清0
			for (int i = 1; i <= 64; i++) {
				no = "_" + i;
				Db.update(update_gtb.replace("XXX", teamG + no), beforePhaseNo);

				// 获取需要结算的信息
				List<GroupGameOrderTeam> dataList = GroupGameOrderTeam.dao.find(select_gtb.replace("XXX", teamG + no),
						beforePhaseNo);
				if (dataList != null) {
					Integer teamId = null;
					String groupNo = "";
					Integer userId = null;
					BigDecimal amount = null;
					String tb = "user_account_stream";
					String tbName = "";
					String insterParams = "";
					String key = "";

					for (GroupGameOrderTeam info : dataList) {
						teamId = info.getTeamId();
						groupNo = info.getGroupNo();
						key = teamId + "_" + groupNo;
						userId = info.getUserId();
						amount = info.getAmount();
						tbName = ConsistentHash.getTbName(userId.toString(), tb);
						insterParams = "(" + userId + ",'" + beforePhaseNo + "','1','" + info.getTeamName() + "','1',"
								+ amount + ",'" + info.getOrderId() + "')";
						if (userAccountStreamMap.containsKey(tbName)) {
							userAccountStreamMap.get(tbName).add(insterParams);
						} else {
							List<String> params = new ArrayList<>();
							params.add(insterParams);
							userAccountStreamMap.put(tbName, params);
						}

						if (userAccountMap.containsKey(key)) {
							JSONObject params = userAccountMap.get(key);
							List<Integer> userList = (List<Integer>) params.get("userList");
							userList.add(userId);
							params.put("userList", userList);
							userAccountMap.put(key, params);
						} else {
							JSONObject params = new JSONObject();
							List<Integer> userList = new ArrayList<>();
							userList.add(userId);
							// 红包金额
							params.put("amount", amount);
							// 达标用户列表
							params.put("userList", userList);
							userAccountMap.put(key, params);
						}
					}
				}
			}

			// 更新账户信息
			for (JSONObject params : userAccountMap.values()) {
				StringBuffer update_account = new StringBuffer();
				update_account = update_account.append("update user_account set usable_balance=usable_balance + ?, "
						+ "income_team_count=income_team_count + 1, "
						+ "income_team_amount=income_team_amount + ? where user_id in ");
				String userIds = params.getJSONArray("userList").toString();
				update_account.append(userIds.replace("[", "(").replace("]", ")"));
				Db.update(update_account.toString(), params.get("amount"), params.get("amount"));
			}

			// 追加流水信息
			for (Entry<String, List<String>> map : userAccountStreamMap.entrySet()) {
				List<String> value = map.getValue();
				StringBuilder insert_stream = new StringBuilder();
				String insert = "";
				insert_stream.append("insert into ").append(map.getKey())
						.append(" (user_id,phase_no,game_type,name,io_type,amount,order_id) values ");
				for (String params : value) {
					insert_stream.append(params).append(",");
					if (insert_stream.length() >= 500000) {
						insert = insert_stream.toString();
						if (insert.endsWith(",")) {
							insert = insert.substring(0, insert.length() - 1);
						}
						if (!insert.endsWith("values ")) {
							Db.update(insert);
						}
						insert_stream = new StringBuilder();
						insert_stream.append("insert into ").append(map.getKey())
								.append(" (user_id,phase_no,game_type,name,io_type,amount,order_id) values ");
					}
				}

				insert = insert_stream.toString();
				if (insert.endsWith(",")) {
					insert = insert.substring(0, insert.length() - 1);
				}
				if (!insert.endsWith("values ")) {
					Db.update(insert);
				}
			}
			usedTime = (System.currentTimeMillis() - start) / 1000;
			System.out.println("总用时" + usedTime + " seconds.");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------团赛结算线程处理异常--------" + e.toString());
		}
	}
}