package cn.simple.kwP.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

public class DubboXmlGenerator {

	// 当前项目目录，以 '/'' 结尾
	private static String projectPath;
	// 搜索package范围
	private static String packagePath = "/src/main/java/cn/simple/kwP/service";
	// 输入路径
	private static String outPath = "/src/main/resources/";

	// 当前模块类别 consumer 或者 provider
	// private static String moduleType = "consumer";
	private static String moduleType = "provider";
	// registry 协议
	private static String registryProtocol = "zookeeper";
	// registry 地址参数
	private static String registryAddress = "zookeeper://127.0.0.1:2181";
	// protocol 名字
	private static String protocolName = "dubbo";
	// protocol 主机
	private static String protocolHost = "127.0.0.1";
	// protocol 端口
	private static String protocolPort = "20880";

	/**
	 * 针对 consumer 生成基于接口的类的 dubbo 声明语句
	 */
	private static String consumerClass2Config(File curFile) throws IOException {
		String classConfig = "";

		// dubbo:service interface
		ArrayList<String> idName = new ArrayList<String>();
		ArrayList<String> interfaceName = new ArrayList<String>();

		BufferedReader tempBfr = new BufferedReader(new FileReader(curFile));
		String tempStr;
		while ((tempStr = tempBfr.readLine()) != null) {
			if (tempStr.indexOf("@BY_NAME") >= 0) {
				tempStr = tempBfr.readLine();
				String tempIdName = "";
				if (tempStr.indexOf("public") >= 0) {
					tempIdName = tempStr.substring(tempStr.indexOf("public") + "public".length() + 1);
				} else if (tempStr.indexOf("private") >= 0) {
					tempIdName = tempStr.substring(tempStr.indexOf("private") + "private".length() + 1);
				} else if (tempStr.indexOf("protected") >= 0) {
					tempIdName = tempStr.substring(tempStr.indexOf("protected") + "protected".length() + 1);
				}

				if (tempIdName.equals("")) {
					System.out.println("consumer 声明接口变量 未支持");
				} else {
					tempIdName = tempIdName.substring(0, tempIdName.indexOf(" "));
					boolean alreadyAdded = false;
					for (int i = 0; i < idName.size(); i++) {
						if (idName.get(i).equals(tempIdName)) {
							alreadyAdded = true;
							break;
						}
					}
					if (!alreadyAdded)
						idName.add(tempIdName);
				}
			}
		}
		tempBfr.close();

		for (int i = 0; i < idName.size(); i++) {
			tempBfr = new BufferedReader(new FileReader(curFile));
			while ((tempStr = tempBfr.readLine()) != null) {
				if (tempStr.indexOf(idName.get(i)) >= 0) {
					interfaceName.add(tempStr.substring("import ".length(), tempStr.length() - 1));
					idName.set(i, (Character.toString(Character.toLowerCase(idName.get(i).charAt(0)))
							+ idName.get(i).substring(1)));

					// dubbo:service
					classConfig += "\t<!-- 声明服务代理 -->\n" + "\t<dubbo:reference id=\"" + idName.get(i)
							+ "\" interface=\"" + interfaceName.get(i) + "\" />\n\n";
					break;
				}
			}
		}

		return classConfig;
	}

	/**
	 * 针对 provider 生成基于接口的类的 dubbo 声明语句
	 */
	private static String providerClass2Config(File curFile, String javaPath) throws IOException {
		String classConfig = "";

		// dubbo:service interface
		String interfaceName = "";

		BufferedReader tempBfr = new BufferedReader(new FileReader(curFile));
		String tempStr;
		while ((tempStr = tempBfr.readLine()) != null) {
			if (tempStr.indexOf("class") >= 0) {
				if (tempStr.indexOf("implements") >= 0) {
					interfaceName = tempStr.substring(tempStr.indexOf("implements") + "implements".length() + 1)
							.replaceAll("(\\s)|\\{", "");
				}
				break;
			}
		}
		tempBfr.close();

		// 如果该类是实现了一个接口
		if (interfaceName != "") {
			tempBfr = new BufferedReader(new FileReader(curFile));
			String packPath = "";
			while ((tempStr = tempBfr.readLine()) != null) {
				if (tempStr.indexOf("package ") >= 0 && StringUtils.isEmpty(packPath)) {
					packPath = tempStr.substring("package ".length(), tempStr.length() - 1);
				}
				if (tempStr.indexOf(interfaceName) >= 0) {
					interfaceName = tempStr.substring("import ".length(), tempStr.length() - 1);
					break;
				}
			}

			String curFileName = curFile.getName().replaceFirst(".java", "");
			// bean id 和 dubbo:service ref
			String idName = (Character.toString(Character.toLowerCase(curFileName.charAt(0)))
					+ curFileName.substring(1));

			// bean class
//			String className = curFile.getAbsolutePath().replaceFirst(javaPath, "").replaceFirst(".java", "")
//					.replaceAll("/", ".");
			String className = packPath + "." + curFileName;
			// bean
			classConfig = "\t<!-- 声明 " + curFileName + " 服务实例 -->\n" + "\t<bean id=\"" + idName + "\" class=\""
					+ className + "\" ></bean>\n";
			// dubbo:service
			classConfig += "\t<!-- 声明需要暴露的服务接口 -->\n" + "\t<dubbo:service interface=\"" + interfaceName + "\" ref=\""
					+ idName + "\" />\n\n";

		}

		return classConfig;
	}

