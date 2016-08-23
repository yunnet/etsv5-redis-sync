package com.etrans.etsv5.app.redis.sync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: AppHelper.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:24:06 <br />
 * 最后修改: 2016年8月23日 下午4:24:06 <br />
 * 修改历史: <br />
 */
public class AppHelper{
	/**
	 * 启动
	 * @param _app
	 * @param _config
	 * @throws Exception
	 */
    public static <T extends AppConfig> void start(IApp<T> _app, T _config) throws Exception{
        _config.loadConfig();
        _app.run(_config);
    }

}
