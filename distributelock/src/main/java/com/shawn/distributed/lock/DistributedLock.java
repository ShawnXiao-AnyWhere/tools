/**  
 * Project Name:codegengerator  
 * File Name:DistributedLock.java  
 * Package Name:com.shawn.distributed.lock  
 * Date:2017年12月13日下午1:16:37  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.distributed.lock;

import java.util.concurrent.TimeUnit;

/**  
 * ClassName:DistributedLock <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年12月13日 下午1:16:37 <br/>  
 * @author   xiaoping  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public interface DistributedLock {

    public void acquire() throws Exception;
    
    public boolean acquire(long time, TimeUnit unit) throws Exception;
    
    public void release() throws Exception;
    
}
  
