package com.activemq;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * @author li_hhui
 * @date:2019年12月9日
 * @version:
 */
public class TestUtil {

	
	/**
	 * UTC时间转yyyyMMddHHmmss格式
	 * @param utcDate
	 * @return
	 * @throws ParseException
	 */
		public static String parseUTC(String utcDate) throws ParseException {
			SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			utcDate = utcDate.replace("Z", " UTC"); //注意UTC前有空格
			Date date = utcFormat.parse(utcDate);
			return sdf.format(date);
		}

		
		@Test
		public void tests(){
			String string ="2018-03-16T16:06:05Z";
			try {
				System.out.println(parseUTC(string));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		@Test
		public void testexp(){
			for (int i = 0; i <10000000; i++) {
				try {
					int b =1/0;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
}
