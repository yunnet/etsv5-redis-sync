package com.etrans.lib.net.tcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: AbstractTcpServer.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:29:32 <br />
 * 最后修改: 2016年8月23日 下午4:29:32 <br />
 * 修改历史: <br />
 */
public abstract class AbstractTcpServer implements ITcpServer {
	private final static Logger logger = LoggerFactory.getLogger(AbstractTcpServer.class.getSimpleName());

	private static int serverSeq = 0;
	
	private final AtomicLong childSeq;
	private final AtomicBoolean active;
	private final TcpListeners<ITcpSession> tcpListeners;
	protected final ITcpSessions sessions;
	private final boolean needLineDelimiter;
	private volatile int timeout = 0;

	private AsynchronousChannelGroup asyncChannelGroup;
	private ExecutorService executor = null;
	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	private final List<Integer> localPorts = new ArrayList<>();
	private final List<SocketAddress> localAddress = new ArrayList<>();

	/**
	 * 构造函数
	 * @param _executor
	 * @param _channel_group
	 * @param _line_delimiter
	 */
	public AbstractTcpServer(ExecutorService _executor, AsynchronousChannelGroup _channel_group, boolean _line_delimiter) {
		this.executor = _executor;
		this.asyncChannelGroup = _channel_group;
		this.needLineDelimiter = _line_delimiter;
		
		active = new AtomicBoolean(false);
		tcpListeners = new TcpListeners<>();
		sessions = new TcpSessions(this);
		childSeq = new AtomicLong(0);
	}

	public AbstractTcpServer(boolean _line_delimiter) {
		this(null, null, _line_delimiter);
	}

	public AbstractTcpServer() {
		this(false);
	}
	
	protected static int getServerSeq(){
		return serverSeq++ & 0xFF;
	}

	/**
	 * 获取总共连接次数，Max(long)
	 * @return
	 */
	protected long getNewChildSeq() {
		return childSeq.getAndIncrement();
	}

	protected boolean isNeedLineDelimiter() {
		return needLineDelimiter;
	}

	protected AsynchronousChannelGroup getAsyncChannelGroup() throws Exception {
		if (null == asyncChannelGroup)
			asyncChannelGroup = AsynchronousChannelGroup.withThreadPool(getExecutor());
		return asyncChannelGroup;
	}

	public ExecutorService getExecutor() {
		if (null == executor)
			executor = Executors.newCachedThreadPool();
		return executor;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int _timeout) {
		timeout = _timeout;
	}

	public ITcpServer setListenPort(int _port) {
		addListenPort(_port);
		return this;
	}

	public ITcpServer addListenPort(int _port) {
		logger.info("addListenPort({})", _port);
		assert (!active.get());
		if(!localPorts.contains(_port)) {
			localPorts.add(_port);
			localAddress.add( new InetSocketAddress(_port) );
		}
		return this;
	}

	public int getListenPort() {
		return localPorts.get(0);
	}

	public int[] getListenPorts(){
		int[] res = new int[localPorts.size()];
		for(int i=0; i<localPorts.size(); i++)
			res[i] = localPorts.get(i);
		return res;
	}

	public SocketAddress getLocalAddress() {
		return localAddress.get(0);
	}

	public SocketAddress[] getLocalAddressList(){
		SocketAddress[] res = new SocketAddress[localAddress.size()];
		for(int i = 0; i < localAddress.size(); i++)
			res[i] = localAddress.get(i);
		return res;
	}

	public final void addListener(ITcpListener<ITcpSession> _listener) {
		logger.info("addListener {}", _listener);
		tcpListeners.addListener(_listener);
	}

	private void fireEventBrok(ITcpSession _session) {
		if (null != _session) {
			tcpListeners.onBrok(_session);
			sessions.delSession(_session);
		}
	}

	public void fireEventLine(ITcpSession _session, byte[] _data) {
		_session.touchAliveTime();
		tcpListeners.onData(_session, _data);
	}

	public void fireEventData(ITcpSession _session,  Object _data) {
		_session.touchAliveTime();
		tcpListeners.onData(_session, _data);
	}

	protected void addSession(ITcpSession _session) {
		if (null != _session) {
			sessions.addSession(_session);
			tcpListeners.onConn(_session, true, null);
		}
	}

	protected void removeSession(final String _key) {
		removeSession(sessions.getSession(_key));
	}

	protected void removeSession(ITcpSession _session) {
		if (null != _session) {
			fireEventBrok(_session);
			sessions.delSession(_session);
		}
	}

	public ITcpSessions getSessions() {
		return sessions;
	}

	public void broadcast(ByteBuffer _bb) {
		Iterator<ITcpSession> iterator = sessions.getIterator();
		while (iterator.hasNext()) {
			ITcpSession session = iterator.next();
			session.write(_bb);
		}
	}

	public void broadcast(StringBuilder _bb) {
		broadcast(_bb.toString());
	}

	public void broadcast(final String _str) {
		ByteBuffer bb = ByteBuffer.wrap(_str.getBytes());
		broadcast(bb);
	}

	public boolean isActive() {
		return active.get();
	}

	private void checkIdleSessions() {
		Iterator<ITcpSession> iterator = sessions.getIterator();
		if (timeout < 1 || (!iterator.hasNext()))
			return;
		
		long now_time = System.currentTimeMillis();
		while (iterator.hasNext()) {
			ITcpSession session = iterator.next();
			if (!session.isClosed()) {
				long idle_secs = (now_time - session.lastRecvTicks()) / 1000;
				if (idle_secs > timeout)
					session.close(String.format("idle timeout secs=%d", idle_secs));
			}
		}
	}

	public void setActive(boolean _active) throws Exception {
		logger.info("setActive({})", _active);
		if (active.get() != _active) {
			active.set(_active);

			if (_active) {
				if (localPorts.size() > 0) {
					doStart();
					timer.scheduleAtFixedRate(()-> checkIdleSessions(), 10, 1, TimeUnit.SECONDS);
				} else 
					throw new Exception("listen port not set");
			} else {
				timer.shutdown();
				doStop();
			}
		}
	}
	
	public void start() throws Exception{
		setActive(true);
	}

	protected abstract void doStart() throws Exception;

	protected abstract void doStop();

}
