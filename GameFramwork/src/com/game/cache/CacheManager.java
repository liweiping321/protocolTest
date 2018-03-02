package com.game.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.game.consts.CacheKey;
import com.game.player.obj.BaseOnlinePlayer;

public class CacheManager {

	private static Logger logger = LoggerFactory.getLogger(CacheManager.class);
	// 缓存对象
	private static ConcurrentHashMap<String, Object> cache = new ConcurrentHashMap<String, Object>();
	// 玩家在线信息
	private static Map<Integer, BaseOnlinePlayer> playerInfoMap = new ConcurrentHashMap<>();
	
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) cache.get(key);
	}

	public static void put(String key, Object value) {
		cache.put(key, value);
	}

	public static void remove(String key) {
		cache.remove(key);
	}

	/**
	 * 缓存获取通用接口
	 * 
	 * @param <T>
	 * 
	 * @param cacheKey
	 *            参考CacheKey类
	 * @param objects
	 *            对应Map里的键值，按业务逻辑顺序依次传入 使用例子： CacheManager.get(key, value); CacheManager.get(key, value1,value2);
	 * @return
	 * @Added by
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T get(String cacheKey, Object... objects) {
		Object o = get(cacheKey);
		try {
			if (objects == null || objects.length <= 0) {// 没有参数
				return (T) o;
			} else if (objects.length == 1) {// 仅有一个参数
				Map map = (Map) o;
				if (map == null) {
					return null;
				}
				Object m = map.get(objects[0]);
				return (T) m;
			} else { // 有多个参数
				Map map = (Map) o;
				for (int i = 0; i < (objects.length - 1); i++) {
					map = (Map) map.get(objects[i]);
				}
				return (T) map.get(objects[objects.length - 1]);
			}
		} catch (Exception e) {
			logger.error("CacheManager.get:", e);
			return null;
		}
	}

	/**
	 * 更新缓存通用接口
	 * 
	 * @param CacheKey
	 *            参考CacheKey类
	 * @param key
	 *            包含多个key值，则按业务逻辑顺序依次传入 使用例子： CacheManager.put(cacheKey, value, key); CacheManager.put(cacheKey, value, key1,key2);
	 * @param value
	 *            缓存值
	 * @Added by
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void put(String cacheKey, Object value, Object... key) {
		Map map = (Map) get(cacheKey);
		if (map == null || key.length <= 0) {
			return;
		}
		try {
			if (key.length == 1) {
				map.put(key, value);
				return;
			} else {
				for (int i = 0; i < (key.length - 1); i++) {
					map = (Map) map.get(key[i]);
				}
				map.put(key, value);
			}

		} catch (Exception e) {
			logger.error("CacheManager.put:", e);
		}
	}

	/**
	 * 删除缓存通用接口
	 * 
	 * @param CacheKey
	 *            参考CacheKey类
	 * @param key
	 *            包含多个key值，则按业务逻辑顺序依次传入 使用例子： CacheManager.remove(cacheKey, key); CacheManager.remove(cacheKey, key1,key2);
	 * @param value
	 *            缓存值
	 * @Added by
	 */
	@SuppressWarnings("rawtypes")
	public static void remove(String cacheKey, Object... key) {
		Map map = (Map) get(cacheKey);
		if (map == null || key.length <= 0) {
			return;
		}
		try {
			if (key.length == 1) {
				map.remove(key[0]);
				return;
			} else {
				for (int i = 0; i < (key.length - 1); i++) {
					map = (Map) map.get(key[i]);
				}
				map.remove(key[key.length - 1]);
			}
		} catch (Exception e) {
			logger.error("CacheManager.remove:", e);
		}
	}
	

	public static BaseOnlinePlayer getPlayerInfo(Integer playerId) {
		return playerInfoMap.get(playerId);
	}

	/**
	 * 获取所有玩家
	 * @return
	 */
	public static Collection<BaseOnlinePlayer> getAllPlayer(){
		return playerInfoMap.values();
	}

	public static BaseOnlinePlayer getPlayerInfoByChannelUidKey(String channel,
			String area, String uid) {
		String key = BaseOnlinePlayer.getChannelAccountKey(channel, area, uid);
		Integer playerId = get(CacheKey.KEY_2_PLAYERID, key);
		return getPlayerInfo(playerId);
	}
}
