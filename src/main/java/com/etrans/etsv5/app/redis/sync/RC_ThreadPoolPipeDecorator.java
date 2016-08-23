package com.etrans.etsv5.app.redis.sync;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_ThreadPoolPipeDecorator.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:19:54 <br />
 * 最后修改: 2016年8月23日 下午4:19:54 <br />
 * 修改历史: <br />
 */
public class RC_ThreadPoolPipeDecorator<IN, OUT> implements RC_Pipe<IN, OUT> {
	private final RC_Pipe<IN, OUT> delegate;
	private final ExecutorService executorSerivce;
	
	private final TerminationToken token;
	//一个同步辅助类，在完成一组正在其他线程中执行的操作之前，它允许一个或多个线程一直等待
	private final CountDownLatch stageProcessDoneLatch = new CountDownLatch(1);

	/**
	 * 构造函数
	 * @param _delegate
	 * @param _executorSerivce
	 */
	public RC_ThreadPoolPipeDecorator(RC_Pipe<IN, OUT> _delegate, ExecutorService _executorSerivce) {
		this.delegate = _delegate;
		this.executorSerivce = _executorSerivce;
		this.token = TerminationToken.newInstance(executorSerivce);
	}

	@Override
	public void init(RC_PipeContext pipeCtx) {
		this.delegate.init(pipeCtx);
	}

	@Override
	public void shutdown(long _timeout, TimeUnit _unit) {
		token.setIsToShutdown();
		
		if(token.reservations.get() > 0){
			try {
				if(stageProcessDoneLatch.getCount() > 0)
					stageProcessDoneLatch.await(_timeout, _unit);
			} catch (Exception e) {
				;
			}
		}
		
		this.delegate.shutdown(_timeout, _unit);
	}

	@Override
	public void setNextPipe(RC_Pipe<?, ?> nextPipe) {
		this.delegate.setNextPipe(nextPipe);
	}

	@Override
	public void process(IN input) throws InterruptedException {
		Runnable task = new Runnable() {
			public void run() {
				int remainingReservations = -1;
				
				try {
					delegate.process(input);
				} catch (InterruptedException e) {
					;
				} finally{
					remainingReservations = token.reservations.decrementAndGet();
				}
				
				if(token.isToShutdown() && 0 == remainingReservations)
					stageProcessDoneLatch.countDown();
			}
		};
		
		executorSerivce.submit(task);
		token.reservations.incrementAndGet();
	}
	
	/**
	 * 线程池停止标志。
	 * 每个ExecutorService实例对应唯一的一个TerminationToken实例。
	 * 这里使用了Two-phase Termination模式（第5章）的思想来停止多个Pipe实例所共用的
	 * 线程池实例。
	 */
	private static class TerminationToken extends RC_TerminationToken {
		private final static ConcurrentMap<ExecutorService, TerminationToken> INSTANCES_MAP = new ConcurrentHashMap<>();

		// 私有构造器
		private TerminationToken() {

		}

		void setIsToShutdown() {
			this.toShutdown = true;
		}

		static TerminationToken newInstance(ExecutorService _executorSerivce) {
			TerminationToken token = INSTANCES_MAP.get(_executorSerivce);
			if (null == token) {
				token = new TerminationToken();
				//如果存在，就不put
				TerminationToken existingToken = INSTANCES_MAP.putIfAbsent(_executorSerivce, token);
				if (null != existingToken) {
					token = existingToken;
				}
			}
			return token;
		}
	}

}
