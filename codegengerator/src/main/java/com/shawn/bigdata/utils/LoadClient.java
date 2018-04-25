/**  
 * Project Name:codegengerator  
 * File Name:LoadClient.java  
 * Package Name:com.shawn.bigdata.utils  
 * Date:2017年11月28日下午4:32:07  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**  
 * ClassName:LoadClient <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年11月28日 下午4:32:07 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class LoadClient {
    public static void load(int start, int end) throws MalformedURLException, FileNotFoundException{  
        
        String endpoint = "http://localhost:8082/nisp/a/login;JSESSIONID=79e1f9625d354503a2fc776ffc967e47";  
              
        URL url = new URL(endpoint);  
        try {  
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
            conn.setRequestProperty("Content-Type","text/plain; charset=UTF-8");   
            conn.setRequestProperty("RANGE","bytes="+start+"-"+end); //header中增加range属性           
            conn.connect();  
            System.out.println(conn.getResponseCode());  
            System.out.println(conn.getContentLength());  
            System.out.println(conn.getContentType());  
            InputStream ins = (InputStream)conn.getContent();     
            //String fileName=conn.getHeaderField("Content-Disposition");  
            //fileName = new String(fileName.getBytes("ISO8859-1"), "UTF-8");  
            //fileName=fileName.substring(fileName.lastIndexOf("\\")+1);
            String fileName = "123.txt";
            System.out.println(fileName);  
            RandomAccessFile raFile = new RandomAccessFile("E:\\"+fileName, "rw");  
            raFile.seek(start);           
            byte[] buffer = new byte[4096];  
            int len = -1;  
            while((len = ins.read(buffer))!=-1){  
                raFile.write(buffer,0,len);  
            }  
            raFile.close();  
            conn.disconnect();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
      
    public static void main(String[] args) throws IOException {  
          
        load(0,100);  
        load(1001,7000);  
  
      
    }  
}
  
