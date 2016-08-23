package com.etrans.lib.net.tcp.netty;

import java.util.concurrent.atomic.AtomicBoolean;

import com.etrans.lib.net.tcp.AbstractTcpChannel;
import com.etrans.lib.net.tcp.ITcpServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: NettyTcpChannel.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:27:18 <br />
 * 最后修改: 2016年8月23日 下午4:27:18 <br />
 * 修改历史: <br />
 */
public class NettyTcpChannel extends AbstractTcpChannel {
    private final ChannelHandlerContext channelHandlerContext;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    /**
     * 构造函数
     * @param _server
     * @param _seq
     * @param _ctx
     */
    public NettyTcpChannel(ITcpServer _server, long _seq, ChannelHandlerContext _ctx){
        super(_server, _ctx.channel().localAddress(), _ctx.channel().remoteAddress(), _seq);
        channelHandlerContext = _ctx;
    }

    @Override
    protected void doWrite(byte[] _buf, int _offset, int _len){
    	ByteBuf buf = Unpooled.copiedBuffer(_buf, _offset, _len);
        channelHandlerContext.writeAndFlush(buf);
    }

    @Override
    protected void writeAndFlush(Object msg) {
        channelHandlerContext.writeAndFlush(msg);
    }

    @Override
    protected boolean getClosed(){
        return closed.get();
    }

    @Override
    protected void doClose(final String _cause){
        if( !closed.getAndSet(true) ){
            channelHandlerContext.close();
        }
    }
	
}
