package com.etrans.lib.net.tcp.netty;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrans.lib.net.tcp.AbstractTcpClient;
import com.etrans.lib.net.tcp.ITcpClient;
import com.etrans.lib.net.tcp.ITcpListener;
import com.etrans.lib.net.tcp.codec.CodecType;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: NettyTcpClient.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:28:53 <br />
 * 最后修改: 2016年8月23日 下午4:28:53 <br />
 * 修改历史: <br />
 */
public class NettyTcpClient extends AbstractTcpClient {
	private final static Logger logger = LoggerFactory.getLogger(NettyTcpClient.class.getSimpleName());

	private final NettyTcpClient self = this;
	private EventLoopGroup workGroup;
	private ChannelHandlerContext channelHandlerContext;
	private volatile InetSocketAddress localAddress = null;
	
	private Bootstrap b = null;
	private ChannelFuture future;
	
	private NettyInitializer initializer = null;

	private final AtomicBoolean connected = new AtomicBoolean(false);

	/**
	 * 构造函数
	 */
	public NettyTcpClient() {
		this(false);
	}

	/**
	 * 构造函数
	 * @param _line_delimiter
	 */
	public NettyTcpClient(boolean _line_delimiter) {
		this(null, _line_delimiter);
	}

	/**
	 * 构造函数
	 * @param _executor_service
	 * @param _line_delimiter
	 */
	public NettyTcpClient(ExecutorService _executor_service, boolean _line_delimiter) {
		super(_executor_service, _line_delimiter);
	}

	@Override
	public InetSocketAddress getLocalAddress() {
		return localAddress;
	}

	private EventLoopGroup getWorkGroop() {
		if (null == workGroup)
			workGroup = new NioEventLoopGroup();
		return workGroup;
	}

	public ChannelHandlerContext getContext() {
		return channelHandlerContext;
	}
	
	/**
	 * 侦听事件
	 */
	INettyListener listener = new INettyListener(){
		public void onChannelActive(ChannelHandlerContext _ctx) {
			channelHandlerContext = _ctx;
			connected.set(true);
			fireEventConn(self, true, null);
		}

		public void onChannelInactive(ChannelHandlerContext _ctx) {
			fireEventBrok(self);
			connected.set(false);
		}

		public void onChannelRead(ChannelHandlerContext _ctx, Object _data) {
			fireEventData(self, _data);
		}};
	
	/**
	 * 初始化
	 * @return
	 */
	public NettyInitializer getInitializer() {
		if (null == initializer) {
			initializer = new NettyInitializer(listener, isNeedLineDemimiter());
		}
		return initializer;
	}
	
	public void setCodec(CodecType _type){
		getInitializer().setCodectype(_type);
	}

	/**
	 * 连接重试
	 */
	@Override
	protected void tryConnnect() {
		if (null == b) {
			b = new Bootstrap();
			b.group(getWorkGroop())
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
			.handler(getInitializer());
		}

		SocketAddress address = getRemoteAddress();
		try {
			logger.info("try connect to server {}...", address);
			future = b.connect(address).sync();
			logger.info("connect to {} ok ", address);
		} catch (Exception e) {
			logger.error("failed on connect to server {{}}", e.getMessage());
			fireEventConn(this, false, e);
		}
	}

	/**
	 * 发送数据
	 */
	@Override
	public void doWrite(byte[] _buf, int _offset, int _len) {
		if (isActive()) {
			ByteBuf buf = Unpooled.copiedBuffer(_buf, _offset, _len);
			channelHandlerContext.writeAndFlush(buf);
		}
	}

	@Override
	public void writeAndFlush(Object msg) {
		if(isActive())
			channelHandlerContext.writeAndFlush(msg);
	}

	@Override
	public void onStop() {
		future.channel().close();
	}

	@Override
	public boolean isConnected() {
		return connected.get();
	}

	/**
	 * 设置缓存池大小
	 * @param _maxFrameLength the maxFrameLength to set
	 */
	public void setMaxFrameLength(int _maxFrameLength) {
		getInitializer().setMaxFrameLength(_maxFrameLength); 
	}

	/**
	 * 样例
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ITcpListener<ITcpClient> listener = new ITcpListener<ITcpClient>() {
			@Override
			public void onConn(ITcpClient _sender, boolean _success, Exception _e) {
				logger.info("connect is ok.");
			}

			@Override
			public void onBrok(ITcpClient _sender) {
				logger.info("connect is brok.");
			}

			@Override
			public void onData(ITcpClient _sender, Object _data) {
				byte[] bytes = (byte[])_data;
				String recv = new String(bytes);
				System.out.println(recv);
			}
		};

		NettyTcpClient client = new NettyTcpClient();
		client.setServer("127.0.0.1", 6000);
		client.addListener(listener);
		client.start();

		ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
		timer.scheduleAtFixedRate(()->{
			String data = "abc123";
			logger.info("send: {}", data);
			client.write(data);
		} , 1, 1, TimeUnit.SECONDS);
	}


}

