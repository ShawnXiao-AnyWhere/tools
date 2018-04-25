/**  
 * Project Name:codegengerator  
 * File Name:Person.java  
 * Package Name:com.shawn.bigdata.elasticsearch  
 * Date:2017年10月11日下午4:00:50  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.bigdata.elasticsearch;  
/**  
 * ClassName:Person <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年10月11日 下午4:00:50 <br/>  
 * @author   Shawn  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class Person {

    private String name;
    private String sex;
    private int age;
    private String hobby;
    private String number;
    
    public Person(){
        
    }
    
    public Person(String name, String sex, int age, String hobby, String number) {
        super();
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.hobby = hobby;
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getHobby() {
        return hobby;
    }
    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    
}
  
