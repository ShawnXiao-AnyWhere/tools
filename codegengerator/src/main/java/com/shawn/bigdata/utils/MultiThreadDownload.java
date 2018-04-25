/**  
 * Project Name:codegengerator  
 * File Name:MultiThreadDownload.java  
 * Package Name:com.shawn.bigdata.utils  
 * Date:2017年11月28日上午11:29:31  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**  
 * ClassName:MultiThreadDownload <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年11月28日 上午11:29:31 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class MultiThreadDownload {

    private static final String SUFFIX_TMP = "_tmp";
    private String filepath = null;
    public String filename = null;
    private String tmpfilename = null;
    
    private int threadNum = 0;
    
    private CountDownLatch latch = null;
    
    private long fileLength = 0l;
    private long threadLength = 0l;
    private long[] startPos;
    private long[] endPos;
    
    public boolean bool = false;
    
    public URL url = null;
    
    public MultiThreadDownload(String filepath, int threadNum){
        this.filepath = filepath;
        this.threadNum = threadNum;
        startPos = new long[this.threadNum];
        endPos = new long[this.threadNum];
        latch = new CountDownLatch(this.threadNum);
    }
    
    public void downloadPart(){
        
        File file = null;
        File tmpfile = null;
        HttpURLConnection connection = null;
        
        filename = filepath.substring(filepath.lastIndexOf('/') + 1, filepath.contains("?") ? filepath.lastIndexOf('?') : filepath.length());
        
        tmpfilename = filename + SUFFIX_TMP;
        
        try{
            url = new URL(filepath);
            connection = (HttpURLConnection)url.openConnection();
            
            setHeader(connection);
            fileLength = connection.getContentLengthLong();
            
            file = new File(filename);
            tmpfile = new File(tmpfilename);
            
            threadLength = fileLength / threadNum;
            
            System.out.println("filename:" + filename + " ," + "fileLength= " + fileLength + "the threadLength= " + threadLength);
            
            if(file.exists() && file.length() == fileLength){
                System.out.println("the file you want to download has existed!");
                return;
            }else{
                setBreakPoint(startPos, endPos, tmpfile);
                ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
                for(int i = 0; i < threadNum; i++){
                    newCachedThreadPool.execute(new DownLoadRunnable(startPos[i], endPos[i], this, i, tmpfile, latch));
                }
                latch.await();
                newCachedThreadPool.shutdown();
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        
        if(file.length() == fileLength){
            if(tmpfile.exists()){
                System.out.println("delete the temp file!");
                tmpfile.delete();
            }
        }
    }
    
    private void setBreakPoint(long[] startPos2, long[] endPos2, File tmpfile) {
          RandomAccessFile randomfile = null;
          
          try{
              if(tmpfile.exists()){
                  System.out.println("the download has continued!");
                  randomfile = new RandomAccessFile(tmpfile, "rw");
                  for(int i = 0; i < threadNum; i++){
                      randomfile.seek(8 * i + 8);
                      startPos[i] = randomfile.readLong();
                      
                      randomfile.seek(8 * (i + 1000) + 16);
                      endPos[i] = randomfile.readLong();
                      
                      System.out.println("the array content in the exit file: ");
                      System.out.println("the thread" + (i+1) + "startPos:" + startPos[i] + ", endPos: " + endPos[i]);
                  }
              }else{
                  System.out.println("the tmpfile is not avaliable");
                  randomfile = new RandomAccessFile(tmpfile, "rw");
                  
                  for(int i = 0; i < threadNum; i++){
                      startPos[i] = threadLength * i;
                      if(i == threadNum - 1){
                          endPos[i] = fileLength;
                      }else{
                          endPos[i] = threadLength * (i + 1) - 1;
                      }
                      
                      randomfile.seek(8 * i + 8);
                      randomfile.writeLong(startPos[i]);
                      
                      randomfile.seek(8 * (i + 100) + 16);
                      randomfile.writeLong(endPos[i]);
                      
                      System.out.println("the array content: ");
                      System.out.println("the thread" + (i + 1) + " startPos:" + startPos[i] + ", endPos: " + endPos[i]);
                  }
              }
          }catch(Exception e){
              e.printStackTrace();
          }finally{
              if(randomfile != null){
                  try {
                    randomfile.close();
                } catch (IOException e) {
                      
                    e.printStackTrace();  
                    
                }
              }
          }
        
    }

    public void setHeader(HttpURLConnection con){
        
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.3) Gecko/2008092510 Ubuntu/8.04 (hardy) Firefox/3.0.3");
        con.setRequestProperty("Accept-Language", "en-us,en;q=0.7,zh-cn;q=0.3");
        con.setRequestProperty("Accept-Encoding", "aa");
        con.setRequestProperty("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        con.setRequestProperty("Keep-Alive", "300");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("If-Modified-Since", "Fri, 02 Jan 2009 17:00:05 GMT");
        con.setRequestProperty("If-None-Match", "\"1261d8-4290-df64d224\"");
        con.setRequestProperty("Cache-Control", "max-age=0");
        con.setRequestProperty("Referer", "http://www.skycn.com/soft/14857.html");
        
    }
}
  
