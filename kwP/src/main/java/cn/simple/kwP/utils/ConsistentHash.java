package cn.simple.kwP.utils;

import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHash {
	/**
	 * 待添加入Hash环的服务器列表
	 */
	private static String[] servers = { };

	/**
	 * key表示服务器的hash值，value表示服务器的名称
	 */
	private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();

	/**
	 * 程序初始化，将所有的服务器放入sortedMap中
	 */
	static {
		for (int i = 0; i < servers.length; i++) {
			int hash = getHash(servers[i]);
			System.out.println("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
			sortedMap.put(hash, servers[i]);
		}
//		System.out.println();
	}

	/**
	 * 使用FNV1_32_HASH算法计算服务器的Hash值,这里不使用重写hashCode的方法，最终效果没区别
	 */
	public static int getHash(String str) {
		final int p = 16777619;
		int hash = (int) 2166136261L;
		for (int i = 0; i < str.length(); i++)
			hash = (hash ^ str.charAt(i)) * p;
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;

		// 如果算出来的值为负数则取其绝对值
		if (hash < 0)
			hash = Math.abs(hash);
		return hash;
	}

	/**
	 * 得到应当路由到的结点
	 */
	private static String getServer(String node) {
		// 得到带路由的结点的Hash值
		int hash = getHash(node);
		int x = hash % 64;
		// 得到大于该Hash值的所有Map
		SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);
		// 第一个Key就是顺时针过去离node最近的那个结点
		Integer i = subMap.firstKey();
		// 返回对应的服务器名称
		return subMap.get(i);
	}

	/**
	 * 得到应当路由到的结点
	 */
	public static String getTbName(String node, String tableName) {
		// 得到带路由的结点的Hash值
		int hash = getHash(node);
		int x = hash % 64 + 1;
//		if (x < 10) {
//			tableName = tableName + "_0" + x;
//		} else {
//			tableName = tableName + "_" + x;
//		}
		// 返回对应的服务器名称
		return tableName + "_" + x;
	}

	public static void main(String[] args) {
		System.out.println(getTbName("浙A9RE27", "ip_order_info"));
		// String[] nodes = { "127.0.0.1:1111", "221.226.0.1:2222",
		// "10.211.0.1:3333" };
		// for (int i = 0; i < nodes.length; i++)
		// System.out
		// .println("[" + nodes[i] + "]的hash值为" + getHash(nodes[i]) + ",
		// 被路由到结点[" + getServer(nodes[i]) + "]");

	}
}