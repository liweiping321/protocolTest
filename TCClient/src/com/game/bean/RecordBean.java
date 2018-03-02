package com.game.bean;

import com.game.ts.net.bean.TSBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jianpeng.zhang
 * @since 2017/2/23.
 */
public class RecordBean implements Serializable
{
    private String filePath;
    private String fileName;
    /**
     * 描述
     */
    private String message;
    private List<TSBean> saveList = new ArrayList<>();

    public String getFilePath()
    {
        return filePath;
    }

    public void setFilePath(String filePath)
    {
        this.filePath = filePath;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<TSBean> getSaveList()
    {
        return saveList;
    }

    public void setSaveList(List<TSBean> saveList)
    {
        this.saveList = saveList;
    }

    public void clear()
    {
        if (saveList != null)
        {
            saveList.clear();
        }

    }

    public void add(TSBean tsBean)
    {
        if (saveList == null)
        {
            saveList = new ArrayList<>();
        }
        saveList.add(tsBean);
    }
}
