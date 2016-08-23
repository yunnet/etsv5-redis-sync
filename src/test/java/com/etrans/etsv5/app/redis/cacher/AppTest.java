package com.etrans.etsv5.app.redis.cacher;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.etrans.etsv5.lib.realtime.IDecoder;
//import com.etrans.etsv5.lib.realtime.KryoDecoder;

/**
 * Unit test for simple App.
 */
public class AppTest{
	private final static Logger logger = LoggerFactory.getLogger("AppTest");
	private Connection connection;
	private Statement statement;
	
//	private IDecoder decoder = new KryoDecoder();
	
	
	private final String C_SQL = "select * from v_pub_vehicle limit 100";
			
			
	public AppTest(){
		String driver="com.mysql.jdbc.Driver";// the MySQL driver  
	    String url="jdbc:mysql://localhost:3306/etbasedata";// URL points to destination database to manipulate  
	    String user="root";//user name for the specified database  
	    String pwd="root";//the corresponding password  
	    
	    try {
	    	connection = DriverManager.getConnection(url, user, pwd);
	    	statement = connection.createStatement();
		} catch (Exception e) {
			logger.error("getconnection err: ", e);
			return;
		}
	}
	
	public void querytable() throws SQLException{
		ResultSet rs = statement.executeQuery(C_SQL);
		
		while(rs.next()){
			System.out.println(rs.getTimestamp(6).getTime());
		}
		
		
//		CachedRowSet cachedRowSet = new CachedRowSetImpl();
//		cachedRowSet.populate(rs);
//		Kryo kryo = new Kryo();
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		kryo.writeObject(new FastOutput(outputStream), cachedRowSet);
//		byte[] bytes = outputStream.toByteArray();
//		
//		Input input = new Input(bytes, 0, bytes.length);
//		Kryo kryo2 = new Kryo();
//		CachedRowSet cachedRowSet2 = kryo2.readObject(input, CachedRowSetImpl.class);
//		
////		CachedRowSet decRow = decoder.decode(bytes, CachedRowSetImpl.class);
//		while(cachedRowSet2.next()){
//			System.out.println(cachedRowSet2.getInt(1));
//		}
		
	}
	
	/**
	 * 样例
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args)throws Exception{
		AppTest appTest = new AppTest();
		appTest.querytable();
	}
}
