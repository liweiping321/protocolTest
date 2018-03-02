package com.game.Data;

import java.util.Map;

/**
 * @author jianpeng.zhang
 * @since 2017/2/6.
 */
public enum SaveModule {
	MessageData("服务端发过来显示在表格的消息"),
    FilterIp("地址栏的数据"),
    FilterText("过滤工具栏的数据"),
    FindConditionText("查询的历史记录"),
	RecordPathData("录制的路径的历史记录"),
	RecordData("录制的信息"),
	OpenHistory("打开回放文件的历史记录");

	String description;

	SaveModule(String description) {
		this.description = description;
	}

	public static void bind(Map<SaveModule, Savable> saveMap)
	{
		if (saveMap != null)
		{
			saveMap.put(SaveModule.FilterIp, new FilterIpHistoryData());
			saveMap.put(SaveModule.FilterText, new FilterHistory());
			saveMap.put(SaveModule.FindConditionText, new FindConditionHistory());
			saveMap.put(SaveModule.RecordData, new RecordData());
			saveMap.put(SaveModule.RecordPathData, new RecordPathHistory());
			saveMap.put(SaveModule.OpenHistory, new OpenHistory());
		}
	}
}
