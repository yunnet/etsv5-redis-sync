package com.etrans.lib.net.tcp.netty;

import io.netty.channel.ChannelHandlerContext;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: INettyListener.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:26:39 <br />
 * 最后修改: 2016年8月23日 下午4:26:39 <br />
 * 修改历史: <br />
 */
public interface INettyListener {
    void onChannelActive(ChannelHandlerContext _ctx);
    void onChannelInactive(ChannelHandlerContext _ctx);
    void onChannelRead(ChannelHandlerContext _ctx, Object msg);
}
