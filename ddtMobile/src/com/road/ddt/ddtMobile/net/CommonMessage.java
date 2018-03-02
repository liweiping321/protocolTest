/**
 * Date: May 15, 2013
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */

package com.road.ddt.ddtMobile.net;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;

import com.game.ts.net.AbstractTSMinaMsg;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.GeneratedMessage.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

/**
 * 游戏协议包类，描述具体游戏数据包结构。<br>
 * <br>
 * 封包规则：一个包分为包头和包体两部分，结构如下：<br>
 * 【分隔符|包长|校验和|code】【包体】。<br>
 * 其中，包头各部分长度为2字节。检验和计算范围从code开始，直到整个包结束。
 * ge
 * @author weihua.cui
 * @author jiayi.wei
 **/
public class CommonMessage extends  AbstractTSMinaMsg{
 
	private static final Logger LOGGER = LogManager.getLogger(CommonMessage.class);
    /** 包头长度 */
    public static final short HDR_SIZE = 16;

    /** 包分隔符 */
    public static final short HEADER = 0x71ab;

    /** 校验和 */
    private short checksum;

    /** 协议号 */
    private short code;

    /** 玩家ID **/
    private long userID;

    /** 包体数据 */
    private byte[] bodyData = null;

    private Message pbMessage;

    /**
     * build实例用，防止类外部创建空消息。
     */
    private CommonMessage()
    {
    }

    /**
     * 构造方法
     * 
     * @param code
     *            协议号
     */
    public CommonMessage(short code)
    {
        this.code = code;
    }
    
    public CommonMessage(short code,Message pbMessage)
    {
    	this.code=code;
    	this.pbMessage=pbMessage;
    }

    /**
     * 获取数据包的长度，包括包头和包体。
     * 
     * @return
     */
    public int getLen()
    {
        if (bodyData != null)
        {
            int bodyLen = bodyData == null ? 0 : bodyData.length;
            return HDR_SIZE + bodyLen;
        }
        
        if (pbMessage != null)
            return HDR_SIZE + pbMessage.getSerializedSize();
        
        return HDR_SIZE;
    }

    /**
     * 获取校验和。<br>
     * 注意：获取到的校验和可能与实时计算的不相等，这取决于您的操作顺序。
     * 
     * @return
     */
    public short getChecksum()
    {
        return checksum;
    }

    /**
     * 获取协议号
     * 
     * @return
     */
    public short getCode()
    {
        return code;
    }

    /**
     * 设置协议号
     * 
     * @param code
     */
    public void setCode(short code)
    {
        this.code = code;
    }

    /**
     * 获取包体，包体允许为null。
     * 
     * @return
     */
    public byte[] getBody()
    {
        return bodyData;
    }

    /**
     * 设置包体，包体允许为null。
     * 
     * @param bytes
     */
    public void setBody(byte[] bytes)
    {
        this.bodyData = bytes;
    }

    /**
     * 包头的字符串表示形式。
     * 
     * @return
     */
    public String headerToStr()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("len: ").append(getLen());
        sb.append(", checksum: ").append(checksum);
        sb.append(", code: 0x").append(Integer.toHexString(code));
        sb.append(", param: ").append(userID);
        return sb.toString();
    }

    /**
     * 数据包的字符串表示形式。
     * 
     * @return
     */
    public String detailToStr()
    {
        String str = "";
        if (bodyData != null)
        {
            try
            {
                str = new String(bodyData, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                str = "(UnsupportedEncodingException)";
            }
        }

        return String.format("%s. content:%s.", headerToStr(), str);
    }

    public long getUserID()
    {
        return userID;
    }

    public void setUserID(long userID)
    {
        this.userID = userID;
    }

    public void writeTo(IoBuffer buffer)
    {
        short len = (short) getLen();

        try
        {
            buffer.putShort(HEADER);
            buffer.putShort(len);
            buffer.position(6);
            buffer.putShort(code);
            buffer.putLong(userID);

            if (pbMessage != null)
            {
                CodedOutputStream cos = CodedOutputStream.newInstance(buffer.asOutputStream());
                pbMessage.writeTo(cos);
                cos.flush();
            }
            else if (bodyData != null)
            {
                buffer.put(bodyData);
            }
            else
            {
//                LOGGER.info("code {} body is null", getCode());
            }
        }
        catch (Exception e)
        {
            LOGGER.error(e.getMessage(),e);
        }
    }

    public void setBuilder(Builder<?> builder)
    {
        this.pbMessage = builder.build();
    }

    public void writeCalcSum(IoBuffer buffer)
    {
        short sum = calcChecksum(buffer, 0);
        int oldPosition = buffer.position();
        buffer.position(4);
        buffer.putShort(sum);
        buffer.position(oldPosition);
    }

    public static CommonMessage build(IoBuffer in, int position)
    {
        CommonMessage builder = new CommonMessage();

        // 跳过分隔符和包长，包长由输入参数长度确定。
        in.position(position + 2);
        short len = in.getShort();
        builder.checksum = in.getShort();
        builder.code = in.getShort();
        builder.userID = in.getLong();
        int bodyLen = len - HDR_SIZE;
        if (bodyLen > 0)
        {
            builder.bodyData = new byte[bodyLen];
            in.get(builder.bodyData, 0, bodyLen);
            // 检查校验和是否正确
            short getCS = builder.calcChecksum(in, position);
			if (builder.checksum != getCS) {
				LOGGER.error(String.format(
						"数据包校验失败，数据包将被丢弃。code: 0x%d。校验和应为%d，实际接收校验和为%d 位置%d",
						builder.getCode(), getCS, builder.checksum, position));
				return null;
			}
        }
        else
        {
            builder.bodyData = null;
        }

        return builder;
    }

    private short calcChecksum(IoBuffer buffer, int position)
    {
        int val = 0x77;
        int i = position + 6;
        int size = getLen();
        while (i < size + position)
            val += (buffer.get(i++) & 0xFF);

        return (short) (val & 0x7F7F);
    }
    
    public Message getPbMessage()
    {
        return pbMessage;
    }
 
	@Override
	public byte[] getBytes() {
		 
		return bodyData;
	}
 
	@Override
	public void parseParams(Builder<?> builder) {
	//	LOGGER.error(code+" "+builder.getClass().getName());
		if(bodyData==null)
		{
			return ;
		}
		try {
			builder.mergeFrom(getBytes()).buildPartial();
		
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
 
	@Override
	public int getLength() {
 
		return getLen();
	}

	public void setChecksum(short checksum) {
		this.checksum = checksum;
	}
	
}
