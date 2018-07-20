package org.ntjr.zookeeper.sendThread;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

public class ClientCnxn {
    private volatile Zookeeper.States state = Zookeeper.States.NOT_CONNECTED;

    class SendThread extends Thread{
        private final  ClientCnxnSocket clientCnxnSocket;
        private boolean isFirstConnect = true;
        private Random r = new Random(System.nanoTime());

        SendThread(ClientCnxnSocket clientCnxnSocket){
            this.clientCnxnSocket = clientCnxnSocket;
            setDaemon(true);
        }
        @Override
        public void run()  {
            if(!clientCnxnSocket.isConnected()){
                if(!isFirstConnect){
                    try{
                        Thread.sleep(r.nextInt(1000));
                    }catch (InterruptedException e){}

                }
            }
        }

        private void startConnect() throws IOException {
            state = Zookeeper.States.CONNECTING;
            InetSocketAddress address = new InetSocketAddress("120.78.175.244",2181);
            clientCnxnSocket.connect(address);
        }

        void primeConnection() throws IOException{

        }
    }

    class EventThread extends Thread{

    }
}
