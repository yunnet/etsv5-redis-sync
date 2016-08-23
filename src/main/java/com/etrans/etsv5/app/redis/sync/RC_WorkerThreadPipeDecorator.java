package com.etrans.etsv5.app.redis.sync;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_WorkerThreadPipeDecorator.java  <br />
 * 说       明: etsv5-redis-cacher  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016上午11:58:42  <br />
 * 最后修改: 2016上午11:58:42  <br />
 * 修改历史:   <br />
 */

public class RC_WorkerThreadPipeDecorator<IN, OUT> implements RC_Pipe<IN, OUT>{
	private final BlockingQueue<IN> workQueue;
	private Set<RC_AbstractTerminatableThread> workThreads = new HashSet<>();
	private RC_TerminationToken token = new RC_TerminationToken();
	
	private RC_Pipe<IN, OUT> delegate;

	/**
	 * 构造函数
	 * @param _delegate
	 * @param _workerCount
	 */
	public RC_WorkerThreadPipeDecorator(RC_Pipe<IN, OUT> _delegate, int _workerCount) {
		this(new SynchronousQueue<IN>(), _delegate, _workerCount);
	}
	
	public RC_WorkerThreadPipeDecorator(BlockingQueue<IN> _workQueue, RC_Pipe<IN, OUT> _delegate, int _workerCount) {
		if(_workerCount <= 0)
			throw new IllegalArgumentException("workerCount should be positive!");
		
		this.workQueue = _workQueue;
		this.delegate = _delegate;
		
		for(int i = 0; i < _workerCount; i++){
			workThreads.add(new RC_AbstractTerminatableThread(token) {
				@Override
				protected void doRun() throws Exception {
					try {
						dispatch();
					} finally {
						token.reservations.decrementAndGet();
					}
				}
			});
		}
	}

	protected void dispatch() throws InterruptedException {
		IN input = workQueue.take();
		delegate.process(input);
	}

	@Override
	public void init(RC_PipeContext _pipeCtx) {
		delegate.init(_pipeCtx);
		
		for(RC_AbstractTerminatableThread thread : workThreads)
			thread.start();
	}

	@Override
	public void shutdown(long _timeout, TimeUnit _unit) {
		for(RC_AbstractTerminatableThread thread : workThreads){
			thread.terminate();
			try {
				thread.join(TimeUnit.MILLISECONDS.convert(_timeout, _unit));
			} catch (InterruptedException e) {
			}
		}
		
		delegate.shutdown(_timeout, _unit);
	}

	@Override
	public void setNextPipe(RC_Pipe<?, ?> _nextPipe) {
		delegate.setNextPipe(_nextPipe);
	}

	@Override
	public void process(IN input) throws InterruptedException {
		this.workQueue.put(input);
		token.reservations.incrementAndGet();		
	}

}
