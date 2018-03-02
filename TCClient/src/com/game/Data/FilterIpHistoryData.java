package com.game.Data;

import com.game.config.TCConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/2/6.
 *
 *        地址栏历史数据
 */
public class FilterIpHistoryData extends AbstractObjectSave<List<String>> implements Savable {

	public FilterIpHistoryData()
	{
		super(SaveModule.FilterIp);
	}

	@Override
	protected Path getPath() {
		return Paths.get(TCConfig.getSavePath(), "data1.bat");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void dealReadObject(Object object) {
		if (object instanceof List) {
			saveData = (List<String>) object;
		} else {
			saveData = new ArrayList<>();
		}
	}

	@Override
	protected List<String> getSavePart() {
		List<String> newSaveData;
		if (saveData.size() > 50) {
			newSaveData = saveData.subList(0, 50);
		} else {
			newSaveData = saveData;
		}
		return newSaveData;
	}

	@Override
	public <T> void addData(T data) {
		if (data != null && data instanceof String) {
			saveData.add((String) data);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String[] getData() {
		return saveData.toArray(new String[saveData.size()]);
	}
}
