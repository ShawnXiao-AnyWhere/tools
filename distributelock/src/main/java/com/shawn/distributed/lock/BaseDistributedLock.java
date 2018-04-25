/**  
 * Project Name:codegengerator  
 * File Name:BaseDistributedLock.java  
 * Package Name:com.shawn.distributed.lock  
 * Date:2017年12月13日下午1:20:07  
 * Copyright (c) 2017, xiaoping@gnnet.com.cn All Rights Reserved.  
 *  
*/  
  
package com.shawn.distributed.lock;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

/**  
 * ClassName:BaseDistributedLock <br/>  
 * Function: TODO ADD FUNCTION. <br/>  
 * Reason:   TODO ADD REASON. <br/>  
 * Date:     2017年12月13日 下午1:20:07 <br/>  
 * @author   xiaoping  
 * @version    
 * @since    JDK 1.8  
 * @see        
 */
public class BaseDistributedLock {

    private final ZkClientExt client;
    private final String path;
    private final String basePath;
    private final String lockName;
    private static final Integer  MAX_RETRY_COUNT = 10;
    
    public BaseDistributedLock(ZkClientExt client, String path, String basePath, String lockName) {
        super();
        this.client = client;
        this.path = path.concat("/").concat(lockName);
        this.basePath = path;
        this.lockName = lockName;
    }
    
    //删除成功获取锁之后所创建的那个顺序节点
    private void deleteOurPath(String ourPath) throws Exception{
        client.delete(ourPath);
    }

    //创建临时顺序节点
    private String createLockNode(ZkClient client, String path) throws Exception{
        return client.createEphemeralSequential(path, null);
    }
    
    //等待比自己次小的顺序节点的删除
    private boolean waitToLock(long startMills, Long millsToWait, String ourPath) throws Exception{
        boolean haveTheLock = false;
        boolean doDelete = false;
        
        try{
            while(!haveTheLock){
                //获取/locker下经过排序的子节点列表
                List<String> children = getSortedChildren();
                
                //获取刚才自己创建的那个顺序节点名
                String sequenceNodeName = ourPath.substring(basePath.length()+1);
                
                //判断自己排第几个
                int ourIndex = children.indexOf(sequenceNodeName);
                if(ourIndex < 0){
                    throw new ZkNoNodeException("节点没有找到：" + sequenceNodeName);
                }
                
                //如果是第一个，代表自己获得了锁
                boolean isGetTheLock = ourIndex == 0;
                
                //如果自己没有获得锁，则要watch比我们次小的那个节点
                String pathToWatch = isGetTheLock ? null : children.get(ourIndex - 1);
                if(isGetTheLock){
                    haveTheLock = true;
                }else{
                    //订阅比自己次小顺序节点的删除事件
                    String previousSequencePath = basePath.concat("/").concat(pathToWatch);
                    final CountDownLatch latch = new CountDownLatch(1);
                    final IZkDataListener previousListener = new IZkDataListener() {

                        @Override
                        public void handleDataChange(String arg0, Object arg1) throws Exception {
                              
                            
                        }

                        @Override
                        public void handleDataDeleted(String arg0) throws Exception {
                              
                            latch.countDown();
                        }
                    };
                    
                    try{
                        //订阅次小顺序节点的删除事件，如果节点不存在会出现异常
                        client.subscribeDataChanges(previousSequencePath, previousListener);
                        if(millsToWait != null){
                            millsToWait -= System.currentTimeMillis() - startMills;
                            startMills = System.currentTimeMillis();
                            if(millsToWait <= 0){
                                doDelete = true;
                                break;
                            }
                            latch.await(millsToWait, TimeUnit.MICROSECONDS);
                        }else{
                            latch.await();
                        }
                    }catch(ZkNoNodeException e){
                        
                    }finally{
                        client.unsubscribeDataChanges(previousSequencePath, previousListener);
                    }
                }
            }
        }catch(Exception e){
            //发生异常需要删除节点
            doDelete = true;
            throw e;
        }finally{
            //如果需要删除节点
            if(doDelete){
                deleteOurPath(ourPath);
            }
        }
        return haveTheLock;
    }
    
    private String getLockNodeNumber(String str, String lockName){
        int index = str.lastIndexOf(lockName);
        if(index >= 0 ){
            index += lockName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;
    }

    //获取/locker下的经过排序的子节点列表
    private List<String> getSortedChildren() {
        try{
            List<String> children = client.getChildren(basePath);
            Collections.sort(children, new Comparator<String>(){

                @Override
                public int compare(String lhs, String rhs) {
                      
                    return getLockNodeNumber(lhs, lockName).compareTo(getLockNodeNumber(rhs, lockName));
                }
                
            });
            return children;
        }catch(ZkNoNodeException e){
            client.createPersistent(basePath, true);
            return getSortedChildren();
        }  
    }
    
    protected void releaseLock(String lockPath) throws Exception{
        deleteOurPath(lockPath);
    }
    
    protected String attempLock(long time, TimeUnit unit) throws Exception{
        final long      startMillis = System.currentTimeMillis();
        final Long      millisToWait = (unit != null) ? unit.toMillis(time) : null;

        String          ourPath = null;
        boolean         hasTheLock = false;
        boolean         isDone = false;
        int             retryCount = 0;
        
        //网络闪断需要重试一试
        while ( !isDone ) {
            isDone = true;

            try {
                // 在/locker下创建临时的顺序节点
                ourPath = createLockNode(client, path);
                // 判断自己是否获得了锁，如果没有获得那么等待直到获得锁或者超时
                hasTheLock = waitToLock(startMillis, millisToWait, ourPath);
            } catch ( ZkNoNodeException e ) { // 捕获这个异常
                if ( retryCount++ < MAX_RETRY_COUNT ) { // 重试指定次数
                    isDone = false;
                } else {
                    throw e;
                }
            }
        }
        if ( hasTheLock ) {
            return ourPath;
        }

        return null;
    }
    
    
}
  
