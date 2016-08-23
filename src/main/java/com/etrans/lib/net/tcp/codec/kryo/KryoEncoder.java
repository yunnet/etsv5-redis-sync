package com.etrans.lib.net.tcp.codec.kryo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;

/** 
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: KryoEncoder.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月13日 下午9:25:51  <br />
 * 最后修改: 2016年6月13日 下午9:25:51  <br />
 * 修改历史:   <br />
 */
public class KryoEncoder {
	private static final Logger logger = LoggerFactory.getLogger(KryoEncoder.class.getSimpleName());

	private Kryo kryo;
	
	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	/**
	 * 构造函数
	 */
	public KryoEncoder() {
		kryo = new Kryo();
	}
	
	protected void encode(Object obj, ByteBuf out) {
        Output output = null;
        try {
            int lengthPos = out.writerIndex();
            out.writeBytes(LENGTH_PLACEHOLDER);
            ByteBufOutputStream bos = new ByteBufOutputStream(out);
            output = new Output(bos);
            kryo.writeClassAndObject(output, obj);
            out.writeBytes(output.toBytes());

            int bodyLength = out.writerIndex() - lengthPos - 4;
            out.setInt(lengthPos, bodyLength);
        } catch (Exception e) {
        	logger.error("decode", e);
        } finally {
            output.close();
        }
    }

}
