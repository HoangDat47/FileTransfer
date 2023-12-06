package p2p;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Message {
    private String type;
    private String sender;
    private String content;

    public Message(String type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    public String type() {
        return type;
    }

    public String content() {
        return content;
    }

    public String sender() {
        return sender;
    }

    public String toString() {
        return type + ":" + sender + ":" + content;
    }

    public static Message fromString(String message) {
        String[] parts = message.split(":");
        String type = parts[0];
        String sender = parts[1];
        String content = parts[2];
        return new Message(type, sender, content);
    }

    public static void sendMessageObject(DatagramSocket socket, Message message, int portDestination) throws IOException {
        byte[] buffer = (message.toString()).getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, portDestination);
        socket.send(packet);
    }

    public static void broadcast(MulticastSocket socket, Message message, InetAddress groupAddress) throws IOException {
        byte[] buffer = (message.toString()).getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, groupAddress, 1234);
        socket.send(packet);
    }
}
