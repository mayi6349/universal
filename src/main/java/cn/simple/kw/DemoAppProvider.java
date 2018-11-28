package cn.simple.kw;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.druid.DruidPlugin;

import cn.simple.kw.config.APPConfig;
import cn.simple.kw.model.GameOrder;
import cn.simple.kw.model._MappingKit;
import cn.simple.kw.utils.BeanKit;
import cn.simple.kw.utils.ConsistentHash;
import cn.simple.kw.utils.TbConst;
import cn.simple.kwA.factory.LinkedHashMapContainerFactory;
import cn.simple.kwA.model.GameInfoCity;
import cn.simple.kwA.model.GameInfoCountry;
import cn.simple.kwA.model.GameInfoTeam;
import cn.simple.kwA.model.GroupGameOrderCity;
import cn.simple.kwA.model.GroupGameOrderCountry;
import cn.simple.kwA.model.GroupGameOrderTeam;
import cn.simple.kwA.model.UserGameOrderCity;
import cn.simple.kwA.model.UserGameOrderCountry;
import cn.simple.kwA.model.UserGameOrderTeam;
import cn.simple.kwA.model.UserInfo;
import cn.simple.kwA.utils.DateUtils;
import cn.simple.kwA.utils.ToolSqlXml;
import net.sf.json.JSONObject;

/**
 * @author: lbq
 * @email: 526509994@qq.com
 * @date: 17/4/11
 */
public class DemoAppProvider {
	public static void main(String[] args) throws InterruptedException {

		PropKit.use("resources.properties");
		ToolSqlXml.init(true, APPConfig.class.getClassLoader().getResource("").getFile());

		Boolean devMode = PropKit.getBoolean("devMode");
		String jdbcUrlClient = PropKit.get("connection.jdbcUrl");
		// 初始化连接池插件 DruidPlugin
		DruidPlugin dpClient = createDruidPlugin(jdbcUrlClient, PropKit.get("connection.user"),
				PropKit.get("connection.password"));

		// ActiveRecordPlugin
		ActiveRecordPlugin arpClient = new ActiveRecordPlugin(dpClient);
		// 根据sql顺序显示字段
		arpClient.setContainerFactory(new LinkedHashMapContainerFactory());
		arpClient.setShowSql(devMode);
		_MappingKit.mapping(arpClient);
		SqlReporter.setLog(devMode);
		// 手动启动各插件
		dpClient.start();
		arpClient.start();
//		testCountryData();
		
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
					sysAmount = sysAmount.add(addAmount).subtract(order.getEntryFee());
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
		Db.update("update sys_account set usable_balance=usable_balance+?", sysAmount);

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
		// UserGameOrder findFirst = UserGameOrder.dao.findFirst("select * from
		// group_game_order limit 1");
		// System.out.println(findFirst);
		// enrollCallback("U111TD20180718ID1", "test", null);
		// testCountryData();
		// testCityData();
		// testCitycheck();
		// Db.tx(new IAtom() {
		//
		// @Override
		// public boolean run() throws SQLException {
		// try {
		// ScheduledExecutorService scheduler =
		// Executors.newScheduledThreadPool(15);
		// GameOrderWindUpCountryThread country = new
		// GameOrderWindUpCountryThread();
		// GameOrderWindUpCityThread city = new GameOrderWindUpCityThread();
		// scheduler.scheduleWithFixedDelay(country, 0,
		// DateUtils.MILLIS_PER_DAY, TimeUnit.MILLISECONDS);
		// scheduler.scheduleWithFixedDelay(city, 0, DateUtils.MILLIS_PER_DAY,
		// TimeUnit.MILLISECONDS);
		// } catch (Exception e) {
		// e.printStackTrace();
		// return false;
		// }
		// return true;
		// }
		// });
		//
		// testCountrycheck();
		// createTb();

		// // 配置Spring插件
		// SpringPlugin springPlugin = new
		// SpringPlugin("classpath:applicationContext.xml");
		// springPlugin.start();
		//
		// // 缓存服务
		// RedisPlugin redisPlugin = new RedisPlugin(PropKit.get("redis.name"),
		// PropKit.get("redis.server"),
		// PropKit.get("redis.no"));
		// redisPlugin.start();
		//
		// // 没有这一句，启动到这服务就退出了
		// Thread.currentThread().join();
		System.out.println("Demo provider for Dubbo启动完成。");
	}

