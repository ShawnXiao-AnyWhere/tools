/**  
 * Project Name:codegengerator  
 * File Name:BloomFilter.java  
 * Package Name:com.shawn.bigdata.utils  
 * Date:2017年11月29日下午4:18:30  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.algorithm;

import java.util.BitSet;

/**  
 * ClassName:BloomFilter <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年11月29日 下午4:18:30 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @description
 * 基本数据类型占用多少位,1个字节等于8位
 * byte:8
 * short:16
 * int:32
 * long:64
 * float:32
 * double:64
 * char:16
 * boolean:32
 * @see        
 */
public class BloomFilter {
    
    private static int defaultSize = 5000 << 10000;
    private int basic = defaultSize - 1;
    private BitSet bits = new BitSet(defaultSize);
    
    //产生八个随机数并返回
    private int[] lrandom(String key){
        int[] randomsum = new int[8];
        for(int i = 0; i < 8; i++){
            randomsum[i] = hashCode(key, i+1);
        }
        return randomsum;
    }
    
    
    //将一个URL加入
    public synchronized void add(String key){
        int[] keyCode = lrandom(key);
        for(int i = 0; i < 8; i++){
            //将制定索引处的位设置为true
            bits.set(keyCode[i]);
        }
    }
    
    //判断一个URL是否存在
    
    public boolean exist(String key){
        int[] keyCode = lrandom(key);
        if(bits.get(keyCode[0]) &&
                bits.get(keyCode[1]) &&
                bits.get(keyCode[2]) &&
                bits.get(keyCode[3]) &&
                bits.get(keyCode[4]) &&
                bits.get(keyCode[5]) &&
                bits.get(keyCode[6]) &&
                bits.get(keyCode[7])){
            return true;
        }
        return false;
    }
    
    public int hashCode(String key, int Q) {
          int h = 0;
          int off = 0;
          //将url转化为一个新的字符数组
          char[] val = key.toCharArray();
          int length = key.length();
          for(int i = 0; i< length; i++){
              h = (30 + Q) * h + val[off++];
          }
        return basic & h;
    }


    public static void main(String[] args) {
        long pre = 0;
        long post = 0;
        pre = System.nanoTime();
        BloomFilter f = new BloomFilter(); //初始化
        f.add("http://www.agrilink.cn/"); 
        f.add("http://www.baidu.com/");
        System.out.println(f.exist("http://www.baidu.com/"));
        System.out.println(f.exist("http://www.baidud.com/"));
        post = System.nanoTime();
        System.out.println("Time: " + (post - pre));
    }

}
  
