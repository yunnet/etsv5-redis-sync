package com.etrans.etsv5.app.redis.sync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_PipeContext.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:20:51 <br />
 * 最后修改: 2016年8月23日 下午4:20:51 <br />
 * 修改历史: <br />
 */
public interface RC_PipeContext {
	
	/**
	 * 用于对处理阶段抛出的异常进行处理.
	 * @param exp
	 */
	void handleError(RC_PipeException exp);

}
