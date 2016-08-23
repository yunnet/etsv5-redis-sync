package com.etrans.etsv5.app.redis.sync;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: App.java  <br />
 * 说       明: etsv5-redis-cacher  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016下午5:48:22  <br />
 * 最后修改: 2016下午5:48:22  <br />
 * 修改历史:   <br />
 */
public class App implements IApp<Config>{

	@Override
	public void run(Config _config) throws Exception {
		new Facade(_config).start();
	}
	
	public static void main(String[] args) {
//		InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
		Level level = Level.toLevel(Level.INFO_INT);
		LogManager.getRootLogger().setLevel(level);
		
		try {
			AppHelper.start(new App(), new Config());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
