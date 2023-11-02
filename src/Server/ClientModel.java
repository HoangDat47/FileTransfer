package Server;

import java.net.InetAddress;

public class ClientModel {
    private final InetAddress address;
    private final int port;

    public ClientModel(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    // Compare this snippet from src/Server/MainServer.java:
    public boolean equals(Object obj) {
        if (obj instanceof ClientModel other) {
            return this.address.equals(other.address) && this.port == other.port;
        }
        return false;
    }
}
