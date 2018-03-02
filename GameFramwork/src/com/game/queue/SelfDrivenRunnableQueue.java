package com.game.queue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import com.game.utils.LogUtils;

/**
 * 自驱动的可执行消息队列，能够自动驱动本队列中的指令有序执行
 * 
 * @author Administrator
 * @date
 * @version
 * 
 */
public class SelfDrivenRunnableQueue<MsgType extends Runnable> implements
        IRunnableQueue<MsgType>
{

    // 用于执行消息的线程池，需在队列初始化时指定
    private ExecutorService executorService = null;

    // 消息队列
    private Queue<MsgType> msgQueue = null;

    // 运行状态
    private boolean isRunning = false;

    /**
     * 构造函数，使用非优先队列
     * 
     * @param executorService
     *            消息执行线程池
     */
    public SelfDrivenRunnableQueue(ExecutorService executorService)
    {
        this.executorService = executorService;
        this.msgQueue = new LinkedList<MsgType>();
    }

    public SelfDrivenRunnableQueue(ExecutorService executorService,
            Comparator<MsgType> comparator)
    {
        this.executorService = executorService;
        this.msgQueue = new PriorityQueue<MsgType>(10, comparator);
    }

    /**
     * 添加可执行到队列，若当前队列为空，则自动提交到指定的线程池执行执行
     * 
     * @see com.road.ddt.queue.IRunnableQueue#add(java.lang.Runnable)
     */
    @Override
    public void add(MsgType msg)
    {
        synchronized (msgQueue)
        {
            msgQueue.add(msg);
            /* 原指令队列为空，提交到线程池推动指令执行 */
            if (msgQueue.size() != 0 && !isRunning)
            {
                if (!executorService.isShutdown())
                {
                    executorService.execute(msg);
                    isRunning = true;
                }

            }
        }
    }

    /**
     * 移除消息队列顶部的指令，若当前队列非空，则自动提交指令执行
     * 
     * @see com.road.ddt.queue.IRunnableQueue#remove()
     */
    @Override
    public void remove()
    {
        synchronized (msgQueue)
        {
            try
            {
                isRunning = false;
                msgQueue.remove();

                if (msgQueue.size() != 0)
                {
                    // 修复异常
                    if (!executorService.isShutdown())
                    {
                        executorService.execute(msgQueue.peek());
                        isRunning = true;
                    }
                }
            }
            catch (Exception e)
            {
                LogUtils.error("UserCmdQueue::remove:", e);
            }
        }
    }

    public void exec(MsgType type)
    {
        executorService.execute(type);
    }

    /**
     * 清空所有消息
     * 
     * @see com.road.ddt.queue.IRunnableQueue#clear()
     */
    @Override
    public void clear()
    {
        synchronized (msgQueue)
        {
            msgQueue.clear();
        }
    }

    // -------------------------自驱动对延时action的支持（待优化）--------------------------------------
    private List<Timer> allStayTimers = new ArrayList<Timer>();// 一直存在的Timer
    private Object time_lock = new Object();
    private HashMap<Integer, Timer> needDisposeTimer = new HashMap<Integer, Timer>();// 需要销毁的timer,
                                                                                     // key为定时器的唯一标识

    private Object NDTimer_lock = new Object();
    private AtomicInteger timerID = new AtomicInteger(0);

    private int GatNextTimerId()
    {
        return timerID.addAndGet(1);
    }

    public void LoopAddAction(MsgType type, int loogTime)
    {
        Timer timer = new Timer();
        timer.schedule(new CallBackAddActionTimerTask(type), loogTime, loogTime);
        synchronized (time_lock)
        {
            allStayTimers.add(timer);
        }
    }

    public class CallBackAddActionTimerTask extends TimerTask
    {
        MsgType type;

        public CallBackAddActionTimerTask(MsgType msg)
        {
            type = msg;
        }

        @Override
        public void run()
        {
            CallBackAddAction(type);
        }

    }

    private void CallBackAddAction(MsgType msg)
    {
        add(msg);
    }

    public void delayAddAction(MsgType msg, int delayTime)
    {
        int id = GatNextTimerId();
        TimeAndAction par = new TimeAndAction(id, msg);
        Timer timer = new Timer();
        timer.schedule(new CallBackAddActionAndDisposeTimerTask(par), delayTime);
    }

    public class CallBackAddActionAndDisposeTimerTask extends TimerTask
    {
        TimeAndAction action;

        public CallBackAddActionAndDisposeTimerTask(TimeAndAction action)
        {
            super();
            this.action = action;
        }

        @Override
        public void run()
        {
            CallBackAddActionAndDispose(action);
        }
    }

    private void CallBackAddActionAndDispose(TimeAndAction action)
    {
        try
        {
            add(action.getMsgType());
            String info = null;
            synchronized (NDTimer_lock)
            {
                if (needDisposeTimer.containsKey(action.getId()))
                {
                    needDisposeTimer.get(action.getId()).cancel();
                    needDisposeTimer.remove(action.getId());
                }
                else
                {
                    info = "Timerid:" + action.getId();
                }
            }
            if (info != null)
            {
                LogUtils.error(info);
            }
        }
        catch (Exception e)
        {
            LogUtils.error("定时器销毁错误    {0}", e);
        }
    }

    public class TimeAndAction
    {
        private int id;
        private MsgType msgType;

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public MsgType getMsgType()
        {
            return msgType;
        }

        public void setMsgType(MsgType msgType)
        {
            this.msgType = msgType;
        }

        public TimeAndAction(int id, MsgType msgType)
        {
            super();
            this.id = id;
            this.msgType = msgType;
        }
    }

    public int getActionSize()
    {
        return msgQueue.size();
    }
}
