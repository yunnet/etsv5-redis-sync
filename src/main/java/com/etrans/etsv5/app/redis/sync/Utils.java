package com.etrans.etsv5.app.redis.sync;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 版权所有 (C) 2016 ® E-trans Company  <br />
 * 单元名称: Utils.java  <br />
 * 说       明: <br />
 * 作       者: yunnet <br />
 * 创建时间: 2016年8月22日 下午3:37:48 <br />
 * 最后修改: 2016年8月22日 下午3:37:48 <br />
 * 修改历史: <br />
 */

public class Utils {
	private static final String C_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final ThreadLocal<SimpleDateFormat> TS_SDF = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(C_YYYY_MM_DD_HH_MM_SS);
		}
	};
	
	private static final byte[] HEX_BYTES = {
            (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5',
            (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
            (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'
    };

	public static long computNextMorningTimeMillis() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }
    
    /***
     * Converts a byte array into a hexadecimal string
     * @param _byte_ary byte array
     * @return hex string
     */
    public static String byteArrayToHex(byte[] _byte_ary, int _offset, int len) {
        char[] s = new char[len * 2];
        for (int i = 0, j = 0; i < len; i++) {
            int c = ((int) _byte_ary[i + _offset]) & 0xff;
            s[j++] = (char) HEX_BYTES[c >> 4 & 0xf];
            s[j++] = (char) HEX_BYTES[c & 0xf];
        }
        String ret = new String(s);
        s = null;
        return ret;
    }
    
    public static String byteArrayToHex(byte[] _byte_ary) {
        return byteArrayToHex(_byte_ary, 0, _byte_ary.length);
    }

    public static String byteBufferToHex(ByteBuffer _buffer){
        byte[] bytes = toBytes(_buffer);
        return byteArrayToHex(bytes);
    }
    
    /**
     * converts a ByteBuffer into a byte array
     * @param buffer the ByteBuffer to convert
     * @return the byte array
     */
    public static byte[] toBytes(ByteBuffer buffer) {
        if (buffer == null) {
            return new byte[0];
        }

        int savedPos = buffer.position();
        int savedLimit = buffer.limit();

        try {
            byte[] array = new byte[buffer.limit() - buffer.position()];

            if (buffer.hasArray()) {
                int offset = buffer.arrayOffset() + savedPos;
                byte[] bufferArray = buffer.array();
                System.arraycopy(bufferArray, offset, array, 0, array.length);

                return array;
            } else {
                buffer.get(array);
                return array;
            }
        } finally {
            buffer.position(savedPos);
            buffer.limit(savedLimit);
        }
    }
    
    
    /**
     * 日期转为字符串
     * @param _time 长整型日期
     * @return 字符型日期
     */
    public static String DateTimeToStr(long _time){
        return DateTimeToStr(new Date(_time));
    }

    /**
     * 日期转为字符串
     * @param _date 日期型日期
     * @return 字符型日期
     */
    public static String DateTimeToStr(Date _date){
        return TS_SDF.get().format(_date);
    }

    /**
     * 字符串转为日期
     * @param _str 字符型日期
     * @return 日期
     * @throws ParseException
     */
    public static Date DateTimeByStr(final String _str) throws Exception{
    	return TS_SDF.get().parse(_str);
    }
    
    /**
     * 字符串转为日期
     * @param _s 字符型日期
     * @param _default 默认日期(日期型)
     * @return
     */
    public static Date StrToDateTimeDef(final String _s, Date _default){
    	try {
    		return DateTimeByStr(_s);
		} catch (Exception e) {
			return _default;
		}    	
    }
    
    /**
     * 字符串转为日期
     * @param _s 字符型日期
     * @param _default 默认日期(长整型)
     * @return
     */
    public static Date StrToDateTimeDef(final String _s, long _default){
    	try {
    		return DateTimeByStr(_s);
		} catch (Exception e) {
			return new Date(_default);
		}    
    }
	
	/**
	 * 字符转radix进制整型 异常返回Default值
	 * @param _s 字符串
	 * @param _radix 进制
	 * @param _default 默认值
	 * @return
	 */
	public static int StrToIntDef(final String _s, int _radix, int _default){
		try {
			return Integer.parseInt(_s, _radix);
		} catch (Exception e) {
			return _default;	
		}
	}
	
	/**
	 * 字符转radix进制字节型 异常返回Default值
	 * @param _s 字符串
	 * @param _radix 进制
	 * @param _default 默认值
	 * @return
	 */
	public static byte StrToByteDef(final String _s, int _radix, byte _default){
		try {
			return Byte.parseByte(_s, _radix);
		} catch (Exception e) {
			return _default;	
		}
	}
	
	/**
	 * 字符转radix进制长整型 异常返回Default值
	 * @param _s 字符串
	 * @param _radix 进制
	 * @param _default 默认值
	 * @return
	 */
	public static long StrToLongDef(final String _s, int _radix, long _default){
		try {
			return Long.parseLong(_s, _radix);
		} catch (Exception e) {
			return _default;	
		}
	}

}
