package cn.simple.kw;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.druid.DruidPlugin;

import cn.simple.kw.config.APPConfig;
import cn.simple.kw.model._MappingKit;
import cn.simple.kwA.factory.LinkedHashMapContainerFactory;
import cn.simple.kwA.utils.ToolSqlXml;
import kohgylw.kcnamer.core.KCNamer;

/**
 * @author: lbq
 * @email: 526509994@qq.com
 * @date: 17/4/11
 */
public class AddSysMan {
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

		long start = System.currentTimeMillis();
		long usedTime;
		System.out.println("start");

		StringBuilder insert_user_info = new StringBuilder();
		insert_user_info.append("insert into user_info (wx_open_id,nick_name,icon_url,from_user_id) values ");
		String value = "";
		String nickName = "";
		String iconUrl = "";
		KCNamer kcNamer = new KCNamer();
		for (int i = 0; i < 1000; i++) {
			if (i % 3 == 0) {
				nickName = kcNamer.getRandomFemaleName();
				iconUrl = "https://www.walkvalue.com/icon/girl/" + i / 3 + ".jpg";
			} else {
				nickName = kcNamer.getRandomMaleName();
				iconUrl = "https://www.walkvalue.com/icon/man/" + i + ".jpg";
			}
			value = "(" + i + ",'" + nickName + "','" + iconUrl + "',0),";
			insert_user_info.append(value);
			if (insert_user_info.length() >= 500000) {
				String sql = insert_user_info.toString();
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1);
				}
				if (!sql.endsWith("values ")) {
					// TODO 线程
//					System.out.println(sql);
					Db.update(sql);
				}
				insert_user_info = new StringBuilder();
				insert_user_info.append("insert into user_info (wx_open_id,nick_name,icon_url,from_user_id) values ");
			}
		}

		String sql = insert_user_info.toString();
		if (sql.endsWith(",")) {
			sql = sql.substring(0, sql.length() - 1);
		}
		if (!sql.endsWith("values ")) {
//			System.out.println(sql);
			Db.update(sql);
		}

		System.out.println("Demo provider for Dubbo启动完成。");
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

	private static String getNm(Integer num) {
		while (num > 9) {
			num = num % 10; // 所报数的个位数
		}
		return num.toString();
	}
}