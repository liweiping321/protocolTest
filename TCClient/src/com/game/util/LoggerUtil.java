package com.game.util;

import com.game.ClientUI.Client;
import com.game.Data.LogLevel;
import com.game.utils.LogUtils;

/**
 * @author jianpeng.zhang
 * @since 2017/1/19.
 */
public class LoggerUtil
{
    public static void info(Client client, String message)
    {
        LogUtils.info(message);
        if (client != null)
        {
            client.showLog(LogLevel.INFO, message);
            //将光标移到最上面
            client.getMessagePane().setCaretPosition(client.getMessagePane().getDocument().getLength());
        }
    }

    public static void warn(Client client, String message)
    {
        LogUtils.warn(message);
        if (client != null)
        {
            client.showLog(LogLevel.WARNING, message);
            client.getMessagePane().setCaretPosition(client.getMessagePane().getDocument().getLength());
        }
    }

    public static void error(Client client, String message)
    {
        LogUtils.error(message);
        if (client != null)
        {
            client.showLog(LogLevel.ERROR, message);
            client.getMessagePane().setCaretPosition(client.getMessagePane().getDocument().getLength());
        }
    }

    public static void critical(Client client, String message)
    {
        LogUtils.error(message);
        if (client != null)
        {
            client.showLog(LogLevel.CRITICAL, message);
            client.getMessagePane().setCaretPosition(client.getMessagePane().getDocument().getLength());
        }
    }
}
