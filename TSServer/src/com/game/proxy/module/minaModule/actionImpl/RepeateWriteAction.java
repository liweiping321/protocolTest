package com.game.proxy.module.minaModule.actionImpl;

import java.util.TimerTask;

import org.apache.mina.core.session.IoSession;

import com.game.TSServer;
import com.game.proxy.module.minaModule.IPackWriteAction;
import com.game.ts.net.TSMinaMsg;

/**
 * 重复发包
 * @author jason.lin
 *
 */
public class RepeateWriteAction implements IPackWriteAction{
	// 最大发包数
	public static final int maxNum = 10 * 60 * 1000;
	
	private int times = 0;// 次数
	private int delay = 0;// 间隔时间
	@Override
	public void handleActionWrite(String[] args, final IoSession session, final TSMinaMsg request) {
		times = Integer.valueOf(args[0]);// 次数
		delay = 0;                       // 间隔时间
		if(args.length >= 2){
			delay = Integer.valueOf(args[1]);
		}
		
		// 上线限制
		if(times > maxNum){
			times = maxNum;
		}else if(times <= 0){
			times = 1;
		}
		
		if(delay == 0){
			for(int index = 0; index < times; index ++){
				session.write(request);
			}
		}else {
			TSServer.timer.schedule(new TimerTask() {  
				public void run() {  
					for(int index = 0; index < times; index ++){
						session.write(request);
					}  
				}
			}, delay);
		}
	}

}
