/**
 * 
 */
package com.game.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.util.TypeUtils;
import com.game.ts.net.bean.InfoBean;
import com.game.ts.net.bean.TSBean;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lip.li
 * @date 2017-1-19
 */
public class InfoBeanUtils {

	public static InfoBean json2InfoBeans(TSBean bean) {
		InfoBean result = new InfoBean();
		List<InfoBean> infoBeans = new ArrayList<InfoBean>();
		// JSONObject jsonObject=JSON.parseObject(bean.getContent());
		JSONObject jsonObject = bean.getContent();
		parseJsonObject(infoBeans, jsonObject);

		result.setType(InfoBean.TypeObject);
		result.setMode("content");
		result.setChild(infoBeans);
		result.setEditable(false);

		setInfoBeansEditable(infoBeans, bean.getDirectType());

		return result;
	}

	/**
	 * 设置是否可编辑
	 */
	private static void setInfoBeansEditable(List<InfoBean> infoBeans, String directType) {
		for (InfoBean infoBean : infoBeans) {

			infoBean.setEditable(false);

			if (!infoBean.getMode().equals("src") &&infoBean.getType().contains(InfoBean.TypeBase)) {
				infoBean.setEditable(true);
			}

			if (CollectionUtils.isNotEmpty(infoBean.getChild())) {
				setInfoBeansEditable(infoBean.getChild(), directType);
			}
		}

	}

	public static InfoBean json2InfoBeans(String jsonString) {
		InfoBean result = new InfoBean();
		List<InfoBean> infoBeans = new ArrayList<InfoBean>();
		JSONObject jsonObject = JSON.parseObject(jsonString);
		parseJsonObject(infoBeans, jsonObject);

		result.setType(InfoBean.TypeObject);
		result.setMode("content");
		result.setChild(infoBeans);
		return result;
	}

	private static void parseJsonObject(List<InfoBean> infoBeans, JSONObject jsonObject) {
		Set<String> names = jsonObject.keySet();
		for (String name : names) {
			Object value = jsonObject.get(name);

			InfoBean infoBean = new InfoBean(name, value, "");

			if (value instanceof JSONObject) {
				parseJsonObject(infoBean.getChild(), (JSONObject) value);
				infoBean.setType(InfoBean.TypeObject);
			} else if (value instanceof JSONArray) {
				JSONArray arrayValues = (JSONArray) value;
				int size = arrayValues.size();
				if (size > 0) {
					Object arrayItem1 = arrayValues.get(0);
					if (arrayItem1 instanceof JSONObject) {
						for (int i = 0; i < size; i++) {
							JSONObject itemJSONObject = arrayValues.getJSONObject(i);
							InfoBean arrayItem = new InfoBean("[" + i + "]", "");
							arrayItem.setType(InfoBean.TypeObject);

							infoBean.getChild().add(arrayItem);
							parseJsonObject(arrayItem.getChild(), itemJSONObject);
						}
						infoBean.setType(InfoBean.TypeArrayObject);
					} else {
						infoBean.setType(InfoBean.TypeArrayBase);
					}
				} else if (size == 0) {
					infoBean.setType(InfoBean.TypeArrayBase);
				}
			}
			if (infoBean.getMode().equals("head")) {
				infoBeans.add(0, infoBean);
			} else {
				infoBeans.add(infoBean);
			}

		}
	}

	public static JSONObject beans2JsonString(InfoBean infoBean) {
		return beans2JsonMap(infoBean.getChild());
	}

	/**
	 * @return
	 */
	private static JSONObject beans2JsonMap(List<InfoBean> infoBeans) {
		JSONObject jsonObject = new JSONObject();
		// Map<String,Object> jsonMap=new HashMap<>();

		for (InfoBean infoBean : infoBeans) {
			if (infoBean.getType().equals(InfoBean.TypeBase)) {

				jsonObject.put(infoBean.getMode(),
						TypeUtils.castToJavaBean(infoBean.getValue(), infoBean.getValueType()));

			} else if (infoBean.getType().equals(InfoBean.TypeObject)) {
				JSONObject objectJsonMap = beans2JsonMap(infoBean.getChild());
				jsonObject.put(infoBean.getMode(), objectJsonMap);
			} else if (infoBean.getType().equals(InfoBean.TypeArrayObject)) {
				List<InfoBean> infoBeans2 = infoBean.getChild();
				if (!CollectionUtils.isEmpty(infoBeans2)) {
					List<JSONObject> list = new ArrayList<>();
					for (InfoBean bean : infoBeans2) {
						list.add(beans2JsonMap(bean.getChild()));
					}
					jsonObject.put(infoBean.getMode(), JSON.toJSON(list));
				}
			} else if (infoBean.getType().equals(InfoBean.TypeArrayBase)) {
				jsonObject.put(infoBean.getMode(), JSON.parseArray(infoBean.getValue()));
			}
		}
		return jsonObject;
	}

	public static Class<?> getValueType(Object value) {
		Class<?> clazz = value.getClass();
		if (clazz == byte.class || clazz == Byte.class) {
			return Long.class;
		}

		if (clazz == short.class || clazz == Short.class) {
			return Long.class;
		}

		if (clazz == int.class || clazz == Integer.class) {
			return Long.class;
		}

		if (clazz == long.class || clazz == Long.class) {
			return Long.class;
		}
		if (clazz == JSONArray.class) {
			JSONArray jsonArray = (JSONArray) value;
			if (jsonArray.size() > 0) {
				return clazz = getValueType(jsonArray.get(0));
			}else{
				return Object.class;
			}

		}
		return clazz;
	}

	public static void main(String[] args) {
		TSBean tsBean = new TSBean();
		tsBean.setId(1);
		tsBean.setTscCmd(0);
		tsBean.setLength(1000);
		tsBean.setCode("0x01");
		tsBean.setSubCode("0x00");
		tsBean.setResult(true);
		tsBean.setDirectType("up");
		tsBean.setContent(JSONObject.parseObject(
				"{\"isChangeChannel\":true,\"other\":0,\"src\":\"Eg=\",\"version\":[\"dsf\", \"dsfsdf\"], \"wwwww\":{\"aaaa\":\"bbbb\", \"cc\":\"dd\"}}"));
		tsBean.setIp("IP");
		tsBean.setUserName("username");
		tsBean.setSessionId("1111111111");

		// InfoBean
		// infoBean=InfoBeanUtils.json2InfoBeans(JSON.toJSONString(tsBean));
		// String path="ip";
		// String[] keys=StringUtils.split(path, "\\.");
		//
		// Class<?> valueType=infoBean.getValueType(keys, 0);
		//
		// Object rootObject=JSON.toJSON(tsBean);
		// ExpressEngine.set(rootObject,
		// path,TypeUtils.castToJavaBean("falseee", valueType));
		// System.out.println(JSON.toJSONString(rootObject));
		JSONPath.contains(JSON.toJSON(new Object()), "");
	}
}
