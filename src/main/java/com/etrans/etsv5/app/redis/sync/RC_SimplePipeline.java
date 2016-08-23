package com.etrans.etsv5.app.redis.sync;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_SimplePipeline.java  <br />
 * 说       明:  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月20日 下午2:14:16 <br />
 * 最后修改: 2016年8月20日 下午2:14:16 <br />
 * 修改历史:  <br />
 */
public class RC_SimplePipeline<T, OUT> extends RC_AbstractPipe<T, OUT> implements RC_Pipeline<T, OUT>{
	private final ExecutorService helperExecutor;
	private final Queue<RC_Pipe<?, ?>> pipes = new LinkedList<>();
	
	/**
	 * 构造函数
	 */
	public RC_SimplePipeline() {
		this(Executors.newSingleThreadExecutor(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "simplePipeline-help");
				t.setDaemon(true);
				return null;
			}
		}));
	}
	
	/**
	 * 构造函数
	 * @param _exe 
	 */
	public RC_SimplePipeline(ExecutorService _exe) {
		super();
		this.helperExecutor = _exe;		
	}
	
	@Override
	public void shutdown(long _timeout, TimeUnit _unit){
		RC_Pipe<?, ?> pipe;
		while(null != (pipe = pipes.poll()) )
			pipe.shutdown(_timeout, _unit);
		
		helperExecutor.shutdown();
	}

	@Override
	public void addPipe(RC_Pipe<?, ?> _pipe) {
		pipes.add(_pipe);
	}
	
	/**
	 * 基于工作者线程的Pipe实现类
	 * @param delegate
	 * @param workerCount
	 */
	public <INPUT, OUTPUT> void addAsWorkerThreadBasedPipe(RC_Pipe<INPUT, OUTPUT> delegate, int workerCount) {
		addPipe(new RC_WorkerThreadPipeDecorator<INPUT, OUTPUT>(delegate, workerCount));
	}

	/**
	 * 基于线程池的Pipe实现类。
	 * @param delegate
	 * @param executorSerivce
	 */
	public <INPUT, OUTPUT> void addAsThreadPoolBasedPipe(RC_Pipe<INPUT, OUTPUT> delegate, ExecutorService executorSerivce) {
		addPipe(new RC_ThreadPoolPipeDecorator<INPUT, OUTPUT>(delegate, executorSerivce));
	}

	@Override
	protected OUT doProcess(T _input) throws RC_PipeException {
		return null;
	}
	
	@Override
	public void process(T _input) throws InterruptedException{
		@SuppressWarnings("unchecked")
		RC_Pipe<T, ?> firstPipe = (RC_Pipe<T, ?>) pipes.peek();
		
		firstPipe.process(_input);
	}
	
	@Override
	public void init(RC_PipeContext _context){
		LinkedList<RC_Pipe<?, ?>> pipeList = (LinkedList<RC_Pipe<?, ?>>) pipes;
		RC_Pipe<?, ?> prevPipe = this;
		
		for(RC_Pipe<?, ?> pipe : pipeList){
			prevPipe.setNextPipe(pipe);
			prevPipe = pipe;
		}
		
		Runnable task = new Runnable() {
			public void run() {
				for(RC_Pipe<?, ?> pipe : pipes)
					pipe.init(_context);
			}
		};
		
		helperExecutor.submit(task);
	}
	
	public RC_PipeContext newDefaultPipelineContext() {
		return new RC_PipeContext() {
			public void handleError(final RC_PipeException exp) {
				helperExecutor.submit(new Runnable() {
					public void run() {
						exp.printStackTrace();
					}
				});
			}
		};
	}

}
