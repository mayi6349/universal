package cn.simple.kwA.utils;

import java.lang.reflect.InvocationTargetException;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.SqlReporter;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.dialect.PostgreSqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.druid.DruidPlugin;

/**
 * model生成器
 * 
 * @author may
 */
public class ApiGenerator {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException {

		// base model 所使用的包名
		String baseModelPackageName = "cn.simple.kw.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/cn/simple/kw/model/base";

		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "cn.simple.kw.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";

		// 初始化连接池插件 DruidPlugin
		Prop prop = PropKit.use("resources.properties");

		DruidPlugin dp = new DruidPlugin(prop.get("connection.jdbcUrl"), prop.get("connection.user"),
				prop.get("connection.password"));
		dp.start();

		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
		// 根据sql顺序显示字段
		arp.setShowSql(false);
		SqlReporter.setLog(false);
		arp.start();

//		C3p0Plugin dp = new C3p0Plugin(prop.get("connection.jdbcUrl"), prop.get("connection.user"),
//				prop.get("connection.password"),PropKit.get("connection.driver"));
//        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);    
//        arp.setShowSql(true);        
////        arp.addMapping("post_user","user_id",User.class);
//        dp.start();
//        arp.setDialect(new PostgreSqlDialect());
//        arp.start();
		
		// 创建生成器
		Generator gernerator = new Generator(dp.getDataSource(), baseModelPackageName, baseModelOutputDir,
				modelPackageName, modelOutputDir);
		gernerator.setDialect(new MysqlDialect());
//		gernerator.setDialect(new PostgreSqlDialect());
		// 添加不需要生成的表名
		// gernerator.addExcludedTable("view_","sys_","sys_role_privilege","sys_user_role");
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(true);
		gernerator.setRemovedTableNamePrefixes("ip_");
		// 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为
		// "User"而非 OscUser
		// gernerator.setRemovedTableNamePrefixes("t_","sys_");
		// 生成
		gernerator.generate();
	}
}
