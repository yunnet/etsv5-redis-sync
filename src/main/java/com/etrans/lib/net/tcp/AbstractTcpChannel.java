package com.etrans.lib.net.tcp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: AbstractTcpChannel.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:29:08 <br />
 * 最后修改: 2016年8月23日 下午4:29:08 <br />
 * 修改历史: <br />
 */
public abstract class AbstractTcpChannel implements ITcpSession {
    private final static Logger logger = LoggerFactory.getLogger(AbstractTcpChannel.class);
    
    private final ITcpServer server;
    private final long sessionSeq;
    private String sessionKey;

	private final SocketAddress localAddress;
    private final SocketAddress remoteAddress;
    private Object attr;

    final AtomicLong lastRecvTicks;

    /**
     * 构造函数
     * @param _server 
     * @param _local
     * @param _remote
     * @param _seq
     */
    public AbstractTcpChannel(ITcpServer _server, SocketAddress _local, SocketAddress _remote, long _seq){
    	this.server = _server;
        this.localAddress = _local;
        this.remoteAddress = _remote;
        this.sessionSeq = _seq;
        
        this.sessionKey = String.format("%s-%s", _local, _remote); // why delete ???
        this.attr = null;
        this.lastRecvTicks = new AtomicLong(System.currentTimeMillis());
    }

    @Override
    protected void finalize() throws Throwable{
        logger.debug("finalize {}", this);
        super.finalize();
    }

    public <T> T getAttr(){
        return (T)attr;
    }

    public <T> T setAttr(T _data){
        T old = (T)attr;
        attr = _data;
        return old;
    }

    public long lastRecvTicks(){
        return lastRecvTicks.get();
    }
    
    public void touchAliveTime() {
        this.lastRecvTicks.set(System.currentTimeMillis());
     }

    public ITcpServer getServer(){
        return server;
    }

    /**
     * 获取远程地址
     */
    public SocketAddress getRemoteAddress(){
        return remoteAddress;
    }

    /**
     * 获取本地地址
     */
    public SocketAddress getLocalAddress(){
        return localAddress;
    }

    /**
     * 获取 sessionKey
     */
    public String getSessionKey(){
        return sessionKey;
    }
    
    /**
	 * 设置 sessionKey
	 * @param sessionKey the sessionKey to set
	 */
	public void setSessionKey(final String sessionKey) {
		this.sessionKey = sessionKey;
	}

    public long getSessionSeq(){
        return sessionSeq;
    }

    public void write(byte[] _buf, int _offset, int _len){
        doWrite(_buf, _offset, _len);
    }

    public void write(ByteBuffer _bb){
        write(_bb.array(), _bb.position(), _bb.limit());
    }

    public void write(final String _str){
        byte[] buf = _str.getBytes();
        write(buf, 0, buf.length);
    }

    public void write(Object msg){
        this.writeAndFlush(msg);
    }

    public void write(StringBuilder _buf){
        write(_buf.toString());
    }

    public void close(){
        close(null);
    }
    
    public void close(final String _cause){
        logger.debug("force close {} cause{{}}", this.sessionKey, _cause);
        doClose(_cause);
    }

    public boolean isClosed(){
        return getClosed();
    }

    protected abstract void doWrite(byte[] _buf, int _offset, int _len);
    protected abstract void writeAndFlush(Object msg);
    protected abstract void doClose(String _cause);
    protected abstract boolean getClosed();

    @Override
    public String toString(){
        return getClass().getName() + "@" + Integer.toHexString(hashCode()) + " " + sessionKey;
    }
}
