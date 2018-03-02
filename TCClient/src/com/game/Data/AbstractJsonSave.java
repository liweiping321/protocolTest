package com.game.Data;

import com.alibaba.fastjson.JSON;
import com.game.utils.LogUtils;
import com.game.utils.language.LanguageMgr;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author jianpeng.zhang
 * @since 2017/2/23.
 */
public abstract class AbstractJsonSave<F> implements Savable
{
    protected F saveData;

    /**
     * 返回保存的完整路径
     */
    protected abstract Path getPath();

    /**
     * 处理文件加载到的Object
     */
    protected abstract void dealReadObject(Object object);

    /**
     * @return 需不需要保存，默认为真
     */
    protected boolean needSave()
    {
        return true;
    }

    /**
     * 返回要保存的数据
     */
    protected F getSavePart()
    {
        return saveData;
    }

    private SaveModule saveModule;


    public AbstractJsonSave(SaveModule saveModule)
    {
        this.saveModule = saveModule;
    }

    @Override public void save()
    {
        try {
            if (!needSave())
            {
                return;
            }
            if (getPath() == null)
            {
                LogUtils.error(saveModule + " " + LanguageMgr.getStringLength("TClient.Save.Error"));
                return;
            }
            File file = getPath().toFile();
            if (!file.exists()) {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    LogUtils.error(saveModule + "  " + LanguageMgr.getStringLength("TClient.Save.Error"));
                    return;
                }
                if (!file.createNewFile()) {
                    LogUtils.error(saveModule + "  " + LanguageMgr.getStringLength("TClient.Save.Error"));
                    return;
                }
            }
            FileUtils.write(file, JSON.toJSONString(getSavePart()));
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(saveModule + " " + LanguageMgr.getStringLength("TClient.Save.Error"), e);
        }
    }

    @Override public void load()
    {
        if (getPath() == null)
        {
            dealReadObject(null);
            return;
        }
        try {
            File file = getPath().toFile();
            Object object = null;
            if (file.exists()) {
                object = FileUtils.readFileToString(file) ;
            }
            dealReadObject(object);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
