package com.etrans.lib.net.tcp.codec.kryo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import io.netty.buffer.ByteBuf;

/** 
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: KryoDecoder.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月13日 下午9:23:52  <br />
 * 最后修改: 2016年6月13日 下午9:23:52  <br />
 * 修改历史:   <br />
 */
public class KryoDecoder {
	private static final Logger logger = LoggerFactory.getLogger(KryoDecoder.class.getSimpleName());

	private final Kryo kryo;
	
	/**
	 * 构造函数
	 */
	public KryoDecoder() {
		kryo = new Kryo();
	}
	
	protected Object decode(ByteBuf in) throws Exception {
        Input input = null;
        try {
            int objectSize = in.readInt();
            ByteBuf objBuf = in.slice(in.readerIndex(), objectSize);
            byte[] objArray = new byte[objectSize];
            objBuf.readBytes(objArray);

            input = new Input(objArray);
            Object obj = kryo.readClassAndObject(input);
            return obj;
        } catch (Exception e) {
        	logger.error("decode", e);
            throw e;
        } finally {
            input.close();
        }
    }

}
