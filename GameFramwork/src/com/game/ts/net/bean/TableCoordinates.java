package com.game.ts.net.bean;

/**
 * @author jianpeng.zhang
 * @since 2017/1/10.
 * 表格的位置
 */
public class TableCoordinates
{
    private int x_position;
    private int y_position;

    public TableCoordinates(int x_position, int y_position)
    {
        this.x_position = x_position;
        this.y_position = y_position;
    }

    public int getX_position()
    {
        return x_position;
    }

    public void setX_position(int x_position)
    {
        this.x_position = x_position;
    }

    public int getY_position()
    {
        return y_position;
    }

    public void setY_position(int y_position)
    {
        this.y_position = y_position;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (!(o instanceof TableCoordinates))
            return false;

        TableCoordinates that = (TableCoordinates) o;

        return x_position == that.x_position && y_position == that.y_position;
    }

    @Override public int hashCode()
    {
        int result = x_position;
        result = 31 * result + y_position;
        return result;
    }
}
