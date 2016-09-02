package com.etrans.etsv5.app.redis.sync;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: PathHelper.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月23日 下午4:23:11 <br />
 * 最后修改: 2016年8月23日 下午4:23:11 <br />
 * 修改历史: <br />
 */
public final class PathHelper {
    private final static Logger logger = LoggerFactory.getLogger(PathHelper.class.getSimpleName());

    /**
     *  该方法既可以用于JAR或WAR文件，也可以用于非JAR文件
     *  getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
     *  Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *  @param cls 为Class类型
     *  @return 返回值为该类所在的Java程序运行的目录
     */
    public static String getClsRoot(Class<?> cls){
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
            if(!pkgName.contains("."))
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
            logger.error("error on get file {}, {}", cls, e);
        }
        return realPath;
    }

    /**
     * 确保指定目录存在，没有则建立
     * @param _dir 目录
     */
    public static void forceDir(final String _dir){
        File file = new File(_dir);
        if(!file.exists())
            file.mkdirs();
    }

    /**
     * 获取文件扩展名
     * @param _file 文件
     * @return 扩展名
     */
    public static String getFileExtension(File _file) {
        String file_name = _file.getName();
        if (file_name.lastIndexOf(".") != -1 && file_name.lastIndexOf(".") != 0) {
            return file_name.substring(file_name.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * 取应用根目录
     * @return
     */
    public static String getAppRoot(){
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        if(null != url){
	        String path = url.toString().replace("file:", "");
	        if(path.endsWith("/"))
	            path = path.substring(0, path.lastIndexOf("/"));
	        return path;
        }
		return null;
    }

    /**
     * 读取文件为字节
     * @param _file
     * @return
     * @throws java.io.IOException
     */
    public static byte[] readFile2ByteArray(File _file) throws IOException {
        if (!_file.exists()) {
            throw new FileNotFoundException(_file.getName());
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) _file.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(_file));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }

    /**
     * 样例
     * @param _args
     * @throws Exception
     */
    public static void main(String[] _args) throws Exception {
        System.out.println(getClsRoot(PathHelper.class));
        System.out.println(PathHelper.getAppRoot());
    }
}
