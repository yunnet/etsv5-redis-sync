package com.etrans.lib.net.tcp.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: NettyHandler.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:26:52 <br />
 * 最后修改: 2016年8月23日 下午4:26:52 <br />
 * 修改历史: <br />
 */
public class NettyHandler extends ChannelInboundHandlerAdapter {
//    private static final AttributeKey<Long> START_TIME = new AttributeKey<>("START_TIME");
    private static final AttributeKey<Long> START_TIME = AttributeKey.newInstance("START_TIME");

    private final INettyListener listener;

    public NettyHandler(INettyListener _listener){
        this.listener = _listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //设置链接属性，记录链接开始的时间
        ctx.attr(START_TIME).set(System.currentTimeMillis());
        super.channelActive(ctx);
        listener.onChannelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        listener.onChannelInactive(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        listener.onChannelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //监听idle事件
    	if(evt instanceof IdleStateEvent){
    		IdleStateEvent event = (IdleStateEvent) evt;
    		//获取链接相关的属性（即链接开始的时间戳）
            Long startTime = ctx.attr(START_TIME).get();
            if(startTime == null)  //not arrive forever
                return;

            long idelTime = (System.currentTimeMillis() - startTime) / 1000;

            if(event.state() == IdleState.READER_IDLE)
                System.out.println(ctx.channel().remoteAddress() + " recv idle " + idelTime);

            else if(event.state() == IdleState.WRITER_IDLE)
                System.out.println(ctx.channel().remoteAddress()+" send idle "+idelTime);

            else if(event.state() == IdleState.ALL_IDLE)
            	System.out.println(ctx.channel().remoteAddress()+" all idel "+idelTime);
    	}
    	
    	super.userEventTriggered(ctx, evt); 
    }

    /**
     * Close the connection when an exception is raised.
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

}