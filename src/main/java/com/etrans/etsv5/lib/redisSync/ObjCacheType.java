package com.etrans.etsv5.lib.redisSync;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: ObjCacheType.java  <br />
 * 说       明: etsv5-lib-defs  <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016下午3:48:39  <br />
 * 最后修改: 2016下午3:48:39  <br />
 * 修改历史:   <br />
 */

public enum ObjCacheType {
	C_OCT_Request(1),
	C_OCT_Response(2)
	;
	
	final int value;
	ObjCacheType(int _value){
        value = _value;
    }
	
	public int getValue(){
        return value;
    }

}
