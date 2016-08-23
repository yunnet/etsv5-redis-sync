package com.etrans.etsv5.app.redis.sync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: IApp.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:23:38 <br />
 * 最后修改: 2016年8月23日 下午4:23:38 <br />
 * 修改历史: <br />
 */
public interface IApp<T extends AppConfig> {
    /**
     * 程序启动
     * @param _config 配置
     * @throws Exception
     */
    void run(T _config) throws Exception;
}
