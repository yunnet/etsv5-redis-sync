/**
 * 
 */
package com.etrans.etsv5.lib.redisSync;

import com.etrans.etsv5.app.redis.sync.Utils;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RequestCache.java  <br />
 * 说       明: etsv5-lib-defs  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016下午3:42:35  <br />
 * 最后修改: 2016下午3:42:35  <br />
 * 修改历史:   <br />
 */
public class ObjCacheRequest extends ObjCache{
	/**
	 * 请求ID
	 */
	private int requestID;
	
	/**
     * 用户编号
     */
	private int userID;
	
	/**
	 * 表名
	 */
	private String tableName;
	
	/**
	 * 字段名
	 */
	private String fields;
	
	/**
	 * 查询条件
	 */
	private String condition;
	
	@Override
	public ObjCacheType getObjType() {
		return ObjCacheType.C_OCT_Request;
	}

	/**
	 * 获取请求ID
	 * @return the requestID
	 */
	public int getRequestID() {
		return requestID;
	}

	/**
	 * 设置请求ID
	 * @param requestID the requestID to set
	 */
	public void setRequestID(int requestID) {
		this.requestID = requestID;
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
	 * 获取表名
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * 设置表名
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 获取字段名
	 * @return the fields
	 */
	public String getFields() {
		return fields;
	}

	/**
	 * 设置字段名
	 * @param fields the fields to set
	 */
	public void setFields(String fields) {
		this.fields = fields;
	}

	/**
	 * 获取查询条件
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * 设置查询条件
	 * @param condition the condition to set
	 */
	public void setCondition(final String condition) {
		this.condition = condition;
	}
	
	@Override
	public String toString() {
		return String.format("%s, user:%d, id:%d, table: %s, fields:[%s], condition:[%s]", 
				Utils.DateTimeToStr(this.getGenTime()),
                this.userID,
                this.requestID,
                this.tableName,
                this.fields,
                this.condition
				);
	}

}
