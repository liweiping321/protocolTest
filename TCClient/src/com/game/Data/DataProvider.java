package com.game.Data;

import com.game.utils.language.LanguageMgr;
import org.apache.mina.util.CopyOnWriteMap;

import java.awt.*;
import java.util.Map;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class DataProvider implements Savable {

	private Map<SaveModule, Savable> saveMap = new CopyOnWriteMap<>();

	public static final Color[] COLORS = { Color.green, Color.cyan, Color.gray, Color.magenta, Color.lightGray,
			Color.orange, Color.pink, Color.yellow };

	public static int currentColorIndex = 0;

	public static void turnColor() {
		currentColorIndex = ++currentColorIndex % COLORS.length;
	}

	public static String filterMode[] = { LanguageMgr.getTranslation("TClient.JComboBox.AllData"),
			LanguageMgr.getTranslation("TClient.JComboBox.SendData"),
			LanguageMgr.getTranslation("TClient.JComboBox.MarkData"),
			LanguageMgr.getTranslation("TClient.JComboBox.DelData"),
			LanguageMgr.getTranslation("TClient.JComboBox.UpData"),
			LanguageMgr.getTranslation("TClient.JComboBox.DownData") };
	// "所有数据", "已发送数据", "已标记数据", "已丢弃数据", "上行数据", "下行数据", "查找结果"

	public static String[] header = { "", "action", "id", "directType", "ip", "userName", "length", "code", "subCode",
			"config", "desc", "tsBean" };

	public static String[] statistics_header = {"", "all", "up", "down", "up:down"};

	private static DataProvider dataProvider = null;

	private static final Object LOCK = new Object();

	public static DataProvider getInstance() {
		if (dataProvider == null) {
			synchronized (LOCK) {
				if (dataProvider == null) {
					dataProvider = new DataProvider();
				}
			}
		}
		return dataProvider;
	}

	public void init() {
		SaveModule.bind(saveMap);
		load();
	}

	@Override
	public void save() {
		for (Savable savable : saveMap.values()) {
			savable.save();
		}
	}

	@Override
	public void load() {
		for (Savable savable : saveMap.values()) {
			savable.load();
		}
	}

	@Override
	public <T> T getData() {
		return null;
	}

	@Override
	public <T> void addData(T data) {

	}

	public <T> void addModuleData(SaveModule module, T data) {
		saveMap.get(module).addData(data);
	}

	public <T> T getModuleData(SaveModule module, Class<T> clazz) {
		if (saveMap.get(module) != null && clazz.isInstance(saveMap.get(module).getData())) {
			return clazz.cast(saveMap.get(module).getData());
		} else {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public <T extends Savable> T getModule(SaveModule module, Class<T> clazz) {
		if (clazz.isInstance(saveMap.get(module))) {
			return clazz.cast(saveMap.get(module));
		}
		return null;
	}
}
