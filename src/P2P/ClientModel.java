package P2P;

import java.io.Serializable;
import java.net.InetAddress;

public class ClientModel implements Serializable {
    private static final long serialVersionUID = 1L;
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
    public String toString() {
        return address.toString() + ":" + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClientModel) {
            ClientModel client = (ClientModel) obj;
            return client.getAddress().equals(address) && client.getPort() == port;
        }
        return false;
    }
}
