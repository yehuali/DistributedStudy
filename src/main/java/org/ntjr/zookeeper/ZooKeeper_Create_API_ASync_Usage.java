package org.ntjr.zookeeper;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

public class ZooKeeper_Create_API_ASync_Usage implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
    public static void main(String[] args) throws Exception{
        ZooKeeper zookeeper = new ZooKeeper("120.78.175.244:2181",5000,new ZooKeeper_Create_API_ASync_Usage());
        connectedSemaphore.await();
        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, new IStringCallback(), "I am context.");
        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL,new IStringCallback(), "I am context.");
        zookeeper.create("/zk-test-ephemeral-", "".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,new IStringCallback(), "I am context.");
        Thread.sleep( Integer.MAX_VALUE );
    }
    public void process(WatchedEvent event) {
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}
class IStringCallback implements AsyncCallback.StringCallback{
    public void processResult(int rc, String path, Object ctx, String name) {
        System.out.println("Create path result: [" + rc + ", " + path + ", " + ctx + ", real path name: " + name);
    }
}

