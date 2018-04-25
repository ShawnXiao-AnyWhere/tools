/**  
 * Project Name:codegengerator  
 * File Name:DownLoadRunnable.java  
 * Package Name:com.shawn.bigdata.utils  
 * Date:2017年11月28日下午3:33:23  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

/**  
 * ClassName:DownLoadRunnable <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年11月28日 下午3:33:23 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class DownLoadRunnable implements Runnable {


    private long startPos;
    private long endPos;
    private MultiThreadDownload task = null;
    private RandomAccessFile downloadfile = null;
    private int id;
    private File tmpfile = null;
    private RandomAccessFile randomfile = null;
    private CountDownLatch latch = null;
    
    public DownLoadRunnable(long startPos, long endPos, MultiThreadDownload task, int id , File tmpfile, CountDownLatch latch){
        this.startPos = startPos;
        this.endPos = endPos;
        this.task = task;
        this.tmpfile = tmpfile;
        try{
            this.downloadfile = new RandomAccessFile(this.task.filename,
                    "rw");
            this.randomfile = new RandomAccessFile(this.tmpfile, "rw");
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        this.id = id;
        this.latch = latch;
    }

    @Override
    public void run() {

        HttpURLConnection httpcon = null;
        InputStream is = null;
        int length = 0;

        System.out.println("the thread " + id + " has started!!");

        while (true) {
            try {
                httpcon = (HttpURLConnection) task.url.openConnection();
                task.setHeader(httpcon);
                
                //防止网络阻塞，设置指定的超时时间；单位都是ms。超过指定时间，就会抛出异常
                httpcon.setReadTimeout(20000);//读取数据的超时设置
                httpcon.setConnectTimeout(20000);//连接的超时设置

                if (startPos < endPos) {
                    
                    //向服务器请求指定区间段的数据，这是实现断点续传的根本。
                    httpcon.setRequestProperty("Range", "bytes=" + startPos
                            + "-" + endPos);

                    System.out
                            .println("Thread " + id
                                    + " the total size:---- "
                                    + (endPos - startPos));

                    downloadfile.seek(startPos);

                    if (httpcon.getResponseCode() != HttpURLConnection.HTTP_OK
                            && httpcon.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                        this.task.bool = true;
                        httpcon.disconnect();
                        downloadfile.close();
                        System.out.println("the thread ---" + id
                                + " has done!!");
                        latch.countDown();//计数器自减
                        break;
                    }

                    is = httpcon.getInputStream();//获取服务器返回的资源流
                    long count = 0l;
                    byte[] buf = new byte[1024];

                    while (!this.task.bool && (length = is.read(buf)) != -1) {
                        count += length;
                        downloadfile.write(buf, 0, length);
                        
                        //不断更新每个线程下载资源的起始位置，并写入临时文件；为断点续传做准备
                        startPos += length;
                        randomfile.seek(8 * id + 8);
                        randomfile.writeLong(startPos);
                    }
                    System.out.println("the thread " + id
                            + " total load count: " + count);
                    
                    //关闭流
                    is.close();
                    httpcon.disconnect();
                    downloadfile.close();
                    randomfile.close();
                }
                latch.countDown();//计数器自减
                System.out.println("the thread " + id + " has done!!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
  
