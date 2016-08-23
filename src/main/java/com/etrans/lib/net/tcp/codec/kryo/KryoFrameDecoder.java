package com.etrans.lib.net.tcp.codec.kryo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/** 
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: KryoFrameEncoder.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月13日 下午8:16:31  <br />
 * 最后修改: 2016年6月13日 下午8:16:31  <br />
 * 修改历史:   <br />
 */
public class KryoFrameDecoder extends LengthFieldBasedFrameDecoder {
	private final KryoDecoder decoder;

	/**
	 * 构造函数
	 */
	public KryoFrameDecoder(){
		this(1024 * 1024,    //maxFrameLength      表示信息最大长度
			 4,              //lengthFieldOffset   表示长度位置(起始位置)偏移量
			 4,              //lengthFieldLength   表示长度占的字节
			 -8,             //lengthAdjustment    表示长度调节值，在总长被定义为包含包头长度时，修正信息长度
			 0               //initialBytesToStrip 表示需要跳过的字节数
		     );
	}

	/**
	 * 构造函数
	 * @param maxFrameLength    表示信息最大长度
	 * @param lengthFieldOffset 表示长度位置(起始位置)偏移量
	 * @param lengthFieldLength 表示长度占的字节
	 */
	public KryoFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
		this(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0);
	}

	/**
	 * 构造函数
	 * @param maxFrameLength      表示信息最大长度
	 * @param lengthFieldOffset   表示长度位置(起始位置)偏移量
	 * @param lengthFieldLength   表示长度占的字节
	 * @param lengthAdjustment    表示长度调节值，在总长被定义为包含包头长度时，修正信息长度
	 * @param initialBytesToStrip 表示需要跳过的字节数
	 */
	public KryoFrameDecoder(int maxFrameLength,	int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
		this.decoder = new KryoDecoder();
	}

	
	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
		ByteBuf buf = (ByteBuf) super.decode(ctx, in);
		if(null == buf)
			return null;

		KryoHeader header = new KryoHeader();
		header.setCrcCode(buf.readInt());
		if(0xABEF0101 != header.getCrcCode())
			return null;

		header.setLength(buf.readInt());
		header.setType(buf.readByte());

		KryoMessage message = new KryoMessage();
		if(buf.readableBytes() > 4)
			message.setBody(decoder.decode(buf));
		
		message.setHeader(header);
		return message;
	}

}
