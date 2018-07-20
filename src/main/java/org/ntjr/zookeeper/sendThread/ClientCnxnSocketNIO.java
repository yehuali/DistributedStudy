package org.ntjr.zookeeper.sendThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ClientCnxnSocketNIO extends ClientCnxnSocket{

    private final Selector selector = Selector.open();
    private SelectionKey sockKey;


    //捕获Selector.open()的异常
    ClientCnxnSocketNIO() throws IOException {
        super();
    }

    @Override
    boolean isConnected() {
        return sockKey != null;
    }

    @Override
    void connect(InetSocketAddress addr) throws IOException {
        SocketChannel sock = createSock();
        try{
            registerAndConnect(sock,addr);
        }catch (IOException e){
            sock.close();
            throw  e;
        }

    }

    SocketChannel createSock() throws IOException{
        SocketChannel sock;
        sock = SocketChannel.open();
        sock.configureBlocking(false);
        sock.socket().setSoLinger(false,-1);
        sock.socket().setTcpNoDelay(true);
        return sock;
    }

    void registerAndConnect(SocketChannel sock,InetSocketAddress address) throws IOException{
        sockKey = sock.register(selector,SelectionKey.OP_CONNECT);
        boolean immediateConnect = sock.connect(address);
        if(immediateConnect){

        }
    }
}
