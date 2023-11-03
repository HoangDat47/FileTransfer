import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<Socket> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345); // Sử dụng cổng 12345
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);

                ServerThread serverThread = new ServerThread(clientSocket);
                serverThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Socket> getClients() {
        return clients;
    }
}
