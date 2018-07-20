package org.ntjr.zookeeper.sendThread;

public class Zookeeper {
    public enum  States{
        CONNECTING, ASSOCIATING, CONNECTED, CONNECTEDREADONLY,
        CLOSED, AUTH_FAILED, NOT_CONNECTED;

        public boolean isAlive() {
            return this != CLOSED && this != AUTH_FAILED;
        }

        public boolean isConnected() {
            return this == CONNECTED || this == CONNECTEDREADONLY;
        }
    }
}
