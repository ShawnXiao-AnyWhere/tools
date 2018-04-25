/**  
 * Project Name:codegengerator  
 * File Name:BitMap.java  
 * Package Name:com.shawn.bigdata.algorithm  
 * Date:2017年11月30日下午1:19:16  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.algorithm;  
/**  
 * ClassName:BitMap <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年11月30日 下午1:19:16 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class BitMap {

    int numSize = 1000;  
    
    int arraySize =(int)Math.ceil((double)numSize/32);  
      
    private int array[] = new int[arraySize];  
      
      
    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
          
        //也可以使用bitset  
        BitMap test = new BitMap();  
        test.initBitMap();  
        int sortArray[] = new int[]{1,4,32,2,6,9};  
        for(int i=0;i<sortArray.length;i++){  
            test.set1(sortArray[i]);  
        }  
        for(int i=0;i<test.numSize;i++){  
            if(test.get(i) !=0){  
               System.out.print((i)+" ");  
            }  
        }  
          
    }  
      
    public void initBitMap(){  
        for(int i=0;i<array.length;i++){  
            array[i] = 0;  
        }  
    }  
    public void set1(int pos){  
        array[pos>>5] =     array[pos>>5] | (1 <<(31-pos% 32) ); //给相应位置1  
          
    }  
      
    public int get(int pos){  
        return array[pos>>5] & (1 <<(31-pos% 32 ));  
    } 
}
  
