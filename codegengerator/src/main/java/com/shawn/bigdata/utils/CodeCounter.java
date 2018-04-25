/**  
 * Project Name:codegengerator  
 * File Name:CodeCounter.java  
 * Package Name:com.shawn.bigdata.utils  
 * Date:2018年1月16日下午4:03:59  
 * Copyright (c) 2018, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**  
 * ClassName:CodeCounter <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2018年1月16日 下午4:03:59 <br/>  
 * @author   xiaoping  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class CodeCounter {
    private static int whiteLines = 0;//空白行数  
    private static int commentLines = 0;//注释行数  
    private static int normalLines = 0;//有效代码行数  
      
    public static void main(String[] args)  
    {  
        File folder = new File("E:\\GreenNet\\sine-nispv4\\nispbigdata-parent\\gn-greenplum-telecomdata");//要统计的源目录路径  
          
        recur(folder);//递归该目录，统计其中代码行数  
          
        System.out.println("Total Lines:" + (whiteLines+commentLines+normalLines));  
        System.out.println("WhiteLines: " + whiteLines);  
        System.out.println("CommentLines: " + commentLines);  
        System.out.println("NormalLines: " + normalLines);  
    }  
      
    /** 
     * 递归方法 若源路径为目录，则递归，直至不包含子目录 
     * @param file 源文件或目录 
     */  
    public static void recur(File file)  
    {  
        File[] files = file.listFiles();  
        for(int i=0; i<files.length; i++)  
        {  
            //若源文件是目录，则递归  
            if(files[i].isDirectory() == true)  
            {  
                recur(files[i]);  
            }  
            //若源文件是普通文件且为java源文件，则逐行分析之  匹配文件名时用到了正则表达式  
            else if((files[i].isFile() == true) && files[i].getName().matches(".*\\.java$"))  
            {  
                parse(files[i]);  
            }  
        }  
    }  
      
    /** 
     * 将java源文件逐行解析，统计每种代码数目 用到了正则表达式 
     * @param file java源文件 
     */  
    public static void parse(File file)  
    {  
        BufferedReader br = null;  
        try {  
            br = new BufferedReader(new FileReader(file));  
            String line = "";  
            boolean isComment = false; //用来标记那些/*...*/注释不在同一行的情况  
              
            while((line=br.readLine()) != null)  
            {  
                line = line.trim();//注：去除每行行首和行尾的空格  但不会删除换行符  区分开空白行与前面有缩进的代码行  
                //if(line.matches("\\n[\\s| ]*\\r"))  
                if(line.matches("^[\\s&&[^\\n]]*"))//空白行：以空白字符“ \t\n\x0B\f\r”开始且不含换行符  
                {      //java中\n表示换行，\s匹配任意的空白符 包括换行符  
  
                    whiteLines++;  
                }else if(line.startsWith("/*") && line.endsWith("*/"))//注释行：形如同一行中/*...*/  
                {  
                    commentLines++;  
                }else if(line.startsWith("/*") && !line.endsWith("*/"))//注释行：形如/*...  
                {  
                    commentLines++;  
                    isComment = true;  
                }else if(true == isComment)  
                {  
                    commentLines++; //注释行：包含在/*和*/之间的注释行  
                    if(line.endsWith("*/"))  
                    {  
                        isComment = false; //注释行： 形如 ...*/  
                    }  
                }else if(line.startsWith("//"))  
                {  
                    commentLines++; //注释行： 形如//....  
                }else   
                {  
                    normalLines++; //有效代码行  
                }  
            }  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally  
        {  
            if(br != null)  
            {  
                try {  
                    br.close();  
                    br = null;  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
                  
            }  
        }  
    }  
}
  
