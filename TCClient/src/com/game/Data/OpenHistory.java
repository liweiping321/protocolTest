package com.game.Data;

import com.game.config.TCConfig;
import com.game.utils.StringUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/2/27.
 */
public class OpenHistory extends AbstractObjectSave<List<String>> implements Savable {

	public OpenHistory() {
		super(SaveModule.OpenHistory);
	}

	@Override
	protected Path getPath() {
		return Paths.get(TCConfig.getSavePath(), "OpenHistory.bat");
	}

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
		if (saveData.size() > 7) {
			for (int i = 7; i < saveData.size(); i++) {
				saveData.remove(i);
			}
		}
		return saveData;
	}

	@Override
	public String[] getData() {
		return saveData.toArray(new String[saveData.size()]);
	}

	@Override
	public <T> void addData(T data) {
		if (data instanceof String) {
			if (!StringUtil.isEmpty((String) data) && !saveData.contains(data)) {
				saveData.add((String) data);
			}
		}
	}
}
