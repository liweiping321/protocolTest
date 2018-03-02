package com.game.proxy;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;

import com.game.type.DirectType;
import com.game.utils.LogUtils;

/**
 * 消息执行体
 * @author jason.lin
 *
 */
public class MsgRunnable implements Runnable{
	private ConnectProxy proxy;
	private final  Object msg;
	private DirectType directType;
	
	public MsgRunnable(ConnectProxy proxy, Object msg, DirectType directType){
		this.proxy = proxy;
		this.msg = msg;
		this.directType = directType;
	}
	
	@Override
	public void run() {
		try {
			
			if(DirectType.UP == this.directType){
				if(proxy.getClient() == null){
					Thread.sleep(1);
					proxy.getQueue().add(this);
					return;
				}
			
//				System.err.println(proxy.getClient().getId() + " ....发送:" + this.directType);
				proxy.getClient().write(msg);
			}else {
				WriteFuture future=proxy.getSession().write(msg);
				future.addListener(new IoFutureListener<IoFuture>(){

					@Override
					public void operationComplete(IoFuture future) {
//						TSMinaMsg tsMsg = (TSMinaMsg)msg;
//						System.out.println(tsMsg.getCode());
					}});
			}
		} catch (Throwable e) {
			LogUtils.error("addMsg", e);
		} finally {
			proxy.getQueue().remove();
		}
	}
}
