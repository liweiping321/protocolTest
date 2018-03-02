package com.game.utils.language;

import com.game.utils.LogUtils;

import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LanguageMgr
{

	private static Hashtable<String, String> allMsgs = new Hashtable<String, String>();

	private static ReadWriteLock locker = new ReentrantReadWriteLock(false);

	// /**
	//  * 初始化所有提示信息
	//  *
	//  * @param path
	//  *            提示信息配置文件路径
	//  * @return 执行结果
	//  */
	// public static boolean setup(String path) {
	// 	return reload(path);
	// }

	static {
		// reload("../Language-zh_cn.txt");
		reload("config/Language-zh_cn.txt");
	}

	/**
	 * 重新加载提示信息
	 * 
	 * @param path
	 *            提示信息配置文件路径
	 * @return 执行结果
	 */
	public static boolean reload(String path) {
		try {
			Hashtable<String, String> temp = loadLanguage(path);

			if (temp.size() > 0) {
				locker.writeLock().lock();
				allMsgs = temp;
				locker.writeLock().unlock();
				return true;
			}
		} catch (Exception ex) {
			LogUtils.error("Load language file error:", ex);
		}
		return false;
	}

	/**
	 * 载人提示信息内容
	 * 
	 * @param path
	 *            文件路径
	 * @return 执行结果
	 */
	private static Hashtable<String, String> loadLanguage(String path) {
		Hashtable<String, String> list = new Hashtable<String, String>();

		try {
			File file = new File(path);
			if (!file.exists()) {
				LogUtils.error("Language file : " + path + " not found !");
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				String line = null;

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("#"))
						continue;

					if (line.indexOf(':') == -1)
						continue;

					// 不能用split分隔，字段中可能有两个 ":"
					String key = line.substring(0, line.indexOf(':'));
					String value = line.substring(line.indexOf(':') + 1, line.length());
					list.put(key, value);
				}

				reader.close();
			}
		} catch (Exception ex) {
			LogUtils.error("fail to load language file:", ex);
		}

		return list;
	}

	/**
	 * 获取指定提示信息的内容
	 * 
	 * @param translateId
	 *            索引id
	 * @param args
	 *            内容参数
	 * @return 提示信息，支持格式化
	 */
	public static String getTranslation(String translateId, Object... args) {
		if (allMsgs.containsKey(translateId)) {

			String translated = allMsgs.get(translateId);

			try {
				// log.error("getTranslation translated:"+translated);
				translated = CSharpFormat.format(translated, args);
			} catch (Exception ex) {
				// 这个异常别注释掉
				LogUtils.error(Thread.currentThread().getStackTrace()[1].getMethodName(), ex);
			}
			return translated == null ? translateId : translated;
		} else {
			return translateId;
		}
	}

	/**
	 * 获取字符串的长度 unicode编码在0~255之间算长度1，其他范围算长度2
	 * 
	 * @param str
	 *            输入字符串
	 * @return 字符串实际长度
	 */
	public static int getStringLength(String str) {
		int length = 0;
		for (int i = 0; i < str.length(); ++i) {
			int code = str.charAt(i);
			if (code >= 0 && code <= 255)
				++length;
			else
				length += 2;
		}
		return length;
	}
}
