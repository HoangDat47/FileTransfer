package File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Scanner;

public class BigFileServer {
    private static final int PORT = 9000;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress clientAddress = InetAddress.getByName("localhost");
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter file name: ");
            String fileName = scanner.nextLine();

            while (true) {
                // Đọc file
                File file = new File("src/share/dat/" + fileName);
                int fileLength = (int) file.length();
                FileInputStream fis = new FileInputStream(file);
                byte[] fileBytes = new byte[fileLength];
                fis.read(fileBytes);

                // Gửi gói đầu tiên (rỗng)
                byte[] firstBytes = new byte[0];
                DatagramPacket firstPacket = new DatagramPacket(firstBytes, 0, clientAddress, PORT);
                socket.send(firstPacket);
                System.out.println("Đã gửi gói đầu");

                // Gửi các gói từ 1 đến n-1
                int fileAmount = (int) Math.ceil((double) fileLength / 60000);
                for (int i = 0; i < fileAmount - 1; i++) {
                    // Copy các bytes cho gói
                    byte[] midBytes = Arrays.copyOfRange(fileBytes, i * 60000, (i + 1) * 60000);
                    DatagramPacket midPacket = new DatagramPacket(midBytes, midBytes.length, clientAddress, PORT);
                    socket.send(midPacket);
                    System.out.println("Đã gửi gói thứ " + (i + 1));
                }

                // Gửi gói cuối cùng
                int remainingBytes = fileLength - (fileAmount - 1) * 60000;
                byte[] endBytes = Arrays.copyOfRange(fileBytes, (fileAmount - 1) * 60000, (fileAmount - 1) * 60000 + remainingBytes);
                DatagramPacket endPacket = new DatagramPacket(endBytes, endBytes.length, clientAddress, PORT);
                socket.send(endPacket);
                System.out.println("Đã gửi gói cuối");

                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
