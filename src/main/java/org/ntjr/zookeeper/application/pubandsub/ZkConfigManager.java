package org.ntjr.zookeeper.application.pubandsub;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

public class ZkConfigManager {

    /**
     * 从数据库加载配置
     */
    public Config downLoadConfigFromDB(){
        String jdbcUrl = "jdbc:mysql://192.168.1.244:3306/p2p_ttz?useUnicode=true&zeroDateTimeBehavior=convertToNull&characterEncoding=UTF-8";
        String jdbcUsername = "root";
        String jdbcPassword = "123456";
        Config config = new Config(jdbcUrl,jdbcUsername,jdbcPassword);
        return config;
    }

    /**
     * 配置文件同步到zookeeper
     */
    public void syncConfigToZK(){
        Config config = downLoadConfigFromDB();
        ZkClient zk = new ZkClient("120.78.175.244:2181",5000);
        if(!zk.exists("/dbConfig")){
            zk.createPersistent("/dbConfig",true);
        }
        zk.writeData("/dbConfig",config);
        zk.close();
    }

    /**
     * 从zookeeper上加载数据
     */
    public Config getConfig(){
        ZkClient zk = new ZkClient("120.78.175.244:2181",5000);
        Config config = (Config)zk.readData("/dbConfig");
        System.out.println("加载配置："+config.toString());


        //监听配置文件修改
        zk.subscribeDataChanges("/dbConfig", new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object o) throws Exception {
                Config config = (Config)o;
                System.out.println("监听到配置文件被修改："+config.toString());
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {
                Config config = null;
                System.out.println("监听到配置文件被删除");
            }
        });

        return config;
    }


    public static void main(String[] args) {
        ZkConfigManager client = new ZkConfigManager();
        client.downLoadConfigFromDB();
        client.syncConfigToZK();
        Config config = client.getConfig();
        System.out.println(config.toString());
        for(int i = 0;i<10;i++){
            System.out.println(config.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

}
