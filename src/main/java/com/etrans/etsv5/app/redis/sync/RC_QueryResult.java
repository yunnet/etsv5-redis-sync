package com.etrans.etsv5.app.redis.sync;

import java.util.Map;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RC_QueryResult.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月22日 下午2:05:24 <br />
 * 最后修改: 2016年8月22日 下午2:05:24 <br />
 * 修改历史: <br />
 */
public class RC_QueryResult implements RC_ITask{
	/**
	 * 任务ID
	 */
	private int taskID;
	
	/**
	 * 请求用户
	 */
	private int userID; 
	
	/**
	 * 查询结果集
	 */
	private Map<String, Map<String, String>> resultMap;

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
	 * 获取
	 * @return the resultMap
	 */
	public Map<String, Map<String, String>> getResultMap() {
		return resultMap;
	}

	/**
	 * 设置
	 * @param resultMap the resultMap to set
	 */
	public void setResultMap(Map<String, Map<String, String>> resultMap) {
		this.resultMap = resultMap;
	}

}
