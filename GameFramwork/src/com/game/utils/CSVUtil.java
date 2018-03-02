package com.game.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;
import com.game.cache.CacheManager;

/**
 * CSV解析成功的原则： 1、CSV名会有对应的实体java bean，如：copy_map.csv文件与SysCopyMap.java对应 2、CSV文件中的属性字段名与bean中属性字段按规则对应，如：monsterName与monsterName对应
 * 
 * @author kevin
 * 
 */
public class CSVUtil {

	private static Logger logger = LoggerFactory.getLogger(CSVUtil.class);

	public static void main(String[] args) throws Exception {
//		// loadCSVConfig();
//		// MissionBean<Integer, Dress> map =
//		// CacheManager.get(Dress.class.getSimpleName());
//		// System.out.println(map);
//		Map<Object, Object> map = parseCsv(System.getProperty("user.dir") + "/config/csv/Skill.csv", SkillBean.class);
//		for (Object string : map.keySet()) {
//			System.out.println(string + " " + map.get(string));
//		}
	}

	/**
	 * 获取CSV文件对应的Map<主键ID，对象>
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Map<Integer, T> getCsvMap(Class<T> clazz) {
		return CacheManager.get(clazz.getSimpleName());
	}

	/**
	 * 获取CSV文件对应的Map<主键ID，List<对象>>
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Map<Integer, List<T>> getCsvMapList(Class<T> clazz) {
		return CacheManager.get(clazz.getSimpleName());
	}

	/**
	 * 获取CSV文件对应的Java对象
	 * 
	 * @param clazz
	 * @param keys
	 * @return
	 */
	public static <T> T getCsvObject(Class<T> clazz, Object... keys) {

		return CacheManager.get(clazz.getSimpleName(), keys);
	}

	/**
	 * 获取CSV文件对应Map<key,List<T>>缓存中的的所有keys的List<T>集合
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Collection<T> getCsvObjects(Class<T> clazz, Object... keys) {

		return CacheManager.get(clazz.getSimpleName(), keys);
	}

	/**
	 * 获取CSV文件对应Map<key,T>缓存中的的所有values对象
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Collection<T> getCsvObjects(Class<T> clazz) {
		Map<Object, T> map = CacheManager.get(clazz.getSimpleName());
		if (map == null) {
			return null;
		}
		return map.values();
	}

	/**
	 * 加载系统昵称配置文件
	 * 
	 * @throws Exception
	 */
	// public static boolean loadNicknameConfig() throws Exception {
	// String filePath = System.getProperty("user.dir") + "/config/csv/name.csv";
	// InputStream in = new FileInputStream(filePath);
	// MissionBean<Integer, List<String>> nameMap = loadNicknameConfig(in);
	// CacheManager.put(CacheKey.SYSTEM_NAME, nameMap);
	// return true;
	// }

	/**
	 * 加载昵称配置文件
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	// public static MissionBean<Integer, List<String>> loadNicknameConfig(InputStream in) throws Exception {
	// CsvReader reader = null;
	// InputStreamReader streamReader = null;
	// MissionBean<Integer, List<String>> nameMap = new HashMap<Integer, List<String>>();
	// try {
	// streamReader = new InputStreamReader(in, Charset.forName("GBK"));
	// reader = new CsvReader(streamReader);
	// reader.readRecord();// 开始准备读数据
	// reader.skipRecord();// 跳过第一行字段说明数据
	// reader.skipRecord();// 跳过一行
	// while (reader.readRecord()) {
	// Integer type = Integer.valueOf(reader.get(1));// 写死第二列为名字类型
	// List<String> names = nameMap.get(type);
	// if (names == null) {
	// names = new ArrayList<>();
	// nameMap.put(type, names);
	// }
	// names.add(reader.get(2));
	// }
	// return nameMap;
	// } catch (Exception e) {
	// logger.error("加载CSV文件异常，请检查：name.csv对应的文件,记录行：" + reader.getCurrentRecord());
	// throw e;
	// } finally {
	// if (in != null) {
	// in.close();
	// }
	// if (streamReader != null) {
	// streamReader.close();
	// }
	// if (reader != null) {
	// reader.close();
	// }
	// }
	// }

	/**
	 * 加载CSV目录下所有配置资源
	 * 
	 * @return
	 * @throws Exception
	 */
	public static boolean loadCSVConfig(Package pack) throws Exception {
		String configPath = System.getProperty("user.dir") + "/config/csv";
		// loadNicknameConfig();
		// loadPayCsv();
		return loadCSVConfig(configPath, pack);
	}

