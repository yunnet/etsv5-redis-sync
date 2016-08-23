package com.etrans.etsv5.app.redis.sync;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: AppConfig.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:24:38 <br />
 * 最后修改: 2016年8月23日 下午4:24:38 <br />
 * 修改历史: <br />
 */
public abstract class AppConfig {
    protected final Logger logger = LoggerFactory.getLogger(AppConfig.class.getSimpleName());
    
    /**
     * 应用根目录
     */
    private String appRoot;

    /**
     * 程序名称
     */
    private String appName;

    /**
     * 应用程序版本
     */
    private String appVersion;
    
    private boolean checkApp;

    /**
     * 发布实例的编号
     */
    private String deployID;
    
    private boolean rpcEnable;
    private String rpcHost;
    private int rpcPort;  
    
	/**
     * 构造函数
     */
    public AppConfig() {
    	this.checkApp = false;
    	try {
    		this.appRoot = Application.getAppPath(this.getClass());
		} catch (Exception e) {
			logger.error("can not get app root", e);
            System.exit(0);
		}
    }

    /**
     * 构造函数
     * @param _app_type 应用类型
     * @param _cfg_class 配置类
     */
    public <T extends AppConfig> AppConfig(Class<T> _cfg_class) {
    	this.checkApp = true;
        try {
        	this.appRoot = PathHelper.getClsRoot(_cfg_class);
        } catch (Exception e) {
            logger.error("can not get app root", e);
            System.exit(0);
        }
    }

    /**
     * 获取应用根目录
     * @return
     */
    public String getAppRoot() {
        return appRoot;
    }

    /**
     * 获取程序名称
     * @return 程序名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 返回应用部署ID号，全局唯一
     * @return 部署ID
     */
    public String getDeployID(){
        return deployID;
    }

    /**
     * 部署的UUID值
     * @return UUID值   
     * 如：31-001-001 => 31001001
     */
    public String getDeployUUID(){
        return deployID.toUpperCase().replace(".","").replace("-","");
    }

    /**
     * 取应用版本信息
     * @return 应用版本
     */
    public String getAppVersion(){
        return appVersion;
    }
    
    /**
	 * 获取
	 * @return the rpcEnable
	 */
	public boolean isRpcEnable() {
		return rpcEnable;
	}

	/**
	 * 获取
	 * @return the rpcHost
	 */
	public String getRpcHost() {
		return rpcHost;
	}

	/**
	 * 获取
	 * @return the rpcPort
	 */
	public int getRpcPort() {
		return rpcPort;
	}


    /**
     * 读取应用配置，必须在具体应用程序中覆盖
     */
    protected abstract void loadAppConfig(JSONObject _json) throws Exception;

    /**
     * 加载系统相关配置
     */
    void loadConfig() throws Exception{
    	//获取配置文件路径
        String json_file = getConfigFile();

        File file = new File(json_file);
        if(!file.exists())
            throw new Exception("config not exists " + json_file);

        logger.info("loadConfig = {}", json_file);
        MemBuffer memBuffer = new MemBuffer(8192);
        memBuffer.load(file);

        logger.info("parseJSON config");
        String tmp_str = memBuffer.toString("UTF-8");
        JSONObject root_json = JSONObject.parseObject(tmp_str);
        memBuffer.clear();

        logger.info("  loadSysConfig......");
        JSONObject sys_json = root_json.getJSONObject("sys");
        appName = sys_json.getString("name");
        appVersion = sys_json.getString("version");
        deployID = sys_json.getString("deploy");
        
        if(checkApp){
	        ;
        }
        
        JSONObject rpc_json = sys_json.getJSONObject("rpc");
        if(null!=rpc_json) {
            this.rpcEnable = rpc_json.getBooleanValue("enable");
            if (rpcEnable) {
                this.rpcHost = rpc_json.getString("host");
                this.rpcPort = rpc_json.getIntValue("port");
            }
        }
        
        logger.info(":::::::::: appName = {}", appName);
        logger.info(":::::::::: version = {}", appVersion);
        logger.info(":::::::::: deploy  = {}", deployID);
        
        logger.info("  loadAppConfig......");
        JSONObject app_obj = root_json.getJSONObject("app");
        loadAppConfig(app_obj);
    }

	/**
	 * 获取配置文件路径
	 * @return
	 */
	private String getConfigFile() {
		return String.format("%s/appConfig.json", appRoot );
	}

}
