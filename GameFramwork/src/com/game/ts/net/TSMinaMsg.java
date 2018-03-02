package com.game.ts.net;

import java.util.Date;

import com.google.protobuf.GeneratedMessage.Builder;

/**
 * ts层消息
 * 
 * @author jason.lin
 *
 */
public interface TSMinaMsg {
	/**一级指令*/
	public short getCode();
	/** 二级指令*/
	public short getSubCode();
	/**解析协议*/
	public void parseParams(Builder<?> builder);
	/** 获取包长 */
	public int getLength();
	public String getString();
	public int getInt();
	public long getLong();
	public boolean getBoolean();
	public char getChar();
	public Date getDate();
	public byte getByte();
	public short getShort();
	public void putByte(byte value);
	public void putShort(short value);
	public void putInt(int value);
	public void putLong(long value);
	public void putBoolean(boolean value);
	public void putString(String value);
	public void putDate(Date date);
	public void putChar(char c);
	public void putFloat(float value);
	public void putDouble(double value);
	public float getFloat();
	public double getDouble();
	public void putBytes(byte[] bytes);
	public byte[] getBytes();
	public TSMinaMsg decompress();
	public void compress();
	
}
