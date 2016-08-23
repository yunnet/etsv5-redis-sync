package com.etrans.lib.net.tcp.codec;

/** 
 * 工程名称: ets-lib  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: CodecType.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年7月5日 上午11:55:04  <br />
 * 最后修改: 2016年7月5日 上午11:55:04  <br />
 * 修改历史:   <br />
 */
public enum CodecType {
	UNKNOWN(0),
	BYTEARRAY(1),
	AVRO(2),
	FASTJSON(3),
	KRYO(4),
	PROTOBUF(5),
	THRIFT(6);
	
	/**
     * 内部值
     */
    private final int value;
    
    CodecType(int _value){
    	this.value = _value;
    }
    
    public int getValue(){
        return value;
    }
    
    public static CodecType valueOf(int _value){
        switch (_value){
            case 0  : return UNKNOWN;
            case 1  : return BYTEARRAY;
            case 2  : return AVRO;
            case 3  : return FASTJSON;
            case 4  : return KRYO;
            case 5  : return PROTOBUF;
            case 6  : return THRIFT;
            default : return UNKNOWN;
        }
    }

}
