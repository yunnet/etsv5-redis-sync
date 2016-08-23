package com.etrans.etsv5.app.redis.sync;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.etrans.lib.db.DBConfig;
import com.etrans.lib.db.DBDriver;

/** 
 * 工程名称: etsv5-redis-cacher  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: Config.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年3月29日 上午11:32:57  <br />
 * 最后修改: 2016年3月29日 上午11:32:57  <br />
 * 修改历史:   <br />
 */
public class Config extends AppConfig {
	private final static Logger logger = LoggerFactory.getLogger(Config.class.getSimpleName());
	
	private boolean redisEnable;
	private String redisHost;
	private int redisPort;
	private String redisPassword;
	
	/**
	 * 侦听端口
	 */
	private int listenPort;
	
	/**
	 * 定时执行任务的时间点： 0点
	 */
	private int timeAtDay;
	
	/**
	 * 强制检查是否在本地加载的范围内
	 */
	private boolean compulsiveCheck;
	
	private DBConfig dbConfig = new DBConfig(DBDriver.MYSQL);
	
	private final Map<String, Table> tableMap = new ConcurrentHashMap<>();

	/**
	 * 获取
	 * @return the listenPort
	 */
	public int getListenPort() {
		return listenPort;
	}

	/**
	 * 获取
	 * @return the tableMap
	 */
	public Map<String, Table> getTableMap() {
		return tableMap;
	}
	
	/**
	 * 获取
	 * @return the redisEnable
	 */
	public boolean isRedisEnable() {
		return redisEnable;
	}

	/**
	 * 获取
	 * @return the redisHost
	 */
	public String getRedisHost() {
		return redisHost;
	}

	/**
	 * 获取
	 * @return the redisPort
	 */
	public int getRedisPort() {
		return redisPort;
	}

	/**
	 * 获取
	 * @return the redisPassword
	 */
	public String getRedisPassword() {
		return redisPassword;
	}

	/**
	 * 获取
	 * @return the dbConfig
	 */
	public DBConfig getDbConfig() {
		return dbConfig;
	}

	/**
	 * 获取
	 * @return the timeAtDay
	 */
	public int getTimeAtDay() {
		return timeAtDay;
	}

	/**
	 * 获取
	 * @return the compulsiveCheck
	 */
	public boolean isCompulsiveCheck() {
		return compulsiveCheck;
	}

	@Override
	protected void loadAppConfig(JSONObject _root) throws Exception {
		this.listenPort = _root.getIntValue("listen");
		this.timeAtDay = _root.getIntValue("timeAtDay");
		this.compulsiveCheck = _root.getBooleanValue("compulsiveCheck");
		
		JSONObject redis_json = _root.getJSONObject("redis");
		redisEnable = redis_json.getBooleanValue("enable");
		redisHost = redis_json.getString("host");
		redisPort = redis_json.getIntValue("port");
		redisPassword = redis_json.getString("password");
		
		JSONObject mysql_json = _root.getJSONObject("mysql");
		if(mysql_json.getBooleanValue("enable")){
			dbConfig.host = mysql_json.getString("host");
			dbConfig.port = mysql_json.getIntValue("port");
			dbConfig.instance = mysql_json.getString("instance");
			dbConfig.databaseName = mysql_json.getString("databaseName");
			dbConfig.user = mysql_json.getString("user");
			dbConfig.pass = mysql_json.getString("password");
			dbConfig.threads = mysql_json.getIntValue("threads");
		}
		
		JSONArray array = mysql_json.getJSONArray("ddl");
		for(int i = 0, n = array.size(); i < n; i++){
			JSONObject js = (JSONObject) array.get(i);
			String table = js.getString("table");
			String fields = js.getString("fields");
			String condition = js.getString("condition");
			
			if(js.getBooleanValue("active")){
				addTable(table, fields, condition);
				logger.info(":::::: table:{} fields:[{}] condition:[{}]", table, fields, condition);
			}else
				logger.warn("skip table:{} fields:[{}] condition:[{}]", table, fields, condition);
		}
	}
	
	/**
	 * 增加查询表
	 * @param _table
	 * @param _fields
	 * @param _condition
	 */
	private void addTable(final String _table, final String _fields, final String _condition){
		if(!tableMap.containsKey(_table)){
			Table table = new Table(_table);
			table.setFields(_fields);
			table.setCondition(_condition);
			tableMap.put(_table, table);
		}
	}
}
