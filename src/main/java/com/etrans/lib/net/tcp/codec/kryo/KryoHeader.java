package com.etrans.lib.net.tcp.codec.kryo;

/** 
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: FrameHeader.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月13日 下午8:50:41  <br />
 * 最后修改: 2016年6月13日 下午8:50:41  <br />
 * 修改历史:   <br />
 */
public class KryoHeader {
	/**
	 * 校验码 4个字节
	 */
	private int crcCode = 0xABEF0101;

	/**
	 * 数据长度
	 */
	private int length;

	/**
	 * 消息类型
	 */
	private byte type;

	public int getCrcCode() {
		return crcCode;
	}

	public void setCrcCode(int crcCode) {
		this.crcCode = crcCode;
	}

	/**
	 * 获取
	 * @return the type
	 */
	public byte getType() {
		return type;
	}

	/**
	 * 设置
	 * @param type the type to set
	 */
	public void setType(byte type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public String toString(){
		return String.format("FrameHeader: {crc: %4X, length:%d, type:%d}", this.crcCode, this.length, this.type);
	}

}
