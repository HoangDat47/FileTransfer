package File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.io.File;

public class UDPFileClient {
    private static final int PORT = 1234;

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            Scanner scn = new Scanner(System.in);
            String fileName = "";

            do {
                System.out.print("Nhap ten file: ");
                fileName = scn.nextLine();
            } while (checkFile(fileName));

            //gui yeu cau lay file
            String request = "READ " + fileName;
            byte[] requestBytes = request.getBytes();
            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, serverAddress, PORT);
            socket.send(requestPacket);

            //nhan file va luu vao thu muc
            File file = new File("src/share/" + fileName);
            FileOutputStream fos = new FileOutputStream(file);
            byte[] inputByte = new byte[60000];
            DatagramPacket inputPacket = new DatagramPacket(inputByte, inputByte.length);
            socket.receive(inputPacket);
            fos.write(inputPacket.getData(), 0, inputPacket.getLength());

            System.out.println("Da nhan file " + fileName + " tu server");
        } catch (IOException e) {
            System.err.println("Loi client: " + e.getMessage());
        }
    }

    private static boolean checkFile(String fileName) {
        boolean error = false;
        File file = new File("src/share/" + fileName);
        int maxLength = 64 * 1024;
        int fileLength = (int) file.length();

        if (fileLength > maxLength) {
            error = true;
            System.out.println("File qua lon");
        } else if (!file.exists()) {
            error = true;
            System.out.println("File khong ton tai");
        } else if (!file.canRead()) {
            error = true;
            System.out.println("Khong the doc file");
        } else if (file.isDirectory()) {
            error = true;
            System.out.println("Khong the gui thu muc");
        } else if (fileLength == 0) {
            error = true;
            System.out.println("File rong");
        } else if (fileLength < 0) {
            error = true;
            System.out.println("File khong hop le");
        } else {
            System.out.println("File hop le");
        }
        return error;
    }
}
