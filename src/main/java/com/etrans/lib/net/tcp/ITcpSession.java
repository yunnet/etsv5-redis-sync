package com.etrans.lib.net.tcp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ITcpSession.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:35:12 <br />
 * 最后修改: 2016年8月23日 下午4:35:12 <br />
 * 修改历史: <br />
 */
public interface ITcpSession {
    <T> T getAttr();
    <T> T setAttr(T _data);

    ITcpServer getServer();

    SocketAddress getLocalAddress();
    SocketAddress getRemoteAddress();
    long lastRecvTicks();
    void touchAliveTime();

    long getSessionSeq();
    String getSessionKey();
    void setSessionKey(String sessionKey);

    void write(String _str);
    void write(StringBuilder _buf);
    void write(ByteBuffer _bb);
    void write(Object msg);

    boolean isClosed();
    void close(String _cause);
    void close();
}
