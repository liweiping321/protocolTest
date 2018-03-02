package com.game.Data;

/**
 * @author jianpeng.zhang
 * @since 2017/2/4.
 */
public interface Savable
{
    public void save();

    public void load();

    public <T> T getData();

    public <T> void addData(T data);
}
