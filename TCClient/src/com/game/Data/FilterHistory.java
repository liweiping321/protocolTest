package com.game.Data;

import com.game.component.FilterStringPanel;
import com.game.config.TCConfig;
import com.game.utils.StringUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/2/6.
 *
 *        过滤栏历史数据
 */
public class FilterHistory extends AbstractObjectSave<List<String>> implements Savable {

	public FilterHistory()
	{
		super(SaveModule.FilterText);
	}

	@Override
	protected Path getPath() {
		return Paths.get(TCConfig.getSavePath(), "data2.bat");
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

	@SuppressWarnings("unchecked")
	@Override
	public FilterStringPanel[] getData() {
		List<FilterStringPanel> filterHistoryPanel = new ArrayList<>();
		for (String string : saveData) {
			filterHistoryPanel.add(new FilterStringPanel(string));
		}
		return filterHistoryPanel.toArray(new FilterStringPanel[filterHistoryPanel.size()]);
	}

	@Override public <T> void addData(T data)
	{
		if (data != null && data instanceof String)
		{
			if (!StringUtil.isEmpty((String) data) && !saveData.contains(data))
			{
				saveData.add((String) data);
			}
		}
	}

	public void clearData()
	{
		saveData.clear();
	}
}
