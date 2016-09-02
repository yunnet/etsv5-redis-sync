package com.etrans.etsv5.app.redis.sync;

/** 
 * 工程名称: etsv5-redis-cacher  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: TaskQuery.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年3月29日 下午1:00:07  <br />
 * 最后修改: 2016年3月29日 下午1:00:07  <br />
 * 修改历史:   <br />
 */
public class RC_QueryTask implements RC_ITask{
	/**
	 * 任务ID
	 */
	private int taskID;
	
	/**
	 * 请求用户ID
	 */
	private int userID; 
	
	/**
	 * 查询语句
	 */
	private String sqlText;
	
	/**
	 * 前缀名
	 */
	private String prefix;
	
	
	
	/**
	 * 获取任务ID
	 * @return the taskID
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * 设置任务ID
	 * @param taskID the taskID to set
	 */
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	
	/**
	 * 获取请求用户ID
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * 设置请求用户ID
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * 获取查询语句
	 * @return the sqlText
	 */
	public String getSqlText() {
		return sqlText;
	}

	/**
	 * 设置查询语句
	 * @param sqlText the sqlText to set
	 */
	public void setSqlText(final String sqlText) {
		this.sqlText = sqlText;
	}

	/**
	 * 获取前缀名
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * 设置前缀名
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
}