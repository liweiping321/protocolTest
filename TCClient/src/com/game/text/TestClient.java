package com.game.text;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;

import com.game.utils.DateUtil;

/**
 * @author jianpeng.zhang
 * @since 2017/1/9.
 */
public class TestClient
{
    public static void main(String[] args)
    {
        // Client client = new Client();
        // client.showLog(LogLevel.DEBUG, "sdddddddddddddjklnksdnfklsklfsjdfs 圣诞节饭卡是否可使肌肤");
        // client.showLog(LogLevel.INFO, "sadkjfndsjfks及第三方看看书呢收到解决肤");
        // client.showLog(LogLevel.WARNING, "sddddddddddd是我颇为破门可使肌肤");
        // client.showLog(LogLevel.ERROR, "sddddddd破武器二没看啥地方肯德基是覅你都说了肤");
        // client.showLog(LogLevel.CRITICAL, "你下次vjsdfjsfjis了看见啥地方的快乐飞是第三方 是否看见是福建省的是否可使肌肤");

        try
        {
            FileOutputStream fos = new FileOutputStream(new File("history.txt"));
            ObjectOutputStream oos = null;
            oos = new ObjectOutputStream(fos);
            oos.writeObject("dsfafasdfasdfdlas");
            oos.writeInt(2443);
            oos.writeObject(new HashMap<>());
            oos.flush();

            FileInputStream fis = new FileInputStream(new File("history.txt"));
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object oo = ois.readObject();
            int i = ois.readInt();
            oo = ois.readObject();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }


        Integer i1 = new Integer(23);
        Integer i2 = new Integer(23);
        System.out.println(i1.equals(i2));

        Calendar calendar = Calendar.getInstance();
        System.out.println(DateUtil.getDataString());

    }
}
