package File;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPFileServer {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            byte[] inputByte;
            while (true) {
                //nhan yeu cau lay file
                inputByte = new byte[60000];
                DatagramPacket inputPacket = new DatagramPacket(inputByte, inputByte.length);
                socket.receive(inputPacket);
                String inputStr = new String(inputPacket.getData(), 0, inputPacket.getLength());
                String fileName = inputStr.substring(5);
                //Doc file va dua vao mang byte
                File file = new File("src/share/dat" + fileName);
                int fileLength = (int) file.length();
                byte[] outputBytes = new byte[fileLength];
                FileInputStream fis = new FileInputStream(file);
                fis.read(outputBytes);
                //gui file cho client
                DatagramPacket outputPacket = new DatagramPacket(outputBytes, outputBytes.length, inputPacket.getAddress(), inputPacket.getPort());
                socket.send(outputPacket);
            }

        } catch (IOException e) {
            System.err.println("Loi server: " + e.getMessage());
        }
    }
}