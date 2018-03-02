package com.game.db.database;

/**
 * @author andy
 * @date 2011-05-04
 * @version
 * 
 */
public class DataObject
{
    private short op = 0;

    public final void setOp(short option)
    {
        if ((this.op == DataOption.INSERT) && (option == DataOption.UPDATE))
        {
            return;
        }
        this.op = option;
    }

    public final short getOp()
    {
        return this.op;
    }
}
