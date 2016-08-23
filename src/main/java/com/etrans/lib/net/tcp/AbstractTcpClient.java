package com.etrans.lib.net.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: AbstractTcpClient.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:29:19 <br />
 * 最后修改: 2016年8月23日 下午4:29:19 <br />
 * 修改历史: <br />
 */
public abstract class AbstractTcpClient implements ITcpClient {
	private final static Logger logger = LoggerFactory.getLogger(AbstractTcpClient.class);

	private final TcpListeners tcpListeners;
	private final AtomicBoolean active;
	private final AtomicBoolean connecting;
	private final AtomicReference customData;
	private final boolean needLineDemimiter;

	private ExecutorService executor = null;
	private InetSocketAddress remoteAddress = null;
	private String serverKey;
	private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

	/**
	 * 构造函数
	 * @param _executor_service
	 * @param _line_delimiter 
	 */
	public AbstractTcpClient(ExecutorService _executor_service, boolean _line_delimiter) {
		this.executor = _executor_service;
		this.needLineDemimiter = _line_delimiter;
		
		tcpListeners = new TcpListeners<ITcpClient>();
		customData = new AtomicReference();

		active = new AtomicBoolean(false);
		connecting = new AtomicBoolean(false);
	}

	public AbstractTcpClient() {
		this(false);
	}
	
	public AbstractTcpClient(boolean _line_delimiter) {
		this(null, _line_delimiter);
	}

	public final void setServer(final String _host, int _port) {
		assert (!active.get());
		String key = buildKey(_host, _port);
		
		if (!key.equals(serverKey)) {
			this.serverKey = key;
			this.remoteAddress = new InetSocketAddress(_host, _port);
		}
	}

	public static String buildKey(final String _host, int _port) {
		return String.format("%s:%d", _host, _port);
	}

	public final String getServerKey() {
		return serverKey;
	}

	public <T> T getCustomData() {
		return (T) customData.get();
	}

	public <T> T setCustomData(T _data) {
		return (T) customData.getAndSet(_data);
	}

	public final InetSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	public final boolean isActive() {
		return active.get();
	}

	public abstract boolean isConnected();
	public abstract InetSocketAddress getLocalAddress();
	protected abstract void doWrite(byte[] _buf, int _offset, int _len);
	protected abstract void writeAndFlush(Object msg);
	protected abstract void tryConnnect();

	protected void onStart() {
	}

	protected void onStop() {
	}

	private final Runnable connTask = new Runnable() {
		public void run() {
			if (isActive()) {
				logger.debug("tryConnect...");
				tryConnnect();
			}
			connecting.set(false);
		}
	};

	private void checkConnTimer() {
		if (isActive()) {
			if (!isConnected() && !connecting.getAndSet(true))
				getExecutor().execute(connTask);
		}
	};

	/**
	 * 重链时间间隔，单位，秒
	 */
	private int reconnInterval = 3;
	public void setReconnInterval(int _seconds) {
		reconnInterval = _seconds < 1 ? 1 : _seconds;
	}

	public void setActive(boolean _b) {
		logger.info("setActive({})", _b);

		if (active.get() != _b) {
			active.set(_b);

			if (_b) {
				onStart();
				timer.scheduleAtFixedRate(()-> checkConnTimer(), 1, reconnInterval, TimeUnit.SECONDS);
			} else {
				timer.shutdown();
				onStop();
			}
		}
	}

	public void start(){
		setActive(true);
	}
	
	public void stop(){
		setActive(false);
	}

	protected boolean isNeedLineDemimiter() {
		return needLineDemimiter;
	}

	public ExecutorService getExecutor() {
		if (null == executor)
			executor = Executors.newCachedThreadPool();
		return executor;
	}

	/**
	 * 注册事件监听器*
	 * @param _listener listener
	 * @return self
	 */
	public <T> void addListener(ITcpListener<T> _listener) {
		tcpListeners.addListener(_listener);
	}

	/**
	 * 响应网络连接成功事件
	 * @param _sender: the sender
	 * @param _sucess: true | false
	 * @param _e: exception
	 */
	protected <T> void fireEventConn(T _sender, boolean _sucess, Exception _e) {
		tcpListeners.onConn(_sender, _sucess, _e);
	}

	/**
	 * 响应网络断开事件
	 * @param _sender: sender
	 */
	protected <T> void fireEventBrok(T _sender) {
		tcpListeners.onBrok(_sender);
	}

	protected <T> void fireEventData(T _sender, Object _data) {
		try {
			tcpListeners.onData(_sender, _data);
		} catch (Exception e) {
			logger.error("fireEventData ", e);
		}
	}

	public final void write(ByteBuffer _bb) {
		write(_bb.array(), 0, _bb.limit());
	}

	public final void write(final String _str) {
		byte[] buf = _str.getBytes();
		write(buf, 0, buf.length);
	}

	public final void write(byte[] _buf) {
		write(_buf, 0, _buf.length);
	}

	public final void write(byte[] _buf, int _len) {
		write(_buf, 0, _len);
	}

	public final void write(StringBuilder _sb) {
		write(_sb.toString());
	}

	public final void write(byte[] _buf, int _offset, int _len) {
		if (isConnected())
			doWrite(_buf, _offset, _len);
		else
			logger.error("write on closed connection");
	}

	public final void write(Object msg){
		if(isConnected())
			writeAndFlush(msg);
		else
			logger.error("write on closed connection");
	}

}