	@SuppressWarnings("unchecked")
	public static void test() {

		long start = System.currentTimeMillis();
		String beforePhaseNo = DateUtils.getYesterdayYYYYMMDD();

		String title = Db.queryStr("select title from game_info_country where phase_no=?", beforePhaseNo);
		if (title == null) {
			title = "";
		}
		// 未达标红包清0
		String countryU = "user_game_order_country";
		String countryG = "group_game_order_country";
		String no = "";
		String sql_update = "update XXX set amount=0 where phase_no=? and status=0";
		String sql_select = "select user_id,group_no,amount,order_id from XXX where phase_no=? and status=1 order by group_no asc";

		Map<String, JSONObject> userAccountMap = new HashMap<>();
		Map<String, List<String>> userAccountStreamMap = new HashMap<>();
		long usedTime;
		for (int i = 1; i <= 64; i++) {
			if (i < 10) {
				no = "_0" + i;
			} else {
				no = "_" + i;
			}
			Db.update(sql_update.replace("XXX", countryU + no), beforePhaseNo);
			Db.update(sql_update.replace("XXX", countryG + no), beforePhaseNo);

			// 获取需要更新插入的用户信息
			List<GroupGameOrderCountry> dataList = GroupGameOrderCountry.dao
					.find(sql_select.replace("XXX", countryG + no), beforePhaseNo);
			if (dataList != null) {
				String groupNo = "";
				Integer userId = null;
				BigDecimal amount = null;
				String tb = "user_account_stream";
				String tbName = "";
				String insterParams = "";
				for (GroupGameOrderCountry info : dataList) {
					groupNo = info.getGroupNo();
					userId = info.getUserId();
					amount = info.getAmount();
					tbName = ConsistentHash.getTbName(userId.toString(), tb);
					insterParams = "(" + userId + "," + amount + ",'1','0','" + beforePhaseNo + "','" + title + "','"
							+ info.getOrderId() + "')";
					if (userAccountStreamMap.containsKey(tbName)) {
						userAccountStreamMap.get(tbName).add(insterParams);
					} else {
						List<String> params = new ArrayList<>();
						params.add(insterParams);
						userAccountStreamMap.put(tbName, params);
					}

					if (userAccountMap.containsKey(groupNo)) {
						JSONObject params = userAccountMap.get(groupNo);
						List<Integer> userList = (List<Integer>) params.get("userList");
						userList.add(userId);
						params.put("userList", userList);
						userAccountMap.put(groupNo, params);
					} else {
						JSONObject params = new JSONObject();
						List<Integer> userList = new ArrayList<>();
						userList.add(userId);
						// 红包金额
						params.put("amount", amount);
						// 达标用户列表
						params.put("userList", userList);
						userAccountMap.put(groupNo, params);
					}
				}
			}
			// 更新user_account
			// 插入user_account_stream
		}

		// System.out.println(userAccountMap);
		// System.out.println(userAccountStreamMap);

		for (JSONObject params : userAccountMap.values()) {
			StringBuffer sql = new StringBuffer();
			sql = sql.append("update user_account " + "set usable_balance=usable_balance + ?, "
					+ "income_country_count=income_country_count + 1, "
					+ "income_country_amount=income_country_amount + ? where user_id in ");
			String userIds = params.getJSONArray("userList").toString();
			sql.append(userIds);
			Db.update(sql.toString().replace("[", "(").replace("]", ")"), params.get("amount"), params.get("amount"));
		}

		for (Entry<String, List<String>> map : userAccountStreamMap.entrySet()) {
			List<String> value = map.getValue();
			StringBuilder sql_insert = new StringBuilder();
			String insert = "";
			sql_insert.append("insert into ").append(map.getKey())
					.append(" (user_id,amount,io_type,game_type,phase_no,title,order_id) values ");
			for (String params : value) {
				sql_insert.append(params).append(",");
				if (sql_insert.length() >= 500000) {
					insert = sql_insert.toString();
					if (insert.endsWith(",")) {
						insert = insert.substring(0, insert.length() - 1);
					}
					if (!insert.endsWith("values ")) {
						Db.update(insert);
					}
					sql_insert = new StringBuilder();
					sql_insert.append("insert into ").append(map.getKey())
							.append(" (user_id,amount,io_type,game_type,phase_no,title,order_id) values ");
				}
			}
			insert = sql_insert.toString();
			if (insert.endsWith(",")) {
				insert = insert.substring(0, insert.length() - 1);
			}
			if (!insert.endsWith("values ")) {
				Db.update(insert);
			}
		}
		usedTime = (System.currentTimeMillis() - start) / 1000;
		System.out.println("总用时" + usedTime + " seconds.");
	}

