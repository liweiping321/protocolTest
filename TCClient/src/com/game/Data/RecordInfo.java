package com.game.Data;

/**
 * @author jianpeng.zhang
 * @since 2017/2/22.
 */
public class RecordInfo
{
    private static boolean onlyMark;

    private static boolean includeUp;

    private static boolean includeDown;

    public static RecordState state = RecordState.none;

    public enum RecordState
    {
        none, recording, playing, pause;
    }

    public static void reset()
    {
        onlyMark = false;
        includeUp = true;
        includeDown = true;
    }

    /**
     * 是否上行下行操作都保存
     */
    public static boolean bothSave()
    {
        return isIncludeDown() && isIncludeUp();
    }

    public static String getRecordType()
    {
        if (isIncludeUp())
        {
            return "up";
        }
        else
        {
            return "down";
        }
    }

    public static boolean isOnlyMark()
    {
        return onlyMark;
    }

    public static void setOnlyMark(boolean onlyMark)
    {
        RecordInfo.onlyMark = onlyMark;
    }

    public static boolean isIncludeUp()
    {
        return includeUp;
    }

    public static void setIncludeUp(boolean includeUp)
    {
        RecordInfo.includeUp = includeUp;
    }

    public static boolean isIncludeDown()
    {
        return includeDown;
    }

    public static void setIncludeDown(boolean includeDown)
    {
        RecordInfo.includeDown = includeDown;
    }
}
