package com.etrans.etsv5.app.redis.sync;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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
	private final HikariDataSource dataSource;
	

	/**
	 * 构造函数
	 * @param _config
	 */
	public MySQLHelper(Config _config) {		
		HikariConfig hkconfig = new HikariConfig();
		hkconfig.setJdbcUrl(_config.getDbConfig().getUrl());
		hkconfig.setUsername(_config.getDbConfig().user);
		hkconfig.setPassword(_config.getDbConfig().pass);
		hkconfig.addDataSourceProperty("cachePrepStmts", "true");
		hkconfig.addDataSourceProperty("prepStmtCacheSize", "250");
		hkconfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		//池中最小空闲链接数量
		hkconfig.setMinimumIdle(2);
		//池中最大链接数量
		hkconfig.setMaximumPoolSize(10);
		
		dataSource = new HikariDataSource(hkconfig);
	}
	
	/**
	 * 获取数据连接源
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException{
		Connection connection = null;
		if(null != dataSource)
			connection = dataSource.getConnection();
			
		return connection;
	}
	
}
