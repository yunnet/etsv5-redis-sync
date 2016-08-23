/**
 * 
 */
package com.etrans.etsv5.lib.redisSync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ObjCacheResponse.java  <br />
 * 说       明: etsv5-lib-defs  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016上午9:41:26  <br />
 * 最后修改: 2016上午9:41:26  <br />
 * 修改历史:   <br />
 */
public class ObjCacheResponse extends ObjCache {
	/**
     * 服务端回执编号
     */
	private int receiptID;
	
	/**
     * 用户编号
     */
	private int userID;
		
	/**
	 * 返回结果是否同步成功
	 */
	private boolean result;

	@Override
	public ObjCacheType getObjType() {
		return ObjCacheType.C_OCT_Response;
	}

	/**
	 * 获取 服务端回执编号
	 * @return the receiptID
	 */
	public int getReceiptID() {
		return receiptID;
	}

	/**
	 * 设置 服务端回执编号
	 * @param receiptID the receiptID to set
	 */
	public void setReceiptID(int receiptID) {
		this.receiptID = receiptID;
	}

	/**
	 * 获取用户编号
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * 设置用户编号
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * 获取返回结果是否同步成功
	 * @return the result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * 设置返回结果是否同步成功
	 * @param result the result to set
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

}
