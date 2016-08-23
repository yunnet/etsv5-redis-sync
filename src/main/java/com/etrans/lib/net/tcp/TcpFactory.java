package com.etrans.lib.net.tcp;

import java.nio.channels.AsynchronousChannelGroup;
import java.util.concurrent.ExecutorService;

import com.etrans.lib.net.tcp.netty.NettyTcpClient;
import com.etrans.lib.net.tcp.netty.NettyTcpServer;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: TcpFactory.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:35:37 <br />
 * 最后修改: 2016年8月23日 下午4:35:37 <br />
 * 修改历史: <br />
 */
public final class TcpFactory{
    public static TcpType defaultNetType = TcpType.netTypeAIO;

    public static ITcpClient createClient(TcpType _type, ExecutorService _executor, AsynchronousChannelGroup _group, boolean _liner){
        switch (_type){
            case netTypeNETTY   : return new NettyTcpClient(_executor, _liner);
//            case netTypeAIO     : return new AioTcpClient(_executor, _group, _liner);
            default             : return null;
        }
    }
    
    public static ITcpClient createClient(ExecutorService _executor, AsynchronousChannelGroup _group, boolean _liner){
        return createClient(defaultNetType, _executor, _group, _liner);
    }

    public static ITcpClient createClient(TcpType _type, boolean _liner){
        return createClient(_type, null, null, _liner);
    }

    public static ITcpClient createClient(TcpType _type){
        return createClient(_type, false);
    }

    public static ITcpClient createClient(boolean _liner){
        return createClient(defaultNetType, _liner);
    }

    public static ITcpClient createClient(){
        return createClient(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static ITcpServer createServer(TcpType _type, ExecutorService _executor, AsynchronousChannelGroup _group, boolean _liner){
        switch (_type){
            case netTypeNETTY   : return new NettyTcpServer(_executor, _group, _liner);
//            case netTypeAIO     : return new AioTcpServer(_executor, _group, _liner);
            default             : return null;
        }
    }

    public static ITcpServer createServer(ExecutorService _executor, AsynchronousChannelGroup _group, boolean _liner){
        return createServer(defaultNetType, _executor, _group, _liner);
    }

    public static ITcpServer createServer(ExecutorService _executor, TcpType _type, boolean _liner){
        return createServer(_type, _executor, null, _liner);
    }

    public static ITcpServer createServer(TcpType _type, boolean _liner){
        return createServer(_type, null, null, _liner);
    }

    public static ITcpServer createServer(TcpType _type){
        return createServer(_type, false);
    }

    public static ITcpServer createServer(boolean _liner){
        return createServer(defaultNetType, _liner);
    }

    public static ITcpServer createServer(){
        return createServer(false);
    }

    /**
     * 样例
     * @param args
     * @throws Exception
     */
    public void main(String args[]) throws Exception{
        TcpFactory.createServer();
        TcpFactory.createClient();
    }
}
