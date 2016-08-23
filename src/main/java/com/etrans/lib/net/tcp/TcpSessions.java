package com.etrans.lib.net.tcp;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: TcpSessions.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:36:29 <br />
 * 最后修改: 2016年8月23日 下午4:36:29 <br />
 * 修改历史: <br />
 */
public class TcpSessions implements ITcpSessions{
    private final Map<Object, ITcpSession> sessionMap = new ConcurrentHashMap<>();
    private final List<ITcpSession> list = new LinkedList<>();
    private final ITcpServer server;

    /**
     * 构造函数
     * @param _server
     */
    public TcpSessions(ITcpServer _server){
        server = _server;
    }

    public <T> ITcpSession getSession(T _key){
        return sessionMap.get(_key);
    }

    public void addSession(ITcpSession _session){
        String key = _session.getSessionKey();
        if(null == sessionMap.get(key)){
            sessionMap.put(key, _session);
	        list.add(_session);
        }
    }

    public void delSession(ITcpSession _session){
        sessionMap.remove(_session.getSessionKey());
	    list.remove(_session);
    }

    public Iterator<ITcpSession> getIterator(){
        return list.iterator();
    }
}