	/**
	 * 找到该 moudle java 目录下的所有 java 文件
	 * 
	 * @return dubbo:service 和 bean 的声明语句
	 */
	private static String findClass(File curDir, String javaPath) throws IOException {
		String classConfig = "";
		File[] files = curDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				classConfig += findClass(files[i], javaPath);
			} else if (!files[i].isHidden()) {
				System.out.println("找到一个 java 文件：" + files[i]);

				classConfig += moduleType.equals("provider") ? providerClass2Config(files[i], javaPath)
						: consumerClass2Config(files[i]);

			}
		}
		return classConfig;
	}

	/**
	 * 输出 dubbo xml 文件
	 * 
	 * @param sourceString
	 *            文件内容
	 * @param xmlFileName
	 *            文件名
	 */
	private static void outputXmlFile(String sourceString, String xmlFileName) throws IOException {
		byte[] sourceByte = sourceString.getBytes();

		File xmlFile = new File(xmlFileName);
		if (xmlFile.exists())
			xmlFile.delete();
		xmlFile.createNewFile();

		FileOutputStream outStream = new FileOutputStream(xmlFileName);
		outStream.write(sourceByte);
		outStream.close();
	}

	/**
	 * 生成 dubbo xml 内容
	 * 
	 * @return dubbo xml 内容
	 */
	private static String generateXmlSourceString() throws Exception {
		// String moduleJavaPath = projectPath + moduleDir + "/src/main/java/";
		String moduleJavaPath = projectPath + packagePath;
		File moduleJavaDir = new File(moduleJavaPath);
		if (!moduleJavaDir.exists())
			throw new Exception("illegal java module directory: " + moduleJavaPath);

		String xmlSourceString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
				+ "<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"
				+ "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:dubbo=\"http://code.alibabatech.com/schema/dubbo\"\n"
				+ "\txsi:schemaLocation=\"http://www.springframework.org/schema/beans\n"
				+ "        http://www.springframework.org/schema/beans/spring-beans.xsd\n"
				+ "        http://code.alibabatech.com/schema/dubbo\n"
				+ "        http://code.alibabatech.com/schema/dubbo/dubbo.xsd\n" + "        \">\n" + "\n"
				+ "\t<!-- 提供方应用信息，用于计算依赖关系 -->\n" + "\t<dubbo:application name=\"jfinal-duboo-demo-" + moduleType
				+ "\" />\n" + "\n" + "\t<!-- zookeeper注册中心 -->\n" + "\t<dubbo:registry protocol=\"" + registryProtocol
				+ "\" address=\"" + registryAddress + "\" />\n" + "\n" + "\t<!-- 用dubbo协议在20880端口暴露服务，注意本机IP要设置正确 -->\n"
				+ "\t<dubbo:protocol " + ((moduleType.equals("provider")) ? "name=\"" + protocolName + "\" " : "")
				+ "host=\"" + protocolHost + "\" "
				+ ((moduleType.equals("provider")) ? "port=\"" + protocolPort + "\" " : "") + "/>\n" + "\n"
				+ findClass(moduleJavaDir, moduleJavaPath) + "</beans>";

		return xmlSourceString;
	}

	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		// // 输出文件到对应 module resource 目录下
		projectPath = DubboXmlGenerator.class.getClassLoader().getResource("").getPath().replace("/target/classes/",
				"");
		String moduleResourcePath = projectPath + outPath;
		System.out.println(moduleResourcePath);
		outputXmlFile(generateXmlSourceString(), moduleResourcePath + moduleType + ".xml");
		long usedTime = (System.currentTimeMillis() - start) / 1000;
		System.out.println("Generate complete in " + usedTime + " seconds.");
		// // 输出文件的当前目录
		// outputXmlFile(generateXmlSourceString(), moduleType + ".xml");

	}
}
