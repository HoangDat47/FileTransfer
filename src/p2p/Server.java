package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class Server {
    private final DatagramSocket socket;
    private HashMap<String, Integer> nodes;

    public Server() throws IOException {
        InetAddress address = InetAddress.getByName("localhost");
        int port = 5000;
        this.nodes = new HashMap<>();
        this.socket = new DatagramSocket(port);
        System.out.println("Server started at " + address + ":" + port);
    }

    public void startListening() {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (true) {
                    socket.receive(packet);
                    String message = new String(packet.getData(), 0, packet.getLength());
                    Message msgObj = Message.fromString(message);
                    switch (msgObj.type()) {
                        case "CONNECTION_REQUEST" -> {
                            System.out.println("#" + msgObj.sender() + " is trying to connect");
                            if (nodes.size() == 10) handleNetworkFull(packet, msgObj);
                            else if (nodes.containsKey(msgObj.sender())) handleNameAlreadyTaken(packet, msgObj);
                            else handleConnectionAccepted(packet, msgObj);
                        }
                        case "DISCONNECT" -> handleDisconnect(msgObj);
                        default -> System.out.println("MESSAGE NOT RECOGNIZED");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void handleNetworkFull(DatagramPacket packet, Message msgObj) throws IOException {
        Message res = new Message("CONNECTION_DENIED", "Server", "Network is full, please try again later");
        Message.sendMessageObject(this.socket, res, packet.getPort());
        System.out.println("#" + msgObj.sender() + " has been denied access");
    }

    private void handleNameAlreadyTaken(DatagramPacket packet, Message msgObj) throws IOException  {
        Message res = new Message("CONNECTION_DENIED", "Server", "Name already taken, find another name");
        Message.sendMessageObject(this.socket, res, packet.getPort());
        System.out.println("#" + msgObj.sender() + " has been denied access");
    }

    private void handleConnectionAccepted(DatagramPacket packet, Message msgObj) throws IOException {
        InetAddress group = InetAddress.getByName("233.1.1.1");
        int multicastPort = 1234;
        nodes.put(msgObj.sender(), packet.getPort());
        String stringToSend = Utils.hashMapToString(nodes) + group + "/" + multicastPort;
        Message res = new Message("CONNECTION_ACCEPTED", "Server", stringToSend);
        Message.sendMessageObject(this.socket, res, packet.getPort());
        System.out.println("#" + msgObj.sender() + " has joined the network");
        System.out.println("Nodes: " + nodes);
    }

    private void handleDisconnect(Message msgObj) {
        nodes.remove(msgObj.sender());
        System.out.println("#" + msgObj.sender() + " has left the network");
        System.out.println("Nodes: " + nodes);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startListening();
    }
}
