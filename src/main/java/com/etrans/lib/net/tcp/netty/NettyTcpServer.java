package com.etrans.lib.net.tcp.netty;

import java.net.SocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrans.lib.net.tcp.AbstractTcpServer;
import com.etrans.lib.net.tcp.ITcpListener;
import com.etrans.lib.net.tcp.ITcpServer;
import com.etrans.lib.net.tcp.ITcpSession;
import com.etrans.lib.net.tcp.codec.CodecType;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.AttributeKey;

/**
 * 版权所有 (C) 2016 e-Trans Company <br />
 * 单元名称: NettyTcpServer.java <br />
 * 说 明: <br />
 * 作 者: mous <br />
 * 创建时间: 2014年7月23日 下午4:42:50 <br />
 * 最后修改: 2016年6月3日 下午4:42:50 <br />
 * 修改历史: <br />
 */
public class NettyTcpServer extends AbstractTcpServer {
	private final static Logger logger = LoggerFactory.getLogger(NettyTcpServer.class.getSimpleName());

	private final ITcpServer self = this;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workGroup;
	private ServerBootstrap b = null;
	private NettyInitializer initializer = null;
	
	/**
	 * 构造函数
	 * @param _line_delimiter  换行
	 */
	public NettyTcpServer(boolean _line_delimiter) {
		this(null, null, _line_delimiter);
	}

	public NettyTcpServer(ExecutorService _executor, AsynchronousChannelGroup _channel_group, boolean _line_delimiter) {
		super(_executor, _channel_group, _line_delimiter);
	}

	/**
	 * 设置缓存池大小
	 * @param _maxFrameLength the maxFrameLength to set
	 */
	public void setMaxFrameLength(int _maxFrameLength) {
		getInitializer().setMaxFrameLength(_maxFrameLength);
	}

	private String getSessionKey(ChannelHandlerContext _ctx) {
		return String.format("%s-%s", _ctx.channel().localAddress(), _ctx.channel().remoteAddress());
	}
	
	/**
	 * @return
	 */
	private static final AttributeKey<ITcpSession> SESSION_OBJ = AttributeKey.newInstance("SESSION_OBJ" + AbstractTcpServer.getServerSeq());
	private final INettyListener listener = new INettyListener() {
		// create
		public void onChannelActive(ChannelHandlerContext _ctx) {
			NettyTcpChannel session = new NettyTcpChannel(self, getNewChildSeq(), _ctx);
			session.setSessionKey(getSessionKey(_ctx));

			addSession(session);
			_ctx.channel().attr(SESSION_OBJ).set(session);
		}

		// remove
		public void onChannelInactive(ChannelHandlerContext _ctx) {
			removeSession(getSessionKey(_ctx));
		} 

		// recv data
		public void onChannelRead(ChannelHandlerContext _ctx, Object _data) {
			ITcpSession session = _ctx.channel().attr(SESSION_OBJ).get();
			if (null != session) {
				fireEventData(session, _data);
			}
		}
	}; 

	private NettyInitializer getInitializer() {
		if (null == initializer){
			initializer = new NettyInitializer(listener, isNeedLineDelimiter());
		}
		return initializer;
	}
	
	public void setCodec(CodecType _type){
		getInitializer().setCodectype(_type);
	}

	private EventLoopGroup getBossGroup(int _num) {
		if (null == bossGroup)
			bossGroup = new NioEventLoopGroup(_num);
		return bossGroup;
	}

	private EventLoopGroup getWorkGroup() {
		if (null == workGroup)
			workGroup = new NioEventLoopGroup();
		return workGroup;
	}

	public void tryListen() throws Exception {
		SocketAddress[] addresses = getLocalAddressList();

		if (null == b) {
			b = new ServerBootstrap();
			b.group(getBossGroup(addresses.length), getWorkGroup());
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 100);
			b.childHandler(getInitializer());
		}

		for (int i = 0, n = addresses.length; i < n; i++) {
			final SocketAddress address = addresses[i];
			logger.info(":::::: start the server on {}", address);

			ChannelFuture f = b.bind(address);
			f.addListener(new ChannelFutureListener() {
				public void operationComplete(ChannelFuture _future) throws Exception {
					if (_future.isSuccess())
						logger.info("listen on {} ok.", address);
					else {
						logger.error("listen on {} err.", address);

						_future.cause().printStackTrace();
					}
				}
			});

			try {
				f.sync();
			} catch (Exception e) {
				logger.error("bf.sync err: ", e);
			}
		}
	}

	private final Runnable launcher = new Runnable() {
		public void run() {
			try {
				tryListen();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	protected void doStart() throws Exception {
		getExecutor().execute(launcher);
	}

	@Override
	protected void doStop() {
		if (bossGroup != null) {
			bossGroup.shutdownGracefully();
			bossGroup = null;
		}

		if (workGroup != null) {
			workGroup.shutdownGracefully();
			workGroup = null;
		}
	}

	public void start() throws Exception {
		this.setActive(true);
	}

	public void stop() throws Exception {
		this.setActive(false);
	}

	/**
	 * 样例
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ITcpListener<ITcpSession> listener = new ITcpListener<ITcpSession>() {
			@Override
			public void onConn(ITcpSession _sender, boolean _sucess, Exception _e) {
				logger.info("onConn {}", _sender.getSessionKey());
			}

			@Override
			public void onBrok(ITcpSession _sender) {
				logger.info("onBrok {}", _sender.getSessionKey());
			}

			@Override
			public void onData(ITcpSession _sender, Object _data) {
				logger.info("{} recv: {}", _sender.getSessionKey(),  new String((byte[])_data));
			}
		};

		NettyTcpServer server = new NettyTcpServer(false);
		server.addListener(listener);
		server.setTimeout(300);
		server.setListenPort(6000);// .addListenPort(5000);
		server.start();
	}


}
