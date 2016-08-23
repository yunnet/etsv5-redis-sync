package com.etrans.lib.net.tcp.codec.kryo;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

/** 
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: KryoFrameEncoder.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月13日 下午9:02:30  <br />
 * 最后修改: 2016年6月13日 下午9:02:30  <br />
 * 修改历史:   <br />
 */
public class KryoFrameEncoder extends MessageToMessageEncoder<KryoMessage>{
	private final KryoEncoder encoder;

	/**
	 * 构造函数
	 */
	public KryoFrameEncoder() {
		encoder = new KryoEncoder();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, KryoMessage msg, List<Object> out) throws Exception {
		if(null == msg || null == msg.getHeader()){
			throw new Exception("The encode message is null.");
		}
		
		ByteBuf sendBuf = Unpooled.buffer();
		sendBuf.writeInt(msg.getHeader().getCrcCode());
		sendBuf.writeInt(msg.getHeader().getLength());
		sendBuf.writeByte(msg.getHeader().getType());

		if (msg.getBody() != null) {
			encoder.encode(msg.getBody(), sendBuf);
        }
		
		int readable = sendBuf.readableBytes();
		sendBuf.setInt(4, readable);
		
		out.add(sendBuf);
	}

}
