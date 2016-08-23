/**
 * 
 */
package com.etrans.etsv5.lib.redisSync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ObjCache.java  <br />
 * 说       明: etsv5-lib-defs  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016下午3:47:52  <br />
 * 最后修改: 2016下午3:47:52  <br />
 * 修改历史:   <br />
 */
public abstract class ObjCache {
	/**
     * 产生时间
     */
	private long genTime = System.currentTimeMillis();
	
	/**
	 * 对象类型
	 * @return
	 */
	public abstract ObjCacheType getObjType();
	
	
	/**
	 * 获取
	 * @return the genTime
	 */
	public long getGenTime() {
		return genTime;
	}
}
