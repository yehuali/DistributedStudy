package org.ntjr.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * 为了避免存储在Zookeeper服务器上的数据被其他进程干扰、
 * 权限控制模式：world auth digest ip 和super
 */
public class AuthSample {
    final static String PATH = "/zk-book-auth_test";

    public static void main(String[] args) throws  Exception{
        ZooKeeper zooKeeper = new ZooKeeper("120.78.175.244:2181",5000,null);
        zooKeeper.addAuthInfo("digest","foo:true".getBytes());
        zooKeeper.create(PATH,"init".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);
        //使用无权限信息的zk会话访问权限信息的数据节点
        ZooKeeper zooKeeper2 = new ZooKeeper("120.78.175.244:2181",5000,null);
        zooKeeper2.getData(PATH,false,null);
    }
}
