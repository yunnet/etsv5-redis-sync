package com.etrans.etsv5.app.redis.sync;

import java.lang.ref.WeakReference;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_TerminationToken.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:20:06 <br />
 * 最后修改: 2016年8月23日 下午4:20:06 <br />
 * 修改历史: <br />
 */
public class RC_TerminationToken {
	// 使用volatile修饰，以保证无需显式锁的情况下该变量的内存可见性
	protected volatile boolean toShutdown = false;
	
	//reservations 预定
	public final AtomicInteger reservations = new AtomicInteger(0);
	
	/*
	 * 在多个可停止线程实例共享一个TerminationToken实例的情况下，该队列用于
	 * 记录那些共享TerminationToken实例的可停止线程，以便尽可能减少锁的使用 的情况下，实现这些线程的停止。
	 */
	private final Queue<WeakReference<RC_Terminatable>> coordinatedThreads;

	/**
	 * 
	 */
	public RC_TerminationToken() {
		coordinatedThreads = new ConcurrentLinkedQueue<WeakReference<RC_Terminatable>>();
	}
	
	public boolean isToShutdown(){
		return this.toShutdown;
	}
	
	public void setToShutdown(boolean _toshutdown){
		this.toShutdown = _toshutdown;
	}
	
	protected void register(RC_Terminatable _terminaltable){
		coordinatedThreads.add(new WeakReference<RC_Terminatable>(_terminaltable));
	}
	
	/**
	 * 通知TerminationToken实例：共享该实例的所有可停止线程中的一个线程停止了， 以便其停止其它未被停止的线程。
	 * @param _terminaltable 已停止的线程
	 */
	protected void notifyThreadTermination(RC_Terminatable _terminaltable){
		WeakReference<RC_Terminatable> wrThread;
		RC_Terminatable otherThread;
		while (null != (wrThread = coordinatedThreads.poll())) {
			otherThread = wrThread.get();
			if(null != otherThread && otherThread != _terminaltable)
				otherThread.terminate();			
		}
	}

}
