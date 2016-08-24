package com.etrans.lib.db;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: DBConfig.java  <br />
 * 说       明: <br />
 * 作       者: mous <br />
 * 创建时间: 2016年8月23日 下午4:25:12 <br />
 * 最后修改: 2016年8月23日 下午4:25:12 <br />
 * 修改历史: <br />
 */
public class DBConfig {
    /**
     * 数据库类型
     */
    private DBDriver driver;
    
    /**
     * 服务器地址
     */
    public String host;
    
    /**
     * 数据库端口
     */
    public int port = 0;
    
    /**
     * 数据库实例名
     */
    public String instance;
    
    /**
     * 数据库名称
     */
    public String databaseName;
    
    /**
     * 登录用户
     */
    public String user;
    
    /**
     * 登录密码
     */
    public String pass = "";
    
    /**
     * 工作线程数
     */
    public int threads = 1;

    DBConfig(){
    }

    public DBConfig(DBDriver _driver){
        this.driver = _driver;
        this.host = "localhost";
        this.databaseName = "test";
        switch (_driver){
            case MYSQL:{
                user = "root";
                port = 3306;
            } break;
            
            case MSSQL:{
                user = "sa";
                port = 1433;
            } break;
            
            case ORACLE:{
                port = 1521;
            } break;
            
            default:
                break;
        }
    }

    /**
     * 克隆方法，复制一份
     * @return 新的对象
     */
    public DBConfig clone(){
        DBConfig config = new DBConfig(driver);
        config.host = this.host;
        config.port = this.port;
        config.instance = this.instance;
        config.databaseName = this.databaseName;
        config.user = this.user;
        config.pass = this.pass;
        config.threads = this.threads;
        return config;
    }

    public DBDriver getDriver() {
        return driver;
    }

    /**
     * 获取配置关键字
     * @return 关键字
     */
    public String getKey(){
        StringBuilder sb = new StringBuilder();
        sb.append(driver.getName()).append("/").append(host).append(":").append(port);
        sb.append("#").append(databaseName).append("$").append(user).append(":").append(pass);
        return sb.toString();
    }

    @Override
    public String toString(){
        return getKey();
    }
    
    /**
     * 获取Url
     * @return
     */
    public String getUrl(){
    	return driver.getConnectionURL(host, port, instance, databaseName);
    }

    /**
     * 样例
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        DBConfig cfg = new DBConfig(DBDriver.MSSQL);
        String jo =  JSONObject.toJSONString(cfg, true);
        DBConfig aa = JSON.parseObject(jo, DBConfig.class);
        System.out.println(jo + aa);
    }

}
