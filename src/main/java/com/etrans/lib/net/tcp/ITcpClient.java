package com.etrans.lib.net.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ITcpClient.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:33:56 <br />
 * 最后修改: 2016年8月23日 下午4:33:56 <br />
 * 修改历史: <br />
 */
public interface ITcpClient{
    ExecutorService getExecutor();

    <T> T getCustomData();
    <T> T setCustomData(T _custom);

    void setServer(String _host, int _port);
    String getServerKey();

    InetSocketAddress getRemoteAddress();
    InetSocketAddress getLocalAddress();

    void setActive(boolean _b);
    void setReconnInterval(int _seconds);

    boolean isActive();
    boolean isConnected();

    <T> void addListener(ITcpListener<T> _listener);

    void write(String _str);
    void write(StringBuilder _buf);
    void write(byte[] _buf);
    void write(byte[] _buf, int _len);
    void write(byte[] _buf, int _offset, int _len);
    void write(Object msg);
}