	/**
	 * 加载支付列表
	 * 
	 * @return
	 * @throws Exception
	 */
	// private static boolean loadPayCsv() throws Exception {
	// String filePath = System.getProperty("user.dir") + "/config/csv/pay.csv";
	// InputStream in = new FileInputStream(filePath);
	// MissionBean<String, List<Pay>> map = loadPayCsv(in);
	// CacheManager.put(Pay.class.getSimpleName(), map);
	// return true;
	// }

	// public static MissionBean<String, List<Pay>> loadPayCsv(InputStream in) throws Exception {
	// MissionBean<String, List<Pay>> map = new HashMap<String, List<Pay>>();
	// MissionBean<Object, Object> beanMap = parseCSV(in, Pay.class);
	// for (Object object : beanMap.values()) {
	// Pay pay = (Pay) object;
	// List<Pay> pays = map.get(pay.getChannel());
	// if (pays == null) {
	// pays = new ArrayList<Pay>();
	// map.put(pay.getChannel(), pays);
	// }
	// pays.add(pay);
	// }
	// return map;
	// }

	/**
	 * 从配置目录下加载所有“.csv”文件，并放入Cache中，通过Test.class.getSimpleName()去取出缓存数据Map<索引key ,Test对象>
	 * 
	 * @param configPath
	 *            配置文件目录路径
	 * @return
	 * @throws Exception
	 */
	public static boolean loadCSVConfig(String configPath, Package pack) throws Exception {
		File configFile = new File(configPath);
		File[] listFiles = configFile.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			File file = listFiles[i];
			String name = file.getName();
			if (name.lastIndexOf(".csv") != -1) {
				Class<?> csvBeanClass = csvToClass(name, pack);
				if (csvBeanClass == null) {
					// 没有CSV文件对应的实体Bean
					continue;
				}
				Map<Object, Object> beanMap = parseCSV(new FileInputStream(file), csvBeanClass);
				if (beanMap != null) {
					// 成功加载CSV
					logger.info("Successfully loaded：" + name);
					CacheManager.put(csvBeanClass.getSimpleName(), beanMap);
				} else {
					// System.err.println("加载失败：" + name);
				}
			}
		}
		return true;
	}

	public static <T> Map<Object, Object> parseCsv(String path, Class<T> csvBeanClass) throws Exception {
		return parseCSV(new FileInputStream(new File(path)), csvBeanClass);
	}

	/**
	 * 解析单个CSV文件成对应的实体Bean key为CSV文件的第1列，若第1列有重复数据，则Value为Bean的list集合，否则为单个Bean对象
	 * 
	 * @param <T>
	 * @param in
	 * @param csvBeanClass
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<Object, Object> parseCSV(InputStream in, Class<T> csvBeanClass) throws Exception {
		CsvReader reader = null;
		Map<Object, Object> beanMap = null;
		try {
			reader = new CsvReader(in, Charset.forName("UTF-8"));
			reader.readRecord();// 开始准备读数据
			ArrayList<String> fields = new ArrayList<String>();
			beanMap = new HashMap<Object, Object>();
			int columnCount = reader.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				fields.add(reader.get(i));
			}
			reader.skipRecord();// 跳过第二行字段数据类型
			reader.skipRecord();// 跳过第三行字段中文解释
			boolean isCompositeId = false;// 是否为联合主键，默认CSV文件的第1列为Bean的唯一主键
			String id = fields.get(0);
			if (id.charAt(0) == 65279) {
				id = id.substring(1);
				fields.remove(0);
				fields.add(0, id);
			}
			String getIdMethod = "get" + firstCharToUpperCase(id);
			Map<T, Object> map = new HashMap<>();
			Map<String, Object> fieldValue = new HashMap<>();
			while (reader.readRecord()) {
				T csvBean = csvBeanClass.newInstance();
				for (int i = 0; i < columnCount; i++) {
					fieldValue.put(fields.get(i), new String(reader.get(i).getBytes(), "UTF-8"));
				}
				BeanUtil.populateProperty(csvBean, fieldValue);
				fieldValue.clear();
				// System.out.println(csvBean);
				Method method = csvBeanClass.getMethod(getIdMethod);
				Object idValue = method.invoke(csvBean);
				if (beanMap.containsKey(idValue)) {
					// 表中id为联合主键
					isCompositeId = true;
				} else {
					beanMap.put(idValue, null);// 先初始KEY
				}
				map.put(csvBean, idValue);
			}
			// 导入beanMap中
			Iterator<Entry<T, Object>> iterator = map.entrySet().iterator();
			if (isCompositeId) {
				while (iterator.hasNext()) {
					Map.Entry<T, Object> entry = (Map.Entry<T, Object>) iterator.next();
					List<T> ts = (List<T>) beanMap.get(entry.getValue());
					if (ts == null) {
						ts = new ArrayList<>();
						beanMap.put(entry.getValue(), ts);
					}
					ts.add(entry.getKey());
				}
				map = null;
			} else {
				while (iterator.hasNext()) {
					Map.Entry<T, Object> entry = (Map.Entry<T, Object>) iterator.next();
					beanMap.put(entry.getValue(), entry.getKey());
				}
			}
		} catch (Exception e) {
			logger.error("加载CSV文件异常，请检查：" + csvBeanClass.getName() + "对应的文件,记录行：" + reader.getCurrentRecord());
			throw e;
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (in != null) {
				in.close();
			}
		}
		return beanMap;
	}

	/**
	 * 将有规则的名称转换为java规范的驼峰名称，如get_your_name转为getYourName
	 * 
	 * @param name
	 * @return java规范属性名称
	 */
	private static String getJavaRuleName(String name) {
		String[] temp = name.split("_");
		name = temp[0];
		if (temp.length > 1) {
			for (int i = 1; i < temp.length; i++) {
				name += temp[i].substring(0, 1).toUpperCase() + temp[i].substring(1);
			}
		}
		return name;
	}

	/**
	 * 查找CSV文件对应的Class
	 * 
	 * @param fileName
	 * @return
	 */
	public static Class<?> csvToClass(String fileName, Package pack) {
		fileName = fileName.substring(0, fileName.length() - ".cvs".length());
		
		// 获取包的名字 并进行替换
		String packageName = pack.getName();
		String packageDirName = packageName.replace('.', '/');
				
		// 转换为CSV文件对应的Bean class 包名称
		String className = packageDirName + "." + firstCharToUpperCase(getJavaRuleName(fileName)) + "Csv";
		// className = className.substring(0, className.lastIndexOf("."));
		Class<?> csvBeanClass;
		try {
			csvBeanClass = Class.forName(className);
		} catch (Exception e) {
			return null;
		}
		return csvBeanClass;
	}

	/**
	 * 首字母大写
	 * 
	 * @param name
	 * @return
	 */
	public static String firstCharToUpperCase(String name) {
		// System.out.println((int)(name.substring(0, 1).charAt(0)));
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	/**
	 * 重新加载CSV流文件
	 * 
	 * @param fileName
	 * @param in
	 * @return
	 * @throws Exception
	 */
	public static boolean reloadFile(String fileName, InputStream in, Package pack) throws Exception {
		if (fileName.equals("name.csv")) {
			// MissionBean<Integer, List<String>> nameMap = loadNicknameConfig(in);
			// CacheManager.put(CacheKey.SYSTEM_NAME, nameMap);
			return true;
		} else if (fileName.equals("pay.csv")) {
			// MissionBean<String, List<Pay>> map = loadPayCsv(in);
			// CacheManager.put(Pay.class.getSimpleName(), map);
			return true;
		}
		Class<?> csvBeanClass = csvToClass(fileName, pack);
		if (csvBeanClass == null) {
			// 没有CSV文件对应的实体Bean
			return false;
		}
		Map<Object, Object> beanMap = parseCSV(in, csvBeanClass);
		if (beanMap != null) {
			// 成功加载CSV
			logger.info("Successfully reloaded：" + fileName);
			CacheManager.put(csvBeanClass.getSimpleName(), beanMap);
		}
		return true;
	}

	/**
	 * 删除缓存中单个Bean类型所有对象
	 * 
	 * @param clazz
	 */
	public static <T> void removeBeanMap(Class<T> clazz) {
		CacheManager.remove(clazz.getSimpleName());
	}
}
