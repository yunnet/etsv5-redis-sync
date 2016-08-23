package com.etrans.lib.net.tcp;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: TcpListenerAdapter.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:35:49 <br />
 * 最后修改: 2016年8月23日 下午4:35:49 <br />
 * 修改历史: <br />
 */
public class TcpListenerAdapter<T> implements ITcpListener<T> {

    public void onConn(T _sender, boolean _success, Exception _e){}

    public void onBrok(T _sender){}

    public void onData(T _sender, Object _data){}
    
}
