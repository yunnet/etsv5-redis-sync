package com.etrans.lib.net.tcp.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public interface IAioListener<T> {
    void onSocketData(T _sender, ByteBuffer _bb);
    void onSocketClose(T _sender, AsynchronousSocketChannel _channel);
}
