package com.etrans.etsv5.app.redis.sync;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lambdaworks.redis.ClientOptions;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnectionPool;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.async.RedisAsyncCommands;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: RedisHelper.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:19:39 <br />
 * 最后修改: 2016年8月23日 下午4:19:39 <br />
 * 修改历史: <br />
 */
public class RedisHelper {
	private final static Logger logger = LoggerFactory.getLogger(RedisHelper.class.getSimpleName());

	private final RedisClient redisClient;
	private RedisConnectionPool<RedisAsyncCommands<String, String>> redisPool;
	private RedisAsyncCommands<String, String> asyncCommands;
	
	/**
	 * 构造函数
	 * @param _config
	 */
	public RedisHelper(Config _config){
		this(_config.getRedisHost(), _config.getRedisPort(), _config.getRedisPassword());
	}
	
	/**
	 * 构造函数
	 * @param _host
	 * @param _port
	 */
	public RedisHelper(final String _host, int _port) {
		this(_host, _port, "");
	}
	
	public RedisHelper(final String _host, int _port, final String _pwd){
		RedisURI redisURI = RedisURI.create(_host, _port);
		redisURI.setPassword(_pwd);
		
		redisClient = RedisClient.create(redisURI);
		redisClient.setDefaultTimeout(60, TimeUnit.SECONDS);
		redisClient.setOptions(ClientOptions.create());
		
		asyncCommands = redisClient.connect().async();
	}
	
	/**
	 * 发送数据到redis
	 * @param _key
	 * @param _data
	 */
	public void set(final String _key, final String _data){
		try {
			asyncCommands.set(_key, _data);
		} catch (Exception e) {
			logger.error("set err: ", e);
		}
	}
	
	public void hmset(final String key, Map<String, String> map){
		try {
			asyncCommands.hmset(key, map);
		} catch (Exception e) {
			logger.error("hmset err: ", e);
		}
	}

	/**
	 * 获取
	 * @return the redisPool
	 */
	public RedisConnectionPool<RedisAsyncCommands<String, String>> getRedisPool() {
		return redisPool;
	}

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		RedisHelper redisHelper = new RedisHelper("127.0.0.1", 6379, "123456");
		
		int i = 0;
		while(true){
			String data = "data=" + i; 
			redisHelper.set(i + "", data);
			
			System.out.println("data = " + data);
			i++;
			TimeUnit.SECONDS.sleep(1);
		}
	}

}
