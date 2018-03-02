/**
 * Date: Mar 15, 2013
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */

package com.game.utils;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.game.consts.AppConfig;
import com.game.type.Configuration;
import com.game.utils.common.MethodPara;

/**
 * property配置工具类
 * 
 * @author jason.lin
 */
public class PropertyConfigUtil {

	/**
	 * 初始化所有配置
	 * @param pack
	 */
	public static void init(Package pack) {
		Set<Class<?>> classSet = ClassUtil.getClasses(pack);
		if (DataUtil.isEmpty(classSet)) {
			LogUtils.error("Attention !!! no config class: " + pack.toString());
			return;
		}

		// 遍历并初始化配置
		for (Class<?> clazz : classSet) {
			try {
				// 过滤非配置类
				Configuration config = clazz.getAnnotation(Configuration.class);
				if (config == null) {
					continue;
				}

				// 初始化配置
				String path = config.config();

				// 初始化配置
				config(path, clazz);
			} catch (Exception e) {
				LogUtils.error(clazz.getName(), e);
			}

		}
	}

	/**
	 * 初始化单个配置
	 * 
	 * @param path
	 * @param obj
	 */
	private static void config(String path, Class<?> clazz) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			Properties properties = new Properties();
			properties.load(new FileInputStream(path));
			
			// 实例化对象
			Object obj = clazz.newInstance();
			
			// 方法所有支持的参数
			@SuppressWarnings("serial")
			List<MethodPara> paraList = new ArrayList<MethodPara>(){
				{
					add(new MethodPara(int.class));
					add(new MethodPara(String.class));
					add(new MethodPara(boolean.class));
				}
			};

			// 得到所有配置
			@SuppressWarnings("rawtypes")
			Enumeration en = properties.propertyNames();
			while(en.hasMoreElements()){
				String key = ((String) en.nextElement()).trim();
				String value = properties.getProperty(key).trim();
				
				String methodStr = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
				
				for(MethodPara methodPara : paraList){
					try {
						Method method = clazz.getDeclaredMethod(methodStr, methodPara.type);
						
						if(method == null){
							continue;
						}
						method.invoke(obj, methodPara.getValue(value));
						break;
					} catch (Exception e) {
					}
				}
			}
		} catch (Exception e) {
			LogUtils.error(path, e);
		}finally {
			if(fis != null){
				try {
					fis.close();
				} catch (Exception e) {
					LogUtils.error(path, e);
				}
			}
		}
	}

	public static void main(String[] args) {
		init(AppConfig.class.getPackage());
		System.err.println(AppConfig.socketPort);
		System.err.println(AppConfig.webPort);
	}
}
