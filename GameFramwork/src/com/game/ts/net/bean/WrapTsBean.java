package com.game.ts.net.bean;

import java.awt.*;

/**
 * @author jianpeng.zhang
 * @since 2017/2/9.
 */
public class WrapTsBean implements Cloneable
{
    private TSBean tsBean;
    /**
     * 标记是否已被删除
     */
    private boolean hasDelete = false;
    /**
     * 标记是否已发送
     */
    private boolean hasSend= false;
    /**
     * 标记是否被标记
     */
    private boolean hasMark= false;

    private Color backgroundColor;

    public WrapTsBean(TSBean tsBean)
    {
        this.tsBean = tsBean;
    }

    public TSBean getTsBean()
    {
        return tsBean;
    }

    public void setTsBean(TSBean tsBean)
    {
        this.tsBean = tsBean;
    }

    public boolean isHasDelete()
    {
        return hasDelete;
    }

    public void setHasDelete(boolean hasDelete)
    {
        this.hasDelete = hasDelete;
    }

    public boolean isHasSend()
    {
        return hasSend;
    }

    public void setHasSend(boolean hasSend)
    {
        this.hasSend = hasSend;
    }

    public boolean isHasMark()
    {
        return hasMark;
    }

    public void setHasMark(boolean hasMark)
    {
        this.hasMark = hasMark;
    }

    public Color getBackgroundColor()
    {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor)
    {
        this.backgroundColor = backgroundColor;
    }

    public WrapTsBean clone()
    {
        WrapTsBean bean=null;
        try
        {
            bean = (WrapTsBean) super.clone();
            bean.setTsBean((TSBean) tsBean.clone());
        }
        catch(CloneNotSupportedException e)
        {
            System.out.println(e.toString());
        }
        return bean;
    }

}

