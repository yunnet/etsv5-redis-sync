package com.etrans.etsv5.app.redis.sync;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.etrans.etsv5.lib.redisSync.ObjCache;
import com.etrans.etsv5.lib.redisSync.ObjCacheRequest;
import com.etrans.etsv5.lib.redisSync.ObjCacheResponse;
import com.etrans.etsv5.lib.redisSync.ObjCacheType;
import com.etrans.lib.net.tcp.ITcpListener;
import com.etrans.lib.net.tcp.ITcpSession;
import com.etrans.lib.net.tcp.codec.CodecType;
import com.etrans.lib.net.tcp.codec.kryo.KryoHeader;
import com.etrans.lib.net.tcp.codec.kryo.KryoMessage;
import com.etrans.lib.net.tcp.netty.NettyTcpServer;

/** 
 * 工程名称: etsv5-redis-cacher  <br />
 * 版权所有 (C) 2016 e-Trans Company  <br />
 * 单元名称: Facade.java  <br />
 * 说       明:   <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年3月29日 上午10:16:42  <br />
 * 最后修改: 2016年3月29日 上午10:16:42  <br />
 * 修改历史:   <br />
 * 1、从config读取table信息,生成task对象
 * 2、从mysql查询出任务，返回任务ID
 * 3、投递任选结果ID，给redis线程。
 * 4、redis线程set进去。
 */
public class Facade {
	private static final Logger logger = LoggerFactory.getLogger(Facade.class.getSimpleName());
	private final Config config;
	private final MySQLHelper mySQLHelper;
	private final RedisHelper redisHelper;
	
	private final NettyTcpServer nettyTcpServer;
	private final Map<String, ITcpSession> sessionMap = new ConcurrentHashMap<>();
	
	private final ThreadPoolExecutor executorQuery;
	private final ThreadPoolExecutor executorWrite;
	
	private final ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();
	private boolean active;
	
	private static final int C_COREPOOL_SIZE = 1;
	private static final int C_MAXPOOL_SIZE = 8;
	private static final int C_POOL_KEEPTIME = 60;
	
	private static final int C_LOCALHOST = 99999;
	
	private final RC_SimplePipeline<RC_ITask, Object> pipeline;
	

	/**
	 * 构造函数
	 * @param _config
	 */
	public Facade(Config _config) {
		this.config = _config;
		
		this.executorQuery = new ThreadPoolExecutor(C_COREPOOL_SIZE, C_MAXPOOL_SIZE, C_POOL_KEEPTIME, TimeUnit.SECONDS, 
				new ArrayBlockingQueue<>(100), new RejectedExecutionHandler() {
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						if(!executor.isShutdown()){
							try {
								executor.getQueue().put(r);
							} catch (Exception e) {
								;
							}
						}
					}
				});
		
