package com.etrans.etsv5.app.redis.sync;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/** 
 * 工程名称: etsv5-redis-cacher  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: MySQLLoader.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年3月29日 下午2:10:32  <br />
 * 最后修改: 2016年3月29日 下午2:10:32  <br />
 * 修改历史:   <br />
 */
public class MySQLHelper {
	private final static Logger logger = LoggerFactory.getLogger(MySQLHelper.class.getSimpleName());

	private BoneCP connPool;
	

	/**
	 * 构造函数
	 * @param _dbconfig
	 */
	public MySQLHelper(Config _config) {
		BoneCPConfig bonecfg = new BoneCPConfig();
		bonecfg.setUsername(_config.getDbConfig().user);
		bonecfg.setPassword(_config.getDbConfig().pass);
		bonecfg.setJdbcUrl(_config.getDbConfig().getUrl());
		
		//数据库连接池的最小连接数
		bonecfg.setMinConnectionsPerPartition(5);
		//数据库连接池的最大连接数  
		bonecfg.setMaxConnectionsPerPartition(10);
		
		try {
			connPool = new BoneCP(bonecfg);
		} catch (SQLException e) {
			logger.error("new BoneCP err: ", e);
		}
	}


	/**
	 * 获取连接池
	 * @return the connPool
	 */
	public BoneCP getConnPool() {
		return connPool;
	}
	
}
