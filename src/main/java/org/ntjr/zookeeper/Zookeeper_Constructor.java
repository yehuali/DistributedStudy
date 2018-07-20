package org.ntjr.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 客户端和服务端的建立是一个异步过程
 * 初始化后立即返回，此时并没有真正建立好一个可用的会话（处于Connecting状态）
 * 会话创建完毕后，zk服务端会向对应的客户端发送一个事件通知
 */
public class Zookeeper_Constructor implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws  Exception {
        ZooKeeper zooKeeper = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor());
        System.out.println("zk状态："+ zooKeeper.getState());
        try {
            connectedSemaphore.await();
        }catch (InterruptedException e){
            System.out.println("Zookeeper session established.");
        }

        /**
         * zookeeper不支持递归创建
         * zk的节点内容只支持字节数组（不负责为节点内容进行序列化）
         */
        String path1 = zooKeeper.create("/zk-test-ephemeral-","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        System.out.println("Success create znode:"+path1);

        String path2 = zooKeeper.create("/zk-test-ephemeral-","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Success create znode:"+path2);
    }

    @Test
    public void zookeeper_Constructor_Usage_With_SID_PASSWD() throws Exception{
        ZooKeeper zookeeper = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor());
        connectedSemaphore.await();
        long sessionId = zookeeper.getSessionId();
        byte[] passwd = zookeeper.getSessionPasswd();

//        zookeeper = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor(),1l,"test".getBytes());
        zookeeper = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor(),sessionId,passwd);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
   public void createAsyncNode() throws Exception{
        ZooKeeper zooKeeper = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor());
        connectedSemaphore.await();
        zooKeeper.create("/zk-test-ephemeral-", "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new IStringCallback(), "I am context.");
        zooKeeper.create("/zk-test-ephemeral-", "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(), "I am context.");
        zooKeeper.create("/zk-test-ephemeral-", "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,new IStringCallback(), "I am context.");
        Thread.sleep( Integer.MAX_VALUE );
   }

    /**
     * 获取节点
     * 事件通知中，是不包含最新的节点列表，客户端须主动从新进行获取
     * 1.注册Watcher
     * 2.stat(描述节点状态信息)
     */
    @Test
    public void getChildNode() throws  Exception{
        String path = "/zk-book8";
        zk = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor());
        connectedSemaphore.await();
        zk.create(path,"".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
        zk.create(path+"/c1","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);

        List<String> childrenList = zk.getChildren(path,true);
        System.out.println(childrenList);

        zk.create(path+"/c2","".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        Thread.sleep(Integer.MAX_VALUE);
    }


    @Test
    public void getNodeData() throws Exception{
        String path = "/zk-book";
        zk = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor());
        connectedSemaphore.await();
        zk.create(path,"123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        /**
         * getData的同步接口获取数据内容，调用时注册了一个Watcher
         * 传入stat变量，从服务端响应中获取到数据节点的最新节点状态信息
         */

        System.out.println(new String(zk.getData(path,true,stat)));
        System.out.println(stat.getCzxid() + "," + stat.getMzxid() + "," +stat.getVersion());
        /**
         * version用于指定节点的数据版本
         * version参数由CAS原理衍化而来，代表预期值，表明针对该数据版本进行更新，避免并发问题
         */
        zk.setData(path,"123".getBytes(),-1);//更新
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void setNodeData() throws Exception{
        String path = "/zk-book";
        zk = new ZooKeeper("120.78.175.244:2181",5000,new Zookeeper_Constructor());
        connectedSemaphore.await();

        zk.create(path,"123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL);
        zk.getData(path,true,null);

        Stat stat = zk.setData(path,"456".getBytes(),-1);
        System.out.println(stat.getCzxid() + "," +stat.getMzxid() + "," + stat.getVersion());
        //数据版本从0开始  -1 客户端需要基于数据的最新版本进行更新操作
        Stat stat2 = zk.setData(path,"456".getBytes(),-1);
        System.out.println(stat2.getCzxid() + "," +stat2.getMzxid() + "," + stat2.getVersion());

        try{
            //使用之前数据版本1进行更新操作导致异常
            zk.setData(path,"456".getBytes(),stat.getVersion());
        }catch (KeeperException e){
            System.out.println("Error:"+e.code() + "," + e.getMessage());
        }
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("Receive watched event:"+event);
        if(Event.KeeperState.SyncConnected == event.getState()){
            if(Event.EventType.None == event.getType() && null == event.getPath()){
                connectedSemaphore.countDown();//解除主程序在CountDownLatch上的等待阻塞。至此，客户端会话创建完毕
            }else if(event.getType() == Event.EventType.NodeChildrenChanged){
                /**
                 * 事件通知中，是不包含最新的节点列表的
                 * stat会被服务端响应的新stat对象替换
                 */
                try{
                    /**
                     * 获取新的子节点列表
                     * getChildren是数据节点的相对节点路径
                     * Watcher通知是一次性的，因此客户端需要反复注册Watcher
                     */
                    System.out.println("ReGet Child:"+zk.getChildren(event.getPath(),true));
                }catch (Exception e){}

            }else if(event.getType() == Event.EventType.NodeDataChanged){
                try{
                    System.out.println(new String(zk.getData(event.getPath(),true,stat)));
                    System.out.println(stat.getCzxid() + "," +stat.getMzxid() + "," +stat.getVersion());
                }catch (Exception e){}
            }

        }
    }
}
/**
 * 创建节点回调
 * rc:服务端响应码 0：成功 -4：连接已断开 -110：指定节点已存在 -112：会话已过期
 * path:接口调用时传入api的数据节点的节点路径参数值
 *
 */
class IStringCallback1 implements AsyncCallback.StringCallback{
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("Create path result:["+rc+","+path+","+ctx+",real path name:"+name);
    }
}
