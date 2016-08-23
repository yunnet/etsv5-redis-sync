package com.etrans.etsv5.app.redis.sync;

import java.util.concurrent.TimeUnit;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_AbstractPipe.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:22:48 <br />
 * 最后修改: 2016年8月23日 下午4:22:48 <br />
 * 修改历史: <br />
 */
public abstract class RC_AbstractPipe<IN, OUT> implements RC_Pipe<IN, OUT> {
	
	protected volatile RC_Pipe<?, ?> nextPipe = null;
	protected volatile RC_PipeContext pipeContext = null;

	public void init(RC_PipeContext _context){
		this.pipeContext = _context;
	}
	
	public void setNextPipe(RC_Pipe<?, ?> _nextPipe){
		this.nextPipe = _nextPipe;
	}
	
	public void shutdown(long _timeout, TimeUnit _unit){
		
	}
	
	/**
	 * 留给子类实现。用于子类实现其任务处理逻辑。
	 * @param _input  输入元素（任务）
	 * @return 任务的处理结果
	 * @throws RC_PipeException
	 */
	protected abstract OUT doProcess(IN _input) throws RC_PipeException;
	
	/**
	 * 处理任务
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void process(IN _input) throws InterruptedException{
		try {
			OUT out = doProcess(_input);
			if(null != nextPipe){
				if(null != out)
					((RC_Pipe<OUT, ?>) nextPipe).process(out);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupted();
		} catch (RC_PipeException e) {
			pipeContext.handleError(e);
		}
	}

}
