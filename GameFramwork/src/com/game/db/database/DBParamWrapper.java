/**
 * All rights reserved. This material is confidential and proprietary to 7ROAD
 */
package com.game.db.database;

import java.util.HashMap;

/**
 * sql脚本参数的包装类
 * 
 * @author tracy
 * @date 2011-12-15
 * @version
 * 
 */
public class DBParamWrapper
{
    private HashMap<Integer, DBParameter> params = null;// in 参数
    private HashMap<Integer, DBParameter> outParams = null;// out 参数 （存储过程有用到）
    private int p = 0;

    public DBParamWrapper()
    {
        this.params = new HashMap<Integer, DBParameter>();
        this.outParams = new HashMap<Integer, DBParameter>();
    }

    /**
     * 添加一个参数
     * 
     * @param type
     *            参数的类型
     * @param o
     *            参数的值
     */
    public void put(int type, Object o)
    {
        params.put(++p, new DBParameter(type, o));
    }

    public void putOutParam(int type, Object o)
    {
        outParams.put(++p, new DBParameter(type, o));
    }

    public HashMap<Integer, DBParameter> getParams()
    {
        return params;
    }

    public HashMap<Integer, DBParameter> getOutParams()
    {
        return outParams;
    }

    /**
     * 重新初始化该sql参数包装类
     */
    public void clear()
    {
        params.clear();
        p = 0;
    }

}
