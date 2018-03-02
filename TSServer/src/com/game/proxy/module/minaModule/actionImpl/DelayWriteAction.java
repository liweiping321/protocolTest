package com.game.proxy.module.minaModule.actionImpl;

import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;

import com.game.TSServer;
import com.game.proxy.module.minaModule.IPackWriteAction;
import com.game.ts.net.TSMinaMsg;

/**
 * 延迟发包
 * @author jason.lin
 *
 */
public class DelayWriteAction implements IPackWriteAction{
	// 最大延时
	public static final int maxDelay = 10 * 60 * 1000;
	
	@Override
	public void handleActionWrite(String[] args, final IoSession session, final TSMinaMsg request) {
		int delay = Integer.valueOf(args[0]);
		if(delay > maxDelay){
			delay = maxDelay;
		}else if(delay <= 0){
			delay = 1;
		}
		
		TSServer.timer.schedule(new TimerTask() {  
            public void run() {  
            	session.write(request);
            }  
        }, delay);
		
	}

}
