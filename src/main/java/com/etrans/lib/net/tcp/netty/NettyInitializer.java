package com.etrans.lib.net.tcp.netty;

import com.etrans.lib.net.tcp.codec.CodecType;
import com.etrans.lib.net.tcp.codec.kryo.KryoFrameDecoder;
import com.etrans.lib.net.tcp.codec.kryo.KryoFrameEncoder;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: NettyInitializer.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:27:05 <br />
 * 最后修改: 2016年8月23日 下午4:27:05 <br />
 * 修改历史: <br />
 */
public class NettyInitializer extends ChannelInitializer<SocketChannel> {
    private static final int IDEL_TIME_OUT = 100;
    private static final int READ_IDEL_TIME_OUT = 30;
    private static final int WRITE_IDEL_TIME_OUT = 40;

    private final INettyListener listener;
    private int maxFrameLength = 8192;
    private final boolean needLineDelimiter;
    
    private CodecType codectype = CodecType.BYTEARRAY;

    /**
     * 构造函数
     * @param _listener
     * @param _delimiter
     */
    public NettyInitializer(INettyListener _listener, boolean _delimiter){
        this.listener = _listener;
        this.needLineDelimiter = _delimiter;
    }

	/**
	 * 设置
	 * @param maxFrameLength the maxFrameLength to set
	 */
	public void setMaxFrameLength(int maxFrameLength) {
		this.maxFrameLength = maxFrameLength;
	}

	/**
	 * 设置
	 * @param codectype the codectype to set
	 */
	public void setCodectype(CodecType codectype) {
		this.codectype = codectype;
	}

	/**
	 * 获取编码器
	 */
	private ChannelOutboundHandler getChannelEncoderHandler() {
		switch (this.codectype) {
			case BYTEARRAY: return new ByteArrayEncoder();
			case KRYO:      return new KryoFrameEncoder();
			case AVRO:      return null; 
			case FASTJSON:  return null;
			case PROTOBUF:  return null;
			case THRIFT:    return null;
			case UNKNOWN:   return null;
			default: return null;
		}
	}

	/**
	 * 获取解码器
	 */
	private ChannelInboundHandler getChannelDecoderHandler() {
		switch (this.codectype) {
			case BYTEARRAY: return new ByteArrayDecoder();
			case KRYO:      return new KryoFrameDecoder();
			case AVRO:      return null; 
			case FASTJSON:  return null;
			case PROTOBUF:  return null;
			case THRIFT:    return null;
			case UNKNOWN:   return null;
			default: return null;
		}
	}

	@Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //实现心跳，空闲链接断开
        //pipeline.addLast("idleStateHandler", new IdleStateHandler(READ_IDEL_TIME_OUT, WRITE_IDEL_TIME_OUT, IDEL_TIME_OUT));
        
        if(this.needLineDelimiter)
            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(this.maxFrameLength, Delimiters.lineDelimiter()));

        pipeline.addLast("decoder", getChannelDecoderHandler());
        pipeline.addLast("encoder", getChannelEncoderHandler());

        pipeline.addLast("handler", new NettyHandler(listener));
    }
}