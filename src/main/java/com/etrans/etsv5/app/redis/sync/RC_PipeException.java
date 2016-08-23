package com.etrans.etsv5.app.redis.sync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_PipeException.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:20:45 <br />
 * 最后修改: 2016年8月23日 下午4:20:45 <br />
 * 修改历史: <br />
 */
public class RC_PipeException extends Exception {
	
	/**
	 * 抛出异常的Pipe实例。
	 */
	public final RC_Pipe<?, ?> sourcePipe;
	
	/**
	 * 抛出异常的Pipe实例在抛出异常时所处理的输入元素。
	 */
	public final Object input;

	/**
	 * @param _pipe
	 * @param _input
	 * @param _message
	 */
	public RC_PipeException(RC_Pipe<?, ?> _pipe, Object _input, String _message) {
		super(_message);
		this.sourcePipe = _pipe;
		this.input = _input;
	}
	
	public RC_PipeException(RC_Pipe<?, ?> _pipe, Object _input, String _message, Throwable _cause) {
		super(_message, _cause);
		this.sourcePipe = _pipe;
		this.input = _input;
	}
}