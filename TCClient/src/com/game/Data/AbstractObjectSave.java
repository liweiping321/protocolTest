package com.game.Data;

import com.game.utils.LogUtils;
import com.game.utils.language.LanguageMgr;

import java.io.*;
import java.nio.file.Path;

/**
 * @author jianpeng.zhang
 * @since 2017/2/6.
 */
public abstract class AbstractObjectSave<F> implements Savable
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
     * 返回要保存的数据
     */
    protected F getSavePart()
    {
        return saveData;
    }

    private SaveModule saveModule;


    public AbstractObjectSave(SaveModule saveModule)
    {
        this.saveModule = saveModule;
    }

    @Override public void save()
    {
        ObjectOutputStream oos = null;
        try {
            if (getPath() == null)
            {
                LogUtils.error(saveModule + " " + LanguageMgr.getStringLength("TClient.Save.Error"));
                return;
            }
            File file = getPath().toFile();
            if (!file.exists()) {
                if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                    LogUtils.error(saveModule + " " + LanguageMgr.getStringLength("TClient.Save.Error"));
                    return;
                }
                if (!file.createNewFile()) {
                    LogUtils.error(saveModule + " " + LanguageMgr.getStringLength("TClient.Save.Error"));
                    return;
                }
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(getSavePart());
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.error(saveModule + " " + LanguageMgr.getStringLength("TClient.Save.Error"), e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    LogUtils.error(e);
                }
            }
        }
    }

    @Override public void load()
    {
        ObjectInputStream ois = null;
        if (getPath() == null)
        {
            dealReadObject(null);
            return;
        }
        try {
            File file = getPath().toFile();
            Object object = null;
            if (file.exists()) {
                ois = new ObjectInputStream(new FileInputStream(file));
                object = ois.readObject();
            }
            dealReadObject(object);

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
