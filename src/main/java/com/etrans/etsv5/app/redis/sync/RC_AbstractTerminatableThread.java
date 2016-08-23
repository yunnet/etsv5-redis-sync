package com.etrans.etsv5.app.redis.sync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_AbstractTerminatableThread.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:22:37 <br />
 * 最后修改: 2016年8月23日 下午4:22:37 <br />
 * 修改历史: <br />
 */
public abstract class RC_AbstractTerminatableThread extends Thread implements RC_Terminatable {
	public final RC_TerminationToken token;
	
	/**
	 * 构造函数
	 */
	public RC_AbstractTerminatableThread() {
		this(new RC_TerminationToken());
	}
	
	/**
	 * 构造函数
	 * @param _token
	 */
	public RC_AbstractTerminatableThread(RC_TerminationToken _token) {
		super();
		this.setDaemon(true);
		this.token = _token;	
		_token.register(this);
	}
	
	/**
	 * 留给子类实现其线程处理逻辑。
	 * @throws Exception
	 */
	protected abstract void doRun() throws Exception;

	/**
	 * 留给子类实现。用于实现线程停止后的一些清理动作。
	 * @param cause
	 */
	protected void doCleanup(Exception cause) {
		// 什么也不做
	}

	/**
	 * 留给子类实现。用于执行线程停止所需的操作。
	 */
	protected void doTerminiate() {
		// 什么也不做
	}
	
	@Override
	public void run() {
		Exception ex = null;
		try {
			for(;;){
				// 在执行线程的处理逻辑前先判断线程停止的标志。
				if(token.isToShutdown() && token.reservations.get() <= 0)
					break;
				
				doRun();
			}
		} catch (Exception e) {
			// 使得线程能够响应interrupt调用而退出
			ex = e;
		} finally {
			try {
				doCleanup(ex);
			} finally {
				token.notifyThreadTermination(this);
			}
		}
	}

	@Override
	public void interrupt() {
		terminate();
	}
	
	/**
	 * 请求停止线程。
	 */
	@Override
	public void terminate() {
		token.setToShutdown(true);
		try {
			doTerminiate();
		} finally {
			// 若无待处理的任务，则试图强制终止线程
			if(token.reservations.get() <= 0)
				super.interrupt();
		}
	}
	
	public void terminate(boolean _waitUtilThreadTerminated){
		terminate();
		
		if(_waitUtilThreadTerminated){
			try {
				//等待该线程终止
				this.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

}
