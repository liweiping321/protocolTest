package com.game.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * 日志工具
 * 
 * @author jason.lin
 *
 */
public class LogUtils {
	private static final String thisClassName = LogUtils.class.getName();
	private static final String msgSep = " ";// \r\n
	private static final Logger actLogger = Logger.getLogger("ACT_LOG");
	private static Logger logger = null;

	static {
		logger = Logger.getLogger("");
	}

	private static Object getStackMsg(Object msg) {
		if("".length() == 0){
			return msg;
		}
		
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		if (ste == null) {
			return "";
		}

		boolean srcFlag = false;
		for (int i = 0; i < ste.length; i++) {
			StackTraceElement s = ste[i];
			// 如果上一行堆栈代码是本类的堆栈，则该行代码则为源代码的最原始堆栈。
			if (srcFlag) {
				return s == null ? "" : toString(s) + msgSep + msg;
			}
			// 定位本类的堆栈
			if (thisClassName.equals(s.getClassName())) {
				srcFlag = true;
				i++;
			}
		}
		return "";
	}

	private static String toString(StackTraceElement element) {
		return element.getClassName() + "." + element.getMethodName()
				+ (element.isNativeMethod() ? "(Native Method)"
						: (element.getFileName() != null && element.getLineNumber() >= 0 ? "(" + element.getFileName() + ":" + element.getLineNumber() + ")"
								: (element.getFileName() != null ? "(" + element.getFileName() + ")" : "(Unknown Source)")));
	}

	public static void debug(Object msg) {
		Object message = getStackMsg(msg);
		logger.debug(message);
	}

	public static void debug(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.debug(message, t);
	}

	public static void info(Object msg) {
		Object message = getStackMsg(msg);
		logger.info(message);
	}

	public static void info(Object msg, Throwable t) {
		logger.info(t.getMessage(), t);
	}

	public static void warn(Object msg) {
		Object message = getStackMsg(msg);
		logger.warn(message);
	}

	public static void warn(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.warn(message, t);
	}

	public static void error(Object msg) {
		Object message = getStackMsg(msg);
		logger.error(message);
	}

	public static void error(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		if (logger != null) {
			logger.error(message, t);
		}
	}

	public static void fatal(Object msg) {
		Object message = getStackMsg(msg);
		logger.fatal(message);
	}

	public static void fatal(Object msg, Throwable t) {
		Object message = getStackMsg(msg);
		logger.fatal(message, t);
	}

	/**
	 * 360 日志推送
	 * 
	 * @param msg
	 */
	public static void act(String msg) {
		try {
			actLogger.trace(msg);
		} catch (Exception e) {
			LogUtils.error("360 act log error,msg content is :" + msg);
		}
	}

	/**
	 * 初始化log配置
	 * @return
	 */
	public static boolean logConfigure() {
		try {
			PropertyConfigurator.configure("config/log4j.properties");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
