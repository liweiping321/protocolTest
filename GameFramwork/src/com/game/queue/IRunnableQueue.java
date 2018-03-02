/**
 *All rights reserved. This material is confidential and proprietary to 7ROAD
 */
package com.game.queue;

/**
 * 可执行队列接口，队列元素为可执行对象
 * 
 * @author sky
 * @date 2011-05-05
 * @version
 * 
 */
public interface IRunnableQueue<MsgType extends Runnable>
{
    /**
     * 提交一个可执行消息，入列
     * 
     * @param msg
     *            可执行消息
     */
    public void add(MsgType msg);

    /**
     * 取出队首元素，出列
     */
    public void remove();

    /**
     * 清空队列
     */
    public void clear();
}
