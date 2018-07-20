package org.ntjr.zookeeper.sendThread;

import java.io.IOException;
import java.net.InetSocketAddress;

abstract class ClientCnxnSocket {
    protected ClientCnxn.SendThread sendThread;

    abstract void connect(InetSocketAddress addr) throws IOException;

    abstract boolean isConnected();

}
