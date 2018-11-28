package cn.simple.kw.service.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.jfinal.plugin.activerecord.Db;

import cn.simple.kwA.utils.DateUtils;

/**
 * 随机分组
 */
public class GroupNoSetThread implements Runnable {

	Logger logger = Logger.getLogger(GroupNoSetThread.class);

	/** 线程启动 */
	public void run() {
		try {

			// 随机分组
			String today = DateUtils.getTodayYYYYMMDD();
			// TODO test
//			today = DateUtils.getTomorrowYYYYMMDD();
			List<Integer> gameIdList = Db.query("select game_id from game_info where phase_no=? and del_flg=0", today);
			List<Integer> sysList = new ArrayList<>();
			List<Integer> userList = new ArrayList<>();
			List<Integer> groupList = new ArrayList<>();
			Map<Integer, Map<String, List<Integer>>> gameMap = new HashedMap();
			Map<String, List<Integer>> groupMap = new HashedMap();
			for (Integer gameId : gameIdList) {
				// 清空
				groupMap.clear();
				
				// 用户系统不分
				// 报名的用户列表获取
				userList = Db.query(
						"select id from game_order where phase_no=? and game_id=? and pay_status=1", today,
						gameId);

				Integer enrollCount = userList.size();
				String groupNo = getGroupNo(enrollCount);
				groupMap.put(groupNo, groupList);
				for (Integer id : userList) {
					groupList = groupMap.get(groupNo);
					// 小组大于等于200了换组重新开始
					if (groupList.size() >= 200) {
						do {
							groupNo = getGroupNo(enrollCount);
						} while (groupMap.containsKey(groupNo));
						groupList.clear();
					}
					groupList.add(id);
					groupMap.put(groupNo, groupList);
				}

				// 最后一组不满200人补足
				groupList = groupMap.get(groupNo);
				if (groupList.size() < 20 && groupList.size() > 0 && groupMap.size() > 1) {
					groupMap.remove(groupNo);
					String[] keys = groupMap.keySet().toArray(new String[0]);
					Random random = new Random();
					// 循环
					for (Integer id : groupList) {
						groupMap.get(keys[random.nextInt(keys.length)]).add(id);
					}
				}
				gameMap.put(gameId, groupMap);
				
				// 用户系统区分
//				// 报名的用户列表获取
//				userList = Db.query(
//						"select id from game_order where phase_no=? and game_id=? and pay_status=1 and type='0'", today,
//						gameId);
//				// 有报名用户
//				if (userList != null && userList.size() > 0) {
//					// 系统报名用户
//					sysList = Db.query(
//							"select id from game_order where phase_no=? and game_id=? and pay_status=1 and type='1'",
//							today, gameId);
//					// 有系统报名
//					if (sysList != null) {
//						Integer enrollCount = userList.size() + sysList.size();
//						String groupNo = getGroupNo(enrollCount);
//						groupMap.put(groupNo, groupList);
//						for (Integer id : userList) {
//							groupList = groupMap.get(groupNo);
//							// 小组大于等于200了换组重新开始
//							if (groupList.size() >= 200) {
//								do {
//									groupNo = getGroupNo(enrollCount);
//								} while (groupMap.containsKey(groupNo));
//								groupList.clear();
//							}
//							groupList.add(id);
//							groupMap.put(groupNo, groupList);
//							// 末位数是8的加入一条系统报名
//							if ("8".equals(getNm(groupList.size()))) {
//								// 追加系统第一条
//								if (sysList.size() > 0) {
//									groupList.add(sysList.get(0));
//									sysList.remove(0);
//								}
//							}
//						}
//
//						// 最后一组不满200人补足
//						groupList = groupMap.get(groupNo);
//						if (groupList.size() < 200 && groupList.size() >= 180) {
//							int needCount = 200 - groupList.size();
//							// 够补全的就补全
//							if (sysList.size() >= needCount) {
//								groupList.addAll(sysList.subList(0, needCount - 1));
//								// 不够补全
//							} else {
//								groupList.addAll(sysList);
//							}
//							groupMap.put(groupNo, groupList);
//						} else if (groupList.size() < 20 && groupList.size() > 0) {
//							if (groupMap.size() > 1) {
//								groupMap.remove(groupNo);
//								String[] keys = groupMap.keySet().toArray(new String[0]);
//								Random random = new Random();
//								// 循环
//								for (Integer id : groupList) {
//									groupMap.get(keys[random.nextInt(keys.length)]).add(id);
//								}
//							}
//
//						} else {
//							// 补齐10%
//							int needCount = groupList.size() / 10;
//							// 够补全的就补全
//							if (sysList.size() >= needCount) {
//								groupList.addAll(sysList.subList(0, needCount - 1));
//								// 不够补全
//							} else {
//								groupList.addAll(sysList);
//							}
//							groupMap.put(groupNo, groupList);
//						}
//					} else {
//
//						Integer enrollCount = userList.size();
//						String groupNo = getGroupNo(enrollCount);
//						groupMap.put(groupNo, groupList);
//						for (Integer id : userList) {
//							groupList = groupMap.get(groupNo);
//							// 小组大于等于200了换组重新开始
//							if (groupList.size() >= 200) {
//								do {
//									groupNo = getGroupNo(enrollCount);
//								} while (groupMap.containsKey(groupNo));
//								groupList.clear();
//							}
//							groupList.add(id);
//							groupMap.put(groupNo, groupList);
//						}
//
//						// 最后一组不满200人补足
//						groupList = groupMap.get(groupNo);
//						if (groupList.size() < 20 && groupList.size() > 0) {
//							groupMap.remove(groupNo);
//							String[] keys = groupMap.keySet().toArray(new String[0]);
//							Random random = new Random();
//							// 循环
//							for (Integer id : groupList) {
//								groupMap.get(keys[random.nextInt(keys.length)]).add(id);
//							}
//						}
//					}
//					gameMap.put(gameId, groupMap);
//				}
			}

			for (Entry<Integer, Map<String, List<Integer>>> game : gameMap.entrySet()) {
				// 总体groupNo变更
				StringBuilder update_groupNo = new StringBuilder();
				update_groupNo.append("update game_order set group_no=case ");
				String strId = "";
				for (Entry<String, List<Integer>> group : game.getValue().entrySet()) {
					strId = " when id in " + group.getValue().toString().replace("[", "(").replace("]", ")") + " then "
							+ group.getKey();
					update_groupNo.append(strId);
					if (update_groupNo.length() >= 500000) {
						update_groupNo.append(" else '' end where phase_no=? and game_id=? and pay_status=1");
						String sql = update_groupNo.toString();
						// TODO 线程
						Db.update(sql, today, game.getKey());
						update_groupNo = new StringBuilder();
						update_groupNo.append("update game_order set group_no=case ");
					}
				}

				if (!update_groupNo.toString().endsWith("case ")) {
					update_groupNo.append(" else '' end where phase_no=? and game_id=? and pay_status=1");
					String sql = update_groupNo.toString();
					// TODO 线程
					Db.update(sql, today, game.getKey());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("--------随机分组线程处理异常--------" + e.toString());
		}
	}

	/**
	 * 生成随机小组号
	 * 
	 * @param enrollCount
	 * @return
	 */
	private String getGroupNo(Integer enrollCount) {
		int maxGroupNo = (enrollCount - 1) / 200 + 1;
		// 生成随机组号
		Random randData = new Random();
		int groupNo = randData.nextInt(maxGroupNo + 1);
		if (groupNo == 0) {
			return getGroupNo(enrollCount);
		}
		return String.valueOf(groupNo);
	}

	private String getNm(Integer num) {
		while (num > 9) {
			num = num % 10; // 所报数的个位数
		}
		return num.toString();
	}
}