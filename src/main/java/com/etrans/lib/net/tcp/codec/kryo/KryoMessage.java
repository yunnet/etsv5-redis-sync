package com.etrans.lib.net.tcp.codec.kryo;

/** 
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: FrameMessage.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年6月13日 下午8:47:36  <br />
 * 最后修改: 2016年6月13日 下午8:47:36  <br />
 * 修改历史:   <br />
 */
public final class KryoMessage {
	private KryoHeader header;
	private Object body;

	/**
	 * 获取
	 * @return the header
	 */
	public KryoHeader getHeader() {
		return header;
	}

	/**
	 * 设置
	 * @param header the header to set
	 */
	public void setHeader(KryoHeader header) {
		this.header = header;
	}

	/**
	 * 获取
	 * @return the body
	 */
	public Object getBody() {
		return body;
	}

	/**
	 * 设置
	 * @param body the body to set
	 */
	public void setBody(Object body) {
		this.body = body;
	}
	
	@Override
	public String toString(){
		return String.format("FrameMessage: {headr:%s, body:%s}", this.header, this.body);
	}

}
