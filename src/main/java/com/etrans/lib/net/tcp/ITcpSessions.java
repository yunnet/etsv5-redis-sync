package com.etrans.lib.net.tcp;

import java.util.Iterator;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ITcpSessions.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:35:22 <br />
 * 最后修改: 2016年8月23日 下午4:35:22 <br />
 * 修改历史: <br />
 */
public interface ITcpSessions {
    <T> ITcpSession getSession(T _key);
    void addSession(ITcpSession _session);
    void delSession(ITcpSession _session);
    Iterator<ITcpSession> getIterator();
}
