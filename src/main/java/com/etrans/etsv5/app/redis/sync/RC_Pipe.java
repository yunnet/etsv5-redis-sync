package com.etrans.etsv5.app.redis.sync;

import java.util.concurrent.TimeUnit;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_Pipe.java  <br />
 * 说       明: etsv5-redis-cacher  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016下午2:21:40  <br />
 * 最后修改: 2016下午2:21:40  <br />
 * 修改历史:   <br />
 */

public interface RC_Pipe<IN, OUT>{
	/**
	 * 初始化当前Pipe实例对外提供的服务。
	 * @param pipeCtx
	 */
	void init(RC_PipeContext pipeCtx);

	/**
	 * 停止当前Pipe实例对外提供的服务。
	 * @param timeout 超时
	 * @param unit 时间单位
	 */
	void shutdown(long timeout, TimeUnit unit);
	
	/**
	 * 设置当前Pipe实例的下一个Pipe实例。
	 * @param nextPipe 下一个Pipe实例
	 */
	void setNextPipe(RC_Pipe<?, ?> nextPipe);
	
	/**
	 * 对输入元素进行处理，并将处理结果作为下一个Pipe实例的输入。
	 * @param input
	 * @throws InterruptedException
	 */
	void process(IN input) throws InterruptedException;

}
