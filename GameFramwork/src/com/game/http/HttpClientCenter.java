package com.game.http;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.game.consts.AppConfig;
import com.game.http.entity.HttpRequestMsg;
import com.game.utils.Md5Tool;

/**
 * 用户中心HTTP请求工具
 * 
 * @author kevin
 *
 */
public class HttpClientCenter extends HttpClient {

	/************* 用户中心API *************/
	/**
	 * 提升本机端口对应所有区服的版本号
	 */
	public static final String PROMOTE_AREA_VERSION = "/game/promoteAreaVersion";
	/**
	 * 领取激活码奖励
	 */
	public static final String VALIDATE_CDKEY = "/game/cdkey/award/";
	/**
	 * 获取本机发货未完成的订单
	 */
	public static final String UNFINISH_PAY = "/game/unfinishPay";
	/**
	 * 删除已处理完的订单
	 */
	public static final String DELETE_UNFINISH_PAY = "/game/delUnfinishPay";

	/**
	 * 向用户中心发送请求
	 * 
	 * @param url
	 *            请求URL，相对路径
	 * @param jsonParam
	 *            请求参数的JSON数据
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static String postRequest(String url, String jsonParam) throws Exception {
		return postRequest(url, JSONObject.parseObject(jsonParam, Map.class));
	}

	/**
	 * 向用户中心发送请求
	 * 
	 * @param url
	 *            请求URL，相对路径
	 * @param paraMap
	 *            请求参数
	 * @return
	 * @throws Exception
	 */
	public static String postRequest(String url, Map<String, Object> paraMap) throws Exception {
		if (paraMap == null) {
			paraMap = new HashMap<String, Object>();
		}
		String time = System.currentTimeMillis() + "";
		String token_key = time + "&" + AppConfig.centerPublicKey;
		token_key = Md5Tool.encrypt(token_key);
		paraMap.put("timestamp", time);
		paraMap.put("token_key", token_key);
		List<NameValuePair> paras = new ArrayList<NameValuePair>();
		for (String name : paraMap.keySet()) {
			paras.add(new BasicNameValuePair(name, paraMap.get(name).toString()));
		}
		url = AppConfig.centerUrl + url;
		return post(url, paras);
	}
	public static void main(String[] args)throws Exception {
		get("http://localhost:8080/tc/api/listenUrl?param=%7B%22body%22%3A%22%E6%88%91%E6%98%AF%E8%B0%81%22%2C%22sessionId%22%3A111%7D");
		
		System.out.println(URLEncoder.encode(JSON.toJSONString(new HttpRequestMsg<String>("111","我是谁")),"utf8"));
		System.out.println(URLDecoder.decode("%7B%22body%22%3A%22%E6%88%91%E6%98%AF%E8%B0%81%22%2C%22sessionId%22%3A111%7D", "utf8"));
	}
}
