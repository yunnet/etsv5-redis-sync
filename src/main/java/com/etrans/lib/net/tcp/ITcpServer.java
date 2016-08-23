package com.etrans.lib.net.tcp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ITcpServer.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:34:21 <br />
 * 最后修改: 2016年8月23日 下午4:34:21 <br />
 * 修改历史: <br />
 */
public interface ITcpServer{
    ExecutorService getExecutor();

    ITcpServer addListenPort(int _port);
    ITcpServer setListenPort(int _port);
    int getListenPort();
    int[] getListenPorts();
    SocketAddress[] getLocalAddressList();
    SocketAddress getLocalAddress();

    int getTimeout();
    void setTimeout(int _timeout);

    ITcpSessions getSessions();
    void addListener(ITcpListener<ITcpSession> _listener);

    void broadcast(ByteBuffer _bb);
    void broadcast(StringBuilder _bb);
    void broadcast(String _str);

    boolean isActive();
    void start() throws Exception;
    void setActive(boolean _b) throws Exception;
}
