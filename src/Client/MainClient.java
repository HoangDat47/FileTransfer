package Client;

import java.net.Socket;
import java.util.Scanner;

public class MainClient extends Thread{
    public static void main(String[] args) {
        MainClient client = new MainClient();
        client.start();
    }

    @Override
    public void run() {
        try (Socket clientSocket = new Socket("localhost", 12345)) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your name: ");
            String name = scanner.nextLine();

            ClientThread clientThread = new ClientThread(clientSocket);
            clientThread.start();

            // Nhận danh sách clients từ máy chủ
            clientThread.receiveClientList();

            while (true) {
                String message = scanner.nextLine();
                clientThread.sendMessage("message " + name + ": " + message);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
