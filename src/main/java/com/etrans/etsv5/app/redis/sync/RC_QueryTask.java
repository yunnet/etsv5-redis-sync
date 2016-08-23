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
	 * 请求用户
	 */
	private int userID; 
	
	/**
	 * 表名
	 */
	private String tablename;
	
	/**
	 * 字段名
	 */
	private String fields;
	
	/**
	 * 查询条件
	 */
	private String condition;
	
	/**
	 * 获取
	 * @return the taskID
	 */
	public int getTaskID() {
		return taskID;
	}

	/**
	 * 设置
	 * @param taskID the taskID to set
	 */
	public void setTaskID(int taskID) {
		this.taskID = taskID;
	}
	
	/**
	 * 获取
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * 设置
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}

	/**
	 * 设置表名
	 * @param tablename the tablename to set
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	/**
	 * 获取表名
	 * @return the tablename
	 */
	public String getTablename() {
		return tablename;
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
	public void setFields(final String fields) {
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

	/**
	 * 生成SQLText
	 * @return
	 */
	public String genSQLText(){
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		
		if(null != fields && !fields.isEmpty())
			sb.append(fields);
		else
			sb.append("*");
		
		sb.append(" FROM ").append(tablename);
		
		if(null != condition && !condition.isEmpty())
			sb.append(" WHERE ").append(condition);
		
		return sb.toString();
	}

	
}