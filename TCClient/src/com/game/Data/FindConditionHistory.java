package com.game.Data;

import com.game.config.TCConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jianpeng.zhang
 * @since 2017/2/17.
 *
 * 查找表达式的历史记录
 */
public class FindConditionHistory extends AbstractObjectSave<Set<String>> implements Savable
{
    public FindConditionHistory()
    {
        super(SaveModule.FindConditionText);
    }

    @Override protected Path getPath()
    {
        return Paths.get(TCConfig.getSavePath(), "data3.bat");
    }

    @SuppressWarnings("unchecked")
    @Override protected void dealReadObject(Object object)
    {
        if (object instanceof Set) {
            saveData = (Set<String>) object;
        } else {
            saveData = new HashSet<>();
        }
    }

    @Override public String[] getData()
    {
        String[] data = new String[saveData.size()];
        saveData.toArray(data);
        return data;
    }

    @Override public <T> void addData(T data)
    {
        if (data != null && data instanceof String) {
            saveData.add((String) data);
        }
    }
}
