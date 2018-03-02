/**
 * 
 */
package com.game.ts.net;

import java.util.Date;

/**
 * @author lip.li
 * @date 2018-1-12
 */
public abstract class AbstractTSMinaMsg implements TSMinaMsg {
  
 
	@Override
	public short getSubCode() {
		
		return 0;
	}
 
	@Override
	public String getString() {
		 
		return null;
	}


	@Override
	public int getInt() {
		
		return 0;
	}

	
	@Override
	public long getLong() {
	
		return 0;
	}


	@Override
	public boolean getBoolean() {
	
		return false;
	}


	@Override
	public char getChar() {
	
		return 0;
	}

	
	@Override
	public Date getDate() {
		
		return null;
	}


	@Override
	public byte getByte() {
		
		return 0;
	}


	@Override
	public short getShort() {
	
		return 0;
	}

	
	@Override
	public void putByte(byte value) {
	
		
	}

	
	@Override
	public void putShort(short value) {
	
		
	}


	@Override
	public void putInt(int value) {
	
		
	}

	
	@Override
	public void putLong(long value) {
	
		
	}

	@Override
	public void putBoolean(boolean value) {
	
		
	}

	@Override
	public void putString(String value) {
	
	}

	
	@Override
	public void putDate(Date date) {
	
		
	}

	
	@Override
	public void putChar(char c) {
	
	}

	@Override
	public void putFloat(float value) {
	
	}

	
	@Override
	public void putDouble(double value) {
	
		
	}

	
	@Override
	public float getFloat() {
		
		return 0;
	}

	
	@Override
	public double getDouble() {
	
		return 0;
	}

	@Override
	public void putBytes(byte[] bytes) {
	
		
	}


	@Override
	public byte[] getBytes() {
	
		return null;
	}

	
	@Override
	public TSMinaMsg decompress() {
	
		return null;
	}

	@Override
	public void compress() {
		
	}

}
