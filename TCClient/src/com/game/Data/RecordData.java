package com.game.Data;

import com.alibaba.fastjson.JSON;
import com.game.bean.RecordBean;
import com.game.ts.net.bean.TSBean;
import com.game.ts.net.bean.WrapTsBean;
import com.game.utils.StringUtil;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/2/22.
 * 用于存放录制信息的类
 */
public class RecordData extends AbstractJsonSave<RecordBean> implements Savable
{
    private boolean needSave = true;

    public RecordData()
    {
        super(SaveModule.RecordData);
    }

    public void setFilePath(String filePath)
    {
        saveData.setFilePath(filePath);
        needSave = true;
    }

    public void setFileName(String fileName)
    {
        saveData.setFileName(fileName);
        needSave = true;
    }

    public void setMessage(String message)
    {
        saveData.setMessage(message);
        needSave = true;
    }

    public void clear()
    {
        saveData.clear();
        needSave = true;
    }

    @Override protected boolean needSave()
    {
        return needSave;
    }

    @Override protected Path getPath()
    {
        if (saveData == null || StringUtil.isEmpty(saveData.getFilePath()) || StringUtil
                .isEmpty(saveData.getFileName()))
        {
            return null;
        }
        if (saveData.getFileName().endsWith(".tb"))
            return Paths.get(saveData.getFilePath(), saveData.getFileName());
        else
            return Paths.get(saveData.getFilePath(), saveData.getFileName() + ".tb");
    }

    @Override protected void dealReadObject(Object object)
    {
        try
        {
            saveData = JSON.parseObject((String) object, RecordBean.class);
        }
        finally
        {
            if (saveData == null)
            {
                saveData = new RecordBean();
            }
            needSave = false;
        }

    }

    @Override public void save()
    {
        super.save();
        needSave = false;
    }
    @SuppressWarnings("unchecked")
    @Override public List<WrapTsBean> getData()
    {
        List<WrapTsBean> tsBeanList = new ArrayList<>();
        for (TSBean tsBean : saveData.getSaveList())
        {
            tsBeanList.add(new WrapTsBean(tsBean));
        }
        return tsBeanList;
    }

    @Override public <T> void addData(T data)
    {
        if (data instanceof TSBean)
        {
            saveData.add((TSBean) data);
            needSave = true;
        }
    }
}
