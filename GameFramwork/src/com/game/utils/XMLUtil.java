package com.game.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

import com.alibaba.fastjson.JSONObject;
import com.game.type.XmlObjectKey;
import com.game.type.XmlObjectSubKey;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class XMLUtil {

	/**
	 * 统一获得xml流文件
	 * 
	 * @param name
	 * @return
	 * @throws FileNotFoundException
	 */
	public static InputStream getXMLInputStream(String name) throws FileNotFoundException {
		return new FileInputStream("cjwz_config/game_xml/" + name + ".xml");
	}

	/**
	 * @param filterMap
	 * @param in
	 * @Added by
	 */
	public static void readNameFilterWords(List<String> filterMap, InputStream in) throws Exception {

	}

	/**
	 * 将xml解析成对象列表
	 * 
	 * @param in
	 * @param head
	 * @param class
	 * @return
	 * @throws DocumentException
	 */
	public static <T> List<T> fromXMLToObjectList(InputStream in, String head, Class<T> T) {
		try {
			List<T> result = new ArrayList<T>();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);

			List<Element> list = document.selectNodes(head);
			Iterator<Element> iter = list.iterator();
			while (iter.hasNext()) {
				Element element = iter.next();
				T t = (T) fromElementToObject(element, T);// 将element转换成对象
				result.add(t);
			}
			return result;
		} catch (Exception e) {

			LogUtils.error("fromXMLToObjectList:" + T.getName(), e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 将xml解析成对象列表
	 * 
	 * @param in
	 * @param head
	 * @param class
	 * @return
	 * @throws DocumentException
	 */
	public static <T> Map<Integer, T> fromXMLToIntKeyMap(InputStream in, String head, Class<T> T) {
		try {
			Map<Integer, T> result = new HashMap<Integer, T>();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);

			// 获取注解key
			String key = getXmlObjKey(T, XmlObjectKey.class);
			if (key == null) {
				LogUtils.error("no define key! for " + T.getName());
				return null;
			}

			List<Element> list = document.selectNodes(head);
			Iterator<Element> iter = list.iterator();
			while (iter.hasNext()) {
				Element element = iter.next();

				T t = (T) fromElementToObject(element, T);// 将element转换成对象

				int id = Integer.parseInt(element.attributeValue(key));
				result.put(id, t);
			}
			return result;
		} catch (Exception e) {

			LogUtils.error("fromXMLToObjectList:" + T.getName(), e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 将xml解析成对象列表
	 * 
	 * @param in
	 * @param head
	 * @param class
	 * @return
	 * @throws DocumentException
	 */
	public static <T> Map<String, T> fromXMLToStringKeyMap(InputStream in, String head, Class<T> T) {
		try {
			Map<String, T> result = new HashMap<String, T>();
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);

			// 获取注解key
			String key = getXmlObjKey(T, XmlObjectKey.class);
			if (key == null) {
				LogUtils.error("no define key! for " + T.getName());
				return null;
			}

			List<Element> list = document.selectNodes(head);
			Iterator<Element> iter = list.iterator();
			while (iter.hasNext()) {
				Element element = iter.next();

				T t = (T) fromElementToObject(element, T);// 将element转换成对象
				String id = element.attributeValue(key);
				result.put(id, t);
			}
			return result;
		} catch (Exception e) {

			LogUtils.error("fromXMLToObjectList:" + T.getName(), e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 将xml解析成对象列表
	 * 
	 * @param in
	 * @param head
	 * @param class
	 * @return
	 * @throws DocumentException
	 */
	public static <T> Map<Integer, Map<Integer, T>> fromXMLToObjectMulMap(InputStream in, String head, Class<T> T)
			throws Exception {
		SAXReader saxReader = null;
		try {
			Map<Integer, Map<Integer, T>> result = new HashMap<Integer, Map<Integer, T>>();
			saxReader = new SAXReader();
			Document document = saxReader.read(in);

			// 获取注解key
			String key = getXmlObjKey(T, XmlObjectKey.class);
			if (key == null) {
				LogUtils.error("no define key! for " + T.getName());
				return null;
			}

			// 获取注解key
			String subKey = getXmlObjKey(T, XmlObjectSubKey.class);

			List<Element> list = document.selectNodes(head);
			Iterator<Element> iter = list.iterator();
			while (iter.hasNext()) {
				Element element = iter.next();
				T t = (T) fromElementToObject(element, T);// 将element转换成对象
				int id = Integer.parseInt(element.attributeValue(key));
				int subId = 0;
				if (subKey != null) {
					subId = Integer.parseInt(element.attributeValue(subKey));
				}

				Map<Integer, T> subMap = result.get(id);
				if (subMap == null) {
					subMap = new HashMap<Integer, T>();
					result.put(id, subMap);
				}
				subMap.put(subId, t);
			}

			return result;
		} catch (Exception e) {

			LogUtils.error("fromXMLToObjectMap:" + T.getName(), e);

		} finally {
			if (in != null) {
				in.close();
			}
		}
		return null;
	}

	/**
	 * 获取对key值
	 * 
	 * @param O
	 * @param T
	 * @return
	 */
	public static <O, T> String getXmlObjKey(Class<O> O, Class<? extends Annotation> T) {
		Field[] fields = O.getDeclaredFields();
		for (Field field : fields) {
			// 获取字段中包含fieldMeta的注解
			T meta = (T) field.getAnnotation(T);
			if (meta != null) {
				return field.getName();
			}
		}

		return null;
	}

	/**
	 * 将xml解析成Map对象
	 * 
	 * @param in
	 * @param head
	 * @param class
	 * @return
	 * @throws DocumentException
	 */
	public static Map<String, Map> fromXMLToMap(InputStream in, String head) throws Exception {
		Map<String, Map> result = new HashMap<String, Map>();
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(in);
		List<Element> list = document.selectNodes(head);
		Iterator<Element> iter = list.iterator();
		while (iter.hasNext()) {
			Element element = iter.next();
			Map map = fromElementToJsonToMap(element);// 将element转换成Map
			String id = element.attributeValue("id");
			result.put(id, map);
		}
		return result;
	}

	/**
	 * 将element转换成对象
	 * 
	 * @param element
	 * @return
	 */
	public static <T> T fromElementToObject(Element element, Class<T> t) {
		String json = fromElementToJson(element);// 将element转换成json格式
		T result = (T) JSONObject.parseObject(json, t);// 将json转换成对象
		return result;
	}

	/**
	 * 将element转换成json
	 * 
	 * @param element
	 * @return
	 */
	public static String fromElementToJson(Element element) {
		Iterator<DefaultAttribute> iter = element.attributeIterator();
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		while (iter.hasNext()) {
			DefaultAttribute attribute = iter.next();
			String name = attribute.getName();
			String value = attribute.getStringValue();
			sb.append("\"").append(name).append("\"").append(":").append("\"").append(value).append("\"").append(",");
		}
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 将element转换成Map
	 * 
	 * @param element
	 * @return
	 */
	public static Map fromElementToJsonToMap(Element element) {
		List<DefaultElement> list = element.content();
		Map reMap = new HashMap();
		Map map = null;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof DefaultElement) {
				map = new HashMap();
				Iterator<DefaultAttribute> it = list.get(i).attributeIterator();
				while (it.hasNext()) {
					DefaultAttribute attribute = it.next();
					String name1 = attribute.getName();
					String value = attribute.getStringValue();
					map.put(name1, value);
				}
				reMap.put(map.get("id"), map);
			}
		}
		return reMap;
	}

	/**
	 * 将xml转换成对象
	 * 
	 * @param in
	 * @param head
	 * @param T
	 * @return
	 * @throws DocumentException
	 */
	public static <T> T fromXMLToObject(InputStream in, String head, Class<T> T) throws Exception {
		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(in);

		List<Element> list = document.selectNodes(head);
		return fromElementToObject(list.get(0), T);
	}

	public static int readAndSetInt(Element e, String name, int defaultValue) {
		String value = e.attributeValue(name);
		if (value == null || value.trim().equals("")) {
			return 0;
		} else {
			return Integer.parseInt(value);
		}
	}

	public static long readAndSetLong(Element e, String name, long defaultValue) {
		String value = e.attributeValue(name);
		if (value == null || value.trim().equals("")) {
			return 0;
		} else {
			return Long.parseLong(value);
		}
	}

	public static int[] readIntArray(Element e, String name, String splitRegx) {
		String value = e.attributeValue(name);
		if (value == null || value.trim().equals("")) {
			return null;
		}
		String[] paramsArr = value.split(splitRegx);
		int[] params = new int[paramsArr.length];
		for (int i = 0; i < paramsArr.length; i++) {
			params[i] = Integer.parseInt(paramsArr[i]);
		}
		return params;
	}

	/**
	 * 解析复杂结构的xml
	 * 
	 * @param T
	 * @param filePath
	 * @param head
	 * @return
	 */
	public static <T> List<T> fromMulXmlToList(Class<T> T, String filePath, String head) {
		InputStream in = null;
		try {
			in = new FileInputStream(new File(filePath));
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);

			List<T> result = new ArrayList<T>();
			List<Element> list = document.selectNodes(head + "/" + T.getSimpleName());
			for (Element element : list) {
				T t = T.newInstance();
				fromMulElementToObj(t, element);
				result.add(t);
			}
			return result;
		} catch (Exception e) {

			e.printStackTrace();
			LogUtils.error("fromMulXmlToObj:" + T.getName(), e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}

	/**
	 * 将复杂element转换成列表
	 * 
	 * @param T
	 * @param root
	 * @return
	 */
	public static <T> List<T> fromMulElementToList(Class<T> T, Element root) {
		try {
			List<T> result = new ArrayList<T>();
			Iterator<DefaultElement> eIterator = root.elementIterator();
			while (eIterator.hasNext()) {
				DefaultElement element = eIterator.next();
				T t = T.newInstance();
				fromMulElementToObj(t, element);
				result.add(t);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.error("fromMulElementToList:" + T.getName(), e);
		}

		return null;
	}

	/**
	 * 解析xml元素成对象
	 * 
	 * @param obj
	 * @param element
	 * @throws Exception
	 */
	private static <T> void fromMulElementToObj(T obj, Element element) throws Exception {
		initMulObjAttributes(obj, element);

		// 解析二级列表
		Iterator<DefaultElement> eIterator = element.elementIterator();
		while (eIterator.hasNext()) {
			T subObj = (T) obj.getClass().newInstance();
			fromMulElementToObj(subObj, eIterator.next());

			// 加入到列表中
			Method method = obj.getClass().getMethod("getList");
			List<T> list = (List<T>) method.invoke(obj);
			if (list == null) {
				list = new ArrayList<>();
				method = obj.getClass().getMethod("setList", List.class);
				method.invoke(obj, list);
			}
			list.add(subObj);
		}
	}

	/**
	 * 初始化属性
	 * 
	 * @param obj
	 * @param element
	 * @throws Exception
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	private static <T> void initMulObjAttributes(T obj, Element element)
			throws Exception, IllegalArgumentException, InvocationTargetException {
		Iterator<DefaultAttribute> aIterator = element.attributeIterator();
		while (aIterator.hasNext()) {
			DefaultAttribute subElement = aIterator.next();
			String name = subElement.getName();
			String value = subElement.getStringValue();
			String methodStr = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
			Method method = obj.getClass().getMethod(methodStr, String.class);
			method.invoke(obj, value);
		}
	}

	/**
	 * 获取xml中的节点Map
	 * 
	 * @param path
	 * @param nodeName
	 * @param keyName
	 * @return
	 */
	public static Map<String, Element> getXmlElementMap(String path, String nodeName, String keyName) {
		List<Element> list = getXmlElementList(path, nodeName);
		if (list == null) {
			return null;
		}

		Map<String, Element> map = new HashMap<>();
		for (Element element : list) {
			String key = element.attributeValue(keyName);
			map.put(key, element);
		}
		return map;
	}

	/**
	 * 获取xml中的节点列表
	 * 
	 * @param path
	 * @param nodeName
	 * @return
	 */
	public static List<Element> getXmlElementList(String path, String nodeName) {
		InputStream in = null;
		try {

			in = new FileInputStream(new File(path));
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(in);
			return document.selectNodes(nodeName);

		} catch (Exception e) {

			e.printStackTrace();
			LogUtils.error("getXmlElementList:" + path, e);

		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return null;
	}

}