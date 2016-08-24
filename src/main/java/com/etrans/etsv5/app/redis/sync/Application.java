package com.etrans.etsv5.app.redis.sync;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;

import java.io.File;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: Application.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:23:57 <br />
 * 最后修改: 2016年8月23日 下午4:23:57 <br />
 * 修改历史: <br />
 */
public class Application{
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(Application.class);

    private String appName = null;
    private String appCaption = "";
    private String appVersion = "";

    public Application(String _app_name){
        appName = _app_name;
    }

    public Application(){
        this(null);
    }

    public String getCaption(){
        return appCaption;
    }

    public String getVersion(){
        return appVersion;
    }

    private String getAppName(){
        String tmp = appName;
        if(null==tmp){
            tmp = System.getProperty("appName", null);
            if(null==tmp)
                tmp = getClass().getSimpleName();
        }
        return tmp;
    }

    private void loadConfig() throws Exception{
        String path = getAppPath(this.getClass()) +"/" + getAppName() +".xml";
        logger.debug("loadConfig({})", path);
        try{
            SAXReader reader = new SAXReader();
            Document  document = reader.read(new File(path));
            Element root = document.getRootElement();

            appCaption = root.attributeValue("application");
            appVersion = root.attributeValue("version");

            onAppConfig(root);
        }
        catch(Exception e){
            e.printStackTrace();
	        System.exit(1);
        }
    }

    protected boolean getBoolAttr(Element _e, String _attr){
        String tmp = _e.attributeValue(_attr);
        return (null!=tmp) && (!tmp.equals("0"));
    }

    protected int getIntAttr(Element _e, String _attr, int _def){
        String tmp = _e.attributeValue(_attr);
        try{
            return Integer.parseInt(tmp);
        }catch (Exception e){
            return _def;
        }
    }

    protected void onAppConfig(Element _root) throws Exception{ }
    protected void onAppStart() throws Exception{}
    protected void onAppStop() throws Exception{}


    public void run() throws Exception{
        loadConfig();
        onAppStart();
    }

    /**
     *  该方法既可以用于JAR或WAR文件，也可以用于非JAR文件
     *  getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
     *  Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *  @param cls 为Class类型
     *  @return 返回值为该类所在的Java程序运行的目录
     */
    public static String getAppPath(Class<?> cls) throws Exception{
        // 检查用户传入的参数是否为空
        if(null==cls)
            throw new IllegalArgumentException("argument is null");
        ClassLoader loader = cls.getClassLoader();

        // 获得类的全名，包括包名
        String clsName = cls.getName()+".class";

        // 获得传入参数所在的包
        Package pkg = cls.getPackage();
        String path = "";

        // 如果不是匿名包，将包名转化为路径
        if(null!=pkg){
            String pkgName = pkg.getName();
            // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if(pkgName.startsWith("java") || pkgName.startsWith("javax"))
                throw new IllegalArgumentException("no system class");

            // 在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(pkgName.length()+1);

            // 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if(pkgName.indexOf(".")<0)
                path = pkgName+"/";
            else{// 否则按照包名的组成部分，将包名转换为路径
                int start = 0;
                int end = pkgName.indexOf(".");
                while (end!=-1){
                    path += pkgName.substring(start,end) +"/";
                    start = end + 1;
                    end = pkgName.indexOf(".", start);
                }
                path += pkgName.substring(start) +"/";
            }
        }

        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(path + clsName);
        String realPath = url.getPath();

        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if(pos>-1)
            realPath = realPath.substring(pos+5);

        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos-1);

        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if(realPath.endsWith("!"))
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));

        // ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
        // 中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
        // 的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
        // 中文及空格路径
        try{
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        }catch (Exception e){
            throw new Exception(e);
        }
        return realPath;
    }

    public static String getAppPath() throws Exception{
        return getAppPath(Application.class);
    }

    public static void main(String[] args) throws Exception {
        Application app = new Application();
        app.run();
        Thread.sleep(Integer.MAX_VALUE);
        app.onAppStop();
    }
}