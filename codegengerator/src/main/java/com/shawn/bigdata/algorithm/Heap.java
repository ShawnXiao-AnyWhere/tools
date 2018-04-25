/**  
 * Project Name:codegengerator  
 * File Name:Heap.java  
 * Package Name:com.shawn.bigdata.algorithm  
 * Date:2017年11月30日下午1:46:22  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.algorithm;

import java.util.Arrays;

/**  
 * ClassName:Heap <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年11月30日 下午1:46:22 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class Heap {
static int a[]={49,38,65,97,76,13,27,49};    
    
    public static void main(String[] args){  
        heapSort(a);  
    }  
    //堆排序函数  
    public static void heapSort(int[] a){  
        int l = a.length;//记录数组长度  
        for(int i=0;i<l-1;i++){//循环调用建立堆函数建立堆  
            buildHeap(a,l-1-i);//调用建立堆函数  
            swap(a,0,l-1-i);//堆建立好后，交换根节点和堆的最后一个节点  
            System.out.println(Arrays.toString(a));  
        }  
    }  
    //交换元素的函数    
    public static void swap(int[] a,int i,int j){  
        int tmp = a[i];  
        a[i] = a[j];  
        a[j] = tmp;  
    }  
    //建立堆函数，在数组a中，从0到lastIndex建立堆  
    public static void buildHeap(int[] a,int lastIndex){  
        int l = a.length;//记录数组长度  
        for(int j=(lastIndex-1)/2;j>=0;j--){//从lastIndex节点（最后一个节点）的父节点开始循环  
            int k = j;//k保存正在判断的节点  
                //如果当前k节点的子节点存在  
            while(2*k+1 <= lastIndex){  
                int biggerIndex = 2*k+1;//k节点的左子节点索引  
                if(biggerIndex < lastIndex){//表示k节点存在右节点  
                    if(a[biggerIndex] < a[biggerIndex+1]){  
                        biggerIndex++;//biggerIndex总是记录较大子节点的索引  
                    }  
                }  
                if(a[k] < a[biggerIndex]){//如果k节点的值小于较大子节点的值，则交换它们的值，并将biggerIndex赋值给k，开始下一次循环  
                    swap(a,k,biggerIndex);//保证k节点的值大于其子节点的值  
                    k = biggerIndex; //赋值  
                }else{  
                    break;  
                }  
            }  
        }  
          
    }  
}
  
