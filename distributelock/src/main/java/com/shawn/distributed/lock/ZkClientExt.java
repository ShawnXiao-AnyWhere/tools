/**  
 * Project Name:codegengerator  
 * File Name:ZkClientExt.java  
 * Package Name:com.shawn.distributed.lock  
 * Date:2017年12月13日下午1:34:04  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.distributed.lock;

import java.util.concurrent.Callable;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.data.Stat;

/**  
 * ClassName:ZkClientExt <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年12月13日 下午1:34:04 <br/>  
 * @author   xiaoping  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class ZkClientExt extends ZkClient{

    public ZkClientExt(String zkServers, int sessionTimeout, int connectionTimeout,
            ZkSerializer zkSerializer) {
          
        super(zkServers, sessionTimeout, connectionTimeout, zkSerializer);  
        
    }
    
    @Override
    public void watchForData(final String path){
        retryUntilConnected(new Callable<Object>() {

            @Override
            public Object call() throws Exception {
                  Stat stat = new Stat();
                  _connection.readData(path, stat, true);
                return null;
            }
            
        });
    } 

}
  