		this.executorWrite = new ThreadPoolExecutor(C_COREPOOL_SIZE, C_MAXPOOL_SIZE, C_POOL_KEEPTIME, TimeUnit.SECONDS, 
				new ArrayBlockingQueue<>(100), new RejectedExecutionHandler() {
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				if(!executor.isShutdown()){
					try {
						executor.getQueue().put(r);
					} catch (Exception e) {
						;
					}
				}
			}
		});
		
		this.pipeline = buildPipeline();
		this.pipeline.init(pipeline.newDefaultPipelineContext());
		
		//mysql connect pool
		this.mySQLHelper = new MySQLHelper(config);
		
		//redis connect pool
		this.redisHelper = new RedisHelper(config);
		
		//netty server
		nettyTcpServer = new NettyTcpServer(false);
		nettyTcpServer.setCodec(CodecType.KRYO);
		nettyTcpServer.setListenPort(config.getListenPort());
		nettyTcpServer.setTimeout(600);
		nettyTcpServer.addListener(listener);
		
		//schedule task
		timer.scheduleWithFixedDelay(()->doTimer(), 
				Math.abs(Utils.computNextMorningTimeMillis() - System.currentTimeMillis()), 
				1000 * 60 * 60 * config.getTimeAtDay(), TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 构建Pipeline
	 * @return
	 */
	private RC_SimplePipeline<RC_ITask, Object> buildPipeline(){
		final RC_SimplePipeline<RC_ITask, Object> pipeline = new RC_SimplePipeline<>();
		
		/****************************1*************************************/
		RC_Pipe<RC_QueryTask, Object> stageQuery = new RC_AbstractPipe<RC_QueryTask, Object>() {
			@Override
			protected Object doProcess(RC_QueryTask _input) throws RC_PipeException {
				logger.info("taskid={} user={} pipe1={}", _input.getTaskID(), _input.getUserID(), Thread.currentThread().getName());
				
				Connection connection = null;
				try {
					connection = mySQLHelper.getConnection();
					if(null == connection){
						logger.error("mySQLHelper get connection is null.");
						return null;
					}
					
					Statement statement = connection.createStatement();
					String sql_text = _input.genSQLText();
					logger.info("SQL: {}", sql_text);
					ResultSet rs = statement.executeQuery(sql_text);
					
					ResultSetMetaData meta = rs.getMetaData();
					
					Map<String, Map<String, String>> resultMap = new LinkedHashMap<>();
					
					int rows = 0;
					while(rs.next()){
						Map<String, String> rsMap = new LinkedHashMap<>();
						
						for(int i = 1; i < meta.getColumnCount(); i++){
							String fieldName = meta.getColumnLabel(i);
							if(null == fieldName || fieldName.isEmpty())
								continue;
							
							String fieldValue =  rs.getString(i);
							if(null == fieldValue || fieldValue.isEmpty())
								fieldValue = "";
							
							rsMap.put(fieldName, fieldValue);
						}
						
						//rs.getint(1) = id
						String key = _input.getTablename() + "_" + rs.getInt(1);
						resultMap.put(key, rsMap);
						
						rows++;
					}
					
					logger.info("::: query {} finish. Affected rows: {}", _input.getTablename(), rows);
					
					RC_QueryResult obj = new RC_QueryResult();
					obj.setTaskID(_input.getTaskID());
					obj.setUserID(_input.getUserID());
					obj.setResultMap(resultMap);
					
					return obj;
				} catch (Exception e) {
					logger.error("stageQuery err: ", e);
				} finally{
					if(null != connection){
						try {
							connection.close();
						} catch (Exception e2) {
							;
						}
					}
				}
				
				return null;
			}
		};
		
		pipeline.addAsThreadPoolBasedPipe(stageQuery, executorQuery);
		
		/****************************2*************************************/
		RC_Pipe<RC_QueryResult, ObjCacheResponse> stageRedis = new RC_AbstractPipe<RC_QueryResult, ObjCacheResponse>() {
			@Override
			protected ObjCacheResponse doProcess(RC_QueryResult _input) throws RC_PipeException {
				logger.info("taskid={} user={} pipe2={}", _input.getTaskID(), _input.getUserID(), Thread.currentThread().getName());
				
				Map<String, Map<String, String>> hashMap = _input.getResultMap();
				Iterator<Entry<String, Map<String, String>>> iterator = hashMap.entrySet().iterator();
				boolean isOK = true;
				try {
					while(iterator.hasNext()){
						Map.Entry<String, Map<String, String>> entry = iterator.next();
						
						String key = entry.getKey();
						Map<String, String> map = entry.getValue();
						
						redisHelper.hmset(key, map);
					}
				} catch (Exception e) {
					isOK = false;
				}
				
				ObjCacheResponse obj = new ObjCacheResponse();
				obj.setReceiptID(_input.getTaskID());
				obj.setUserID(_input.getUserID());
				obj.setResult(isOK);
				
				return obj;
			}
		};
		pipeline.addAsThreadPoolBasedPipe(stageRedis, executorWrite);
		
		/****************************3*************************************/
		RC_Pipe<ObjCacheResponse, Void> stageDispatch = new RC_AbstractPipe<ObjCacheResponse, Void>() {
			@Override
			protected Void doProcess(ObjCacheResponse _input) throws RC_PipeException {
				logger.info("taskid={} user={} pipe3={}", _input.getReceiptID(), _input.getUserID(), Thread.currentThread().getName());
				
				if(_input.getUserID() != C_LOCALHOST)
					disptchData(_input);
				return null;
			}
		};
		pipeline.addAsThreadPoolBasedPipe(stageDispatch, executorWrite);
		
		return pipeline;
	}
	
	public void setActive(boolean _active){
		if(this.active != _active){
			this.active = _active;
			
			doActive(_active);
		}
	}

	private void doActive(boolean _active) {
		if(_active){
			try {
				nettyTcpServer.start();
			} catch (Exception e) {
				logger.error("nettyTcpServer start failed.");
			}
		}
	}
	
	/**
	 * 开始
	 */
	public void start(){
		setActive(true);
	}
	
	/**
	 * 侦听事件
	 */
	private final ITcpListener<ITcpSession> listener = new ITcpListener<ITcpSession>() {
		@Override
		public void onConn(ITcpSession _sender, boolean _sucess, Exception _e) {
			logger.info("onConn {}", _sender.getSessionKey());
			sessionMap.put(_sender.getSessionKey(), _sender);
		}

		@Override
		public void onBrok(ITcpSession _sender) {
			logger.info("onBrok {}", _sender.getSessionKey());
			sessionMap.remove(_sender.getSessionKey());
		}

		@Override
		public void onData(ITcpSession _sender, Object _data) {
			if(_data instanceof KryoMessage) {
				KryoMessage frameMessage = (KryoMessage)_data;
				if(ObjCacheType.C_OCT_Request.getValue() == frameMessage.getHeader().getType()) {
					ObjCacheRequest obj = (ObjCacheRequest) frameMessage.getBody();
					logger.info("recv: {} <{}:{}>, table:{}, fields:[{}], condition:[{}]", 
							Utils.DateTimeToStr(obj.getGenTime()),
                            obj.getUserID(),
                            obj.getRequestID(), 
                            obj.getTableName(), 
                            obj.getFields(),
                            obj.getCondition()
							);
					
					handleRequest(obj);
				}
			}else
				logger.error("recv err: {}", new String((byte[])_data));
		}

	};
	
	/**
	 * 处理客户端请求
	 * @param _data
	 */
	private void handleRequest(ObjCacheRequest _data) {
		if(config.isCompulsiveCheck()){
			if(!config.getTableMap().containsKey(_data.getTableName())){
				logger.warn("request table={} is not define.", _data.getTableName());
				return;
			}
		}
		
		RC_QueryTask task = new RC_QueryTask();
		task.setTaskID(_data.getRequestID());
		task.setUserID(_data.getUserID());
		task.setTablename(_data.getTableName());
		task.setFields(_data.getFields());
		task.setCondition(_data.getCondition());
		
		putTask(task);
	}
	
	private void putTask(RC_ITask _task){
		try {
			pipeline.process(_task);
		} catch (Exception e) {
			logger.error("handleRequest err: ", e);
		}
	}
	
	/**
	 * 广播给所有人
	 * @param _data
	 */
	public void disptchData(ObjCache _data){
		KryoMessage message = new KryoMessage();
		KryoHeader header = new KryoHeader();
		header.setType((byte)_data.getObjType().getValue());

		message.setHeader(header);
		message.setBody(_data);
		
		for(ITcpSession session : sessionMap.values())
			session.write(message);
	}
	
	/**
	 * 任务调度
	 */
	private void doTimer() {
		for(Table table : config.getTableMap().values()){
			RC_QueryTask task = new RC_QueryTask();
			task.setTaskID(C_LOCALHOST);
			task.setUserID(C_LOCALHOST);
			task.setTablename(table.getTablename());
			task.setFields(table.getFields());
			task.setCondition(table.getCondition());
			
			putTask(task);
		}
	}

}
