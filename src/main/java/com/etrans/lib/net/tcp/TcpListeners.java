package com.etrans.lib.net.tcp;

import java.util.ArrayList;
import java.util.List;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: TcpListeners.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:36:17 <br />
 * 最后修改: 2016年8月23日 下午4:36:17 <br />
 * 修改历史: <br />
 */
public class TcpListeners<T> implements ITcpListener<T> {
    private final List<ITcpListener<T>> list = new ArrayList<>();

    public void addListener(ITcpListener<T> _listener){
        if(!list.contains(_listener))
            list.add(_listener);
    }

    public int size(){
        return list.size();
    }

    public void onConn(T _sender, boolean _success, Exception _e){
        for(ITcpListener<T> listener : list)
            listener.onConn(_sender, _success, _e);
    }

    public void onBrok(T _sender){
        for(ITcpListener<T> listener : list)
            listener.onBrok(_sender);
    }

    public void onData(T _sender, Object _data){
        for(ITcpListener<T> listener : list)
            listener.onData(_sender, _data);
    }

}
