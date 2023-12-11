package File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class BigFileClient {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        try {
            MulticastSocket socket = new MulticastSocket(PORT);
            InetAddress serverAddress = InetAddress.getByName("230.0.0.1");
            socket.joinGroup(serverAddress);
            File file = new File("src/share/hi");
            FileOutputStream fos = new FileOutputStream(file);
            FileInputStream fis = new FileInputStream(file);
            int count = 0;

            while (true){
                //nhan goi dau (rong)
                byte[] firstBytes = new byte[1];
                DatagramPacket firstPacket = new DatagramPacket(firstBytes, 1);
                socket.receive(firstPacket);
                int start = firstPacket.getLength();
                boolean complete = false;

                while (start == 0) {
                    System.out.println("Dang ghi file");
                    byte[] inputBytes = new byte[60000];
                    DatagramPacket packet = new DatagramPacket(inputBytes, 60000);
                    socket.receive(packet);

                    if(packet.getLength() == 0) {
                        complete = true;
                        break;
                    }
                    fos.write(packet.getData());
                    count+=packet.getLength();
                }
                if (complete) {
                    byte[] copyBytes = new byte[count];
                    fis.read(copyBytes);
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(copyBytes, 0, count);
                    break;
                }
            }
            socket.close();
            fis.close();
            fos.close();
            file.delete(); //xoa file rong
            System.out.println("Da nhan file");
            socket.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
