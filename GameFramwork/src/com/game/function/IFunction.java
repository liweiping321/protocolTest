package com.game.function;

/**
 * 方法
 * @author jason.lin
 *
 */
public interface IFunction <T> {

	/**
	 * 执行1
	 * @param params
	 * @return
	 */
	public T execute1(Object ... params);
	
	/**
	 * 执行2
	 * @param params
	 * @return
	 */
	public T execute2(Object ... params);
}