	public static void testCountrycheck() {
		String no = "";
		long count = 0;
		long count1 = 0;
		for (int i = 1; i <= 64; i++) {
			if (i < 10) {
				no = "_0" + i;
			} else {
				no = "_" + i;
			}
			count = count
					+ (long) Db.queryFirst("select count(0) from user_account_stream" + no + " where game_type='0'");
			count1 = count1
					+ (long) Db.queryFirst("select count(0) from group_game_order_country" + no + " where status=1");

		}
		System.out.println(count);
		System.out.println(count1);
	}

	public static void testCitycheck() {
		String no = "";
		long count = 0;
		long count1 = 0;
		for (int i = 1; i <= 64; i++) {
			if (i < 10) {
				no = "_0" + i;
			} else {
				no = "_" + i;
			}
			count = count
					+ (long) Db.queryFirst("select count(0) from user_account_stream" + no + " where game_type='1'");
			count1 = count1
					+ (long) Db.queryFirst("select count(0) from group_game_order_city" + no + " where status=1");

		}
		System.out.println(count);
		System.out.println(count1);
	}

	public static void testCountryData() {
		String beforePhaseNo = DateUtils.getYesterdayYYYYMMDD();

		StringBuilder sql_insert = new StringBuilder();
		String insert = "";
		sql_insert.append("insert into user_account (user_id,usable_balance) values ");
		for (int i = 2; i <= 1000000; i++) {
			sql_insert.append("(").append(i).append(",").append(0).append("),");
			if (sql_insert.length() >= 500000) {
				insert = sql_insert.toString();
				if (insert.endsWith(",")) {
					insert = insert.substring(0, insert.length() - 1);
				}
				if (!insert.endsWith("values ")) {
					Db.update(insert);
				}
				sql_insert = new StringBuilder();
				sql_insert.append("insert into user_account (user_id,usable_balance)  values ");
			}
		}
		insert = sql_insert.toString();
		if (insert.endsWith(",")) {
			insert = insert.substring(0, insert.length() - 1);
		}
		if (!insert.endsWith("values ")) {
			Db.update(insert);
		}
System.out.println("gameOrder");
		insert = "insert into game_order (user_id,game_id,phase_no,group_no,order_id,amount,entry_fee,type,status) values ";
		StringBuilder params = new StringBuilder();
		for (int i = 1; i <= 1000000; i++) {
			int hash = ConsistentHash.getHash(String.valueOf(i));
			int groupId = hash % 5000 + 1;
			params.append("(").append(i).append(",1").append(",'").append(beforePhaseNo);
			params.append("','").append(groupId).append("','").append(i).append("',");
			if (i % 4 == 0) {
				params.append("2.5");
			} else {
				params.append("2.2");
			}
			params.append(",2,");

			if (i % 4 == 0) {
				params.append("'0'");
			} else {
				params.append("'1'");
			}
			
			if (i % 3 == 0) {
				params.append(",'0'),");
			} else {
				params.append(",'1'),");
			}

			if (params.length() >= 500000) {
				String sql = insert + params.toString();
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				if (!sql.endsWith("values ")) {
					Db.update(sql);
				}
				params = new StringBuilder();
			}
		}
		String sql = insert + params.toString();
		if (sql.endsWith(",")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		if (!sql.endsWith("values ")) {
			Db.update(sql);
		}
	}

	public static void testCityData() {
		String beforePhaseNo = DateUtils.getYesterdayYYYYMMDD();

		String insert = "";

		insert = "insert into group_game_order_city (user_id,phase_no,city_id,group_no,order_id,amount,status) values ";
		Map<String, StringBuilder> sqlParams = new HashMap<>();
		String groupNo = "";
		String tb = "group_game_order_city";
		String tbName = "";
		for (int i = 1; i <= 1000000; i++) {
			int hash = ConsistentHash.getHash(String.valueOf(i));
			int groupId = hash % 5000 + 1;
			int cityId = hash % 30 + 1;
			groupNo = String.valueOf(groupId);
			tbName = ConsistentHash.getTbName(beforePhaseNo + cityId + groupNo, tb);

			if (sqlParams.containsKey(tbName)) {
				sqlParams.get(tbName).append("(").append(i).append(",'").append(beforePhaseNo).append("',")
						.append(cityId).append(",'").append(groupId).append("','").append(i).append("',").append(3.5);
				if (i % 3 == 0) {
					sqlParams.get(tbName).append(",'0'),");
				} else {
					sqlParams.get(tbName).append(",'1'),");
				}
			} else {
				StringBuilder params = new StringBuilder();
				params.append("(").append(i).append(",'").append(beforePhaseNo);
				params.append("',").append(cityId).append(",'").append(groupId).append("','").append(i).append("',")
						.append(3.5);
				if (i % 3 == 0) {
					params.append(",'0'),");
				} else {
					params.append(",'1'),");
				}
				sqlParams.put(tbName, params);
			}

			if (sqlParams.get(tbName).length() >= 500000) {
				String sql = insert.replace(tb, tbName) + sqlParams.get(tbName).toString();
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				if (!sql.endsWith("values ")) {
					Db.update(sql);
				}
				sqlParams.put(tbName, new StringBuilder());
			}
		}

		for (Entry<String, StringBuilder> map : sqlParams.entrySet()) {
			tbName = map.getKey();
			String sql = insert.replace(tb, tbName) + sqlParams.get(tbName).toString();
			if (sql.endsWith(",")) {
				sql = sql.substring(0, sql.length() - 1);
			}
			if (!sql.endsWith("values ")) {
				Db.update(sql);
			}
		}
	}

	private static DruidPlugin createDruidPlugin(String jdbcUrl, String user, String password) {
		// 初始化连接池插件 DruidPlugin
		DruidPlugin dp = new DruidPlugin(jdbcUrl, user, password);

		Slf4jLogFilter sql_log_filter = new Slf4jLogFilter();
		sql_log_filter.setConnectionLogEnabled(false);
		sql_log_filter.setStatementLogEnabled(false);
		sql_log_filter.setStatementExecutableSqlLogEnable(true);
		sql_log_filter.setResultSetLogEnabled(false);
		dp.addFilter(sql_log_filter);

		// 慢sql记录
		StatFilter sql_stat_filter = new StatFilter();
		sql_stat_filter.setSlowSqlMillis(3 * 1000);
		sql_stat_filter.setLogSlowSql(true);
		dp.addFilter(sql_stat_filter);

		WallFilter wall = new WallFilter();
		wall.setDbType(JdbcConstants.MYSQL);
		WallConfig config = new WallConfig();
		config.setFunctionCheck(false); // 支持数据库函数
		wall.setConfig(config);

		dp.addFilter(wall);
		dp.setFilters("stat,wall");
		dp.setInitialSize(10).setMaxActive(50);
		dp.setTestWhileIdle(true);
		dp.setTestOnBorrow(true);
		dp.setTestOnReturn(true);
		dp.setMaxPoolPreparedStatementPerConnectionSize(20);
		return dp;
	}

	/**
	 * 表相关创建
	 */
	/**
	 * 
	 */
	private static void createTb() {

		for (Integer i = 1; i <= 1000; i++) {
			String sql1 = ToolSqlXml.getSql("static.game_info_team");
			String tbName1 = ConsistentHash.getTbName(i.toString(), "game_info_team");
			long count1 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName1);
			if (count1 == 0) {
				Db.update(sql1.replace("game_info_team", tbName1));
			}
			String sql2 = ToolSqlXml.getSql("static.group_game_order_city");
			String tbName2 = ConsistentHash.getTbName(i.toString(), "group_game_order_city");
			long count2 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName2);
			if (count2 == 0) {
				Db.update(sql2.replace("group_game_order_city", tbName2));
			}
			String sql3 = ToolSqlXml.getSql("static.group_game_order_country");
			String tbName3 = ConsistentHash.getTbName(i.toString(), "group_game_order_country");
			long count3 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName3);
			if (count3 == 0) {
				Db.update(sql3.replace("group_game_order_country", tbName3));
			}
			String sql4 = ToolSqlXml.getSql("static.group_game_order_team");
			String tbName4 = ConsistentHash.getTbName(i.toString(), "group_game_order_team");
			long count4 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName4);
			if (count4 == 0) {
				Db.update(sql4.replace("group_game_order_team", tbName4));
			}
			String sql5 = ToolSqlXml.getSql("static.user_account_stream");
			String tbName5 = ConsistentHash.getTbName(i.toString(), "user_account_stream");
			long count5 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName5);
			if (count5 == 0) {
				Db.update(sql5.replace("user_account_stream", tbName5));
			}
			String sql7 = ToolSqlXml.getSql("static.user_game_order_city");
			String tbName7 = ConsistentHash.getTbName(i.toString(), "user_game_order_city");
			long count7 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName7);
			if (count7 == 0) {
				Db.update(sql7.replace("user_game_order_city", tbName7));
			}
			String sql8 = ToolSqlXml.getSql("static.user_game_order_country");
			String tbName8 = ConsistentHash.getTbName(i.toString(), "user_game_order_country");
			long count8 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName8);
			if (count8 == 0) {
				Db.update(sql8.replace("user_game_order_country", tbName8));
			}
			String sql9 = ToolSqlXml.getSql("static.user_game_order_team");
			String tbName9 = ConsistentHash.getTbName(i.toString(), "user_game_order_team");
			long count9 = Db.queryFirst("SELECT COUNT(0) FROM information_schema.TABLES WHERE TABLE_NAME=?", tbName9);
			if (count9 == 0) {
				Db.update(sql9.replace("user_game_order_team", tbName9));
			}
		}
	}

	public static boolean enrollCallback(String out_trade_no, String transaction_id, String time_end) {

		boolean tx = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				try {
					String userId = "";
					String phaseNo = "";
					String id = "";
					String tbName = "";

					if (out_trade_no.contains("QG")) {
						userId = out_trade_no.substring(1, out_trade_no.indexOf("QG"));
						phaseNo = out_trade_no.substring(out_trade_no.indexOf("QG") + 2, out_trade_no.indexOf("ID"));
						id = out_trade_no.substring(out_trade_no.indexOf("ID") + 2, out_trade_no.length());
						tbName = ConsistentHash.getTbName(userId, TbConst.U_G_O_COUNTRY);
						// gameInfo
						GameInfoCountry gameInfo = GameInfoCountry.dao.findByIdLoadColumns(id,
								"id,entry_fee,sum_count,sum_amount");
						if (gameInfo == null) {
							return false;
						}
						// 分组
						int groupNo = (gameInfo.getSumCount() - 1) / 200 + 1;
						// U更新
						UserGameOrderCountry update = new UserGameOrderCountry();
						update.setGroupNo(String.valueOf(groupNo));
						update.setTradeNo(transaction_id);
						update.setPayTime(time_end);
						update.setPayStatus("1");
						update.setOrderId(out_trade_no);
						Db.update(tbName, "order_id", update.toRecord());

						UserGameOrderCountry uOrder = UserGameOrderCountry.dao
								.findFirst("select * from " + tbName + " where order_id=?", out_trade_no);
						GroupGameOrderCountry gOrder = new GroupGameOrderCountry();

						BeanKit.copyProperties(uOrder, gOrder);
						gOrder.setId(null);
						gOrder.setStatus("0");
						String gtbName = ConsistentHash.getTbName(phaseNo + groupNo, TbConst.G_G_O_COUNTRY);
						Db.save(gtbName, gOrder.toRecord());
						// 总报名人数+1，金额+
						GameInfoCountry updateGameInfo = new GameInfoCountry();
						updateGameInfo.setId(Integer.valueOf(id));
						updateGameInfo.setSumAmount(gameInfo.getSumAmount().add(gameInfo.getEntryFee()));
						updateGameInfo.setSumCount(gameInfo.getSumCount() + 1);
						updateGameInfo.update();

						String nm = getNm(gameInfo.getSumCount() + 1);
						if ("3".equals(nm)) {
							String orderId = "U0QG" + phaseNo + "ID" + id;
							// TODO
							enrollCallback(orderId, "sys", DateUtils.getNowTime());
						}
					} else if (out_trade_no.contains("CS")) {
						userId = out_trade_no.substring(1, out_trade_no.indexOf("CS"));
						phaseNo = out_trade_no.substring(out_trade_no.indexOf("CS") + 2, out_trade_no.indexOf("ID"));
						id = out_trade_no.substring(out_trade_no.indexOf("ID") + 2, out_trade_no.length());
						tbName = ConsistentHash.getTbName(userId, TbConst.U_G_O_CITY);
						// gameInfo
						GameInfoCity gameInfo = GameInfoCity.dao.findByIdLoadColumns(id,
								"id,entry_fee,sum_count,sum_amount");
						if (gameInfo == null) {
							return false;
						}
						// 分组
						int groupNo = (gameInfo.getSumCount() - 1) / 200 + 1;

						// U更新
						UserGameOrderCity update = new UserGameOrderCity();
						update.setGroupNo(String.valueOf(groupNo));
						update.setTradeNo(transaction_id);
						update.setPayTime(time_end);
						update.setPayStatus("1");
						update.setOrderId(out_trade_no);
						Db.update(tbName, "order_id", update.toRecord());

						UserGameOrderCity uOrder = UserGameOrderCity.dao
								.findFirst("select * from " + tbName + " where order_id=?", out_trade_no);
						GroupGameOrderCity gOrder = new GroupGameOrderCity();
						BeanKit.copyProperties(uOrder, gOrder);
						gOrder.setId(null);
						gOrder.setStatus("0");
						String gtbName = ConsistentHash.getTbName(phaseNo + gOrder.getCityId() + groupNo,
								TbConst.G_G_O_CITY);
						Db.save(gtbName, gOrder.toRecord());
						// 总报名人数+1，金额+
						GameInfoCity updateGameInfo = new GameInfoCity();
						updateGameInfo.setId(Integer.valueOf(id));
						updateGameInfo.setSumAmount(gameInfo.getSumAmount().add(gameInfo.getEntryFee()));
						updateGameInfo.setSumCount(gameInfo.getSumCount() + 1);
						updateGameInfo.update();

					} else if (out_trade_no.contains("TD")) {
						userId = out_trade_no.substring(1, out_trade_no.indexOf("TD"));
						phaseNo = out_trade_no.substring(out_trade_no.indexOf("TD") + 2, out_trade_no.indexOf("ID"));
						id = out_trade_no.substring(out_trade_no.indexOf("ID") + 2, out_trade_no.length());
						tbName = ConsistentHash.getTbName(userId, TbConst.U_G_O_TEAM);
						// gameInfo
						String teamTbName = ConsistentHash.getTbName(phaseNo, TbConst.G_I_TEAM);
						GameInfoTeam gameInfo = GameInfoTeam.dao.findFirst(
								"select id,entry_fee,sum_count,sum_amount from " + teamTbName + " where id=?", id);
						if (gameInfo == null) {
							return false;
						}
						// 分组
						int groupNo = (gameInfo.getSumCount() - 1) / 200 + 1;

						// U更新
						UserGameOrderTeam update = new UserGameOrderTeam();
						update.setGroupNo(String.valueOf(groupNo));
						update.setTradeNo(transaction_id);
						update.setPayTime(time_end);
						update.setPayStatus("1");
						update.setOrderId(out_trade_no);
						Db.update(tbName, "order_id", update.toRecord());
						UserGameOrderTeam uOrder = UserGameOrderTeam.dao
								.findFirst("select * from " + tbName + " where order_id=?", out_trade_no);
						GroupGameOrderTeam gOrder = new GroupGameOrderTeam();
						BeanKit.copyProperties(uOrder, gOrder);
						gOrder.setId(null);
						gOrder.setStatus("0");
						String gtbName = ConsistentHash.getTbName(phaseNo + gOrder.getTeamId() + groupNo,
								TbConst.G_G_O_TEAM);
						Db.save(gtbName, gOrder.toRecord());
						// 总报名人数+1，金额+
						GameInfoTeam updateGameInfo = new GameInfoTeam();
						updateGameInfo.setId(Integer.valueOf(id));
						updateGameInfo.setSumAmount(gameInfo.getSumAmount().add(gameInfo.getEntryFee()));
						updateGameInfo.setSumCount(gameInfo.getSumCount() + 1);
						Db.update(teamTbName, updateGameInfo.toRecord());
					}

					// 报名次数+1
					UserInfo userInfo = UserInfo.dao.findFirst(
							"select enroll_count,max_enroll_count,from_user_id from user_info where user_id=?", userId);
					if (userInfo != null) {
						userInfo.setEnrollCount(userInfo.getEnrollCount() + 1);
						userInfo.update();

						Integer fromUserId = userInfo.getFromUserId();
						if (fromUserId != null) {
							Db.update("update user_info set max_enroll_count=max_enroll_count+1 where user_id=?",
									userInfo.getFromUserId());
						}
					}
				} catch (Exception e) {
					return false;
				}
				return true;
			}
		});

		return tx;
	}

	private static String getNm(Integer num) {
		while (num > 9) {
			num = num % 10; // 所报数的个位数
		}
		return num.toString();
	}
}