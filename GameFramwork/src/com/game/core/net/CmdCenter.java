package com.game.core.net;

import java.lang.instrument.IllegalClassFormatException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.game.utils.ClassUtil;
import com.game.utils.DataUtil;
import com.game.utils.LogUtils;


/**
 * 指令中心
 * @author jason.lin
 * 
 */
public class CmdCenter {

	/*** 处理请求handler：key为协议编号 ***/
	public static Map<Short, Cmd> cmdMap = new HashMap<Short, Cmd>();

	/**
	 * 注册指令
	 * @param cmdPack
	 * @throws Exception
	 */
	public static void registCmd(Package cmdPack) throws Exception {
		Set<Class<?>> classSet = ClassUtil.getClasses(cmdPack);
		if(DataUtil.isEmpty(classSet)){
			LogUtils.error("Attention !!! no cmd path: " + cmdPack.toString());
			return;
		}
		
		for(Class<?> clazz : classSet){
			// 过滤非cmd
			if(!Cmd.class.isAssignableFrom(clazz)){
				continue;
			}
			String name = clazz.getSimpleName();
			
			if (name.indexOf("_") == -1) {
				LogUtils.warn("bad format:" + name);
				continue;
			}
			
			Cmd cmd = (Cmd)clazz.newInstance();
			if (cmdMap.put(Short.parseShort(name.substring(name.lastIndexOf("_") + 1)), cmd) != null) {
				throw new IllegalClassFormatException("协议号已经被使用：" + name);
			}
		}
	}
}
