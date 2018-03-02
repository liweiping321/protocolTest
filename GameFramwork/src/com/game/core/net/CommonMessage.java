/**
 * Date: May 15, 2013
 * 
 * Copyright (C) 2013-2015 7Road. All rights reserved.
 */

package com.game.core.net;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * 游戏协议包类，描述具体游戏数据包结构。<br>
 * <br>
 * 封包规则：一个包分为包头和包体两部分，结构如下：<br>
 * 【分隔符|包长|校验和|code】【包体】。<br>
 * 其中，包头各部分长度为2字节。检验和计算范围从code开始，直到整个包结束。
 * 
 **/
public class CommonMessage {
	/** 包头长度 */
	public static final short HDR_SIZE = 8;
	/** 包分隔符 */
	public static final short HEADER = 0x71ab;
	/** 协议号 */
	private short code;
	/** 包体数据 */
	private byte[] body;

	/**
	 * 构建实例。<br>
	 * 注意：所构建的实例的校验和是从输入参数msgData中读取的，并非通过消息数据计算所得。
	 * 
	 * @param msgData
	 *            消息数据，至少包括包头，允许不带包体数据。
	 * @return
	 */
	public static Request build(byte[] msgData) {
		if (msgData == null || msgData.length < HDR_SIZE ) {
			return null;
		}

		ByteBuffer buff = ByteBuffer.wrap(msgData);
		buff.position(HDR_SIZE - 2);
		short code = buff.getShort();
		int bodyLen = msgData.length - HDR_SIZE;
		if (bodyLen > 0) {
			byte[] body = new byte[bodyLen];
			buff.get(body, 0, bodyLen);
			return Request.valueOf(code, body);
		} else {
			return Request.valueOf(code, null);
		}
	}

	/**
	 * 数据包转换为ByteBuffer，包括包头和包体。
	 * 
	 * @return
	 */
	public ByteBuffer toByteBuffer() {
		int len = getLen();
		ByteBuffer buff = ByteBuffer.allocate(len);
		buff.putShort(HEADER);
		
		// 插入长度
		buff.putInt(len);
		buff.putShort(code);
		if (body != null) {
			buff.put(body);
		}
		buff.flip();
		return buff;
	}

	/**
	 * 获取数据包的长度，包括包头和包体。
	 * 
	 * @return
	 */
	public int getLen() {
		int bodyLen = body == null ? 0 : body.length;
		return (HDR_SIZE + bodyLen);
	}

	/**
	 * 获取协议号
	 * 
	 * @return
	 */
	public short getCode() {
		return code;
	}

	/**
	 * 设置协议号
	 * 
	 * @param code
	 */
	public void setCode(short code) {
		this.code = code;
	}

	/**
	 * 获取包体，包体允许为null。
	 * 
	 * @return
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * 设置包体，包体允许为null。
	 * 
	 * @param bytes
	 */
	public void setBody(byte[] bytes) {
		this.body = bytes;
	}

	/**
	 * 包头的字符串表示形式。
	 * 
	 * @return
	 */
	public String headerToStr() {
		StringBuilder sb = new StringBuilder();
		sb.append("len: ").append(getLen());
		sb.append(", code: 0x").append(Integer.toHexString(code));
		return sb.toString();
	}

	/**
	 * 数据包的字符串表示形式。
	 * 
	 * @return
	 */
	public String detailToStr() {
		String str = "";
		if (body != null) {
			try {
				str = new String(body, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				str = "(UnsupportedEncodingException)";
			}
		}
		return String.format("%s. content:%s.", headerToStr(), str);
	}

	@Override
	public String toString() {
		return "CommonMessage [code=" + code + "]";
	}
}
