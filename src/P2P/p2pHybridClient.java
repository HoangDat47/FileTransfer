package P2P;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class p2pHybridClient extends Thread {
    private List<ClientModel> receivedModelList = new ArrayList<>();

    private static Socket socket;
    private static String name;

    public static void main(String[] args) {
        p2pHybridClient client = new p2pHybridClient();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(client);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your name: ");
        name = scanner.nextLine();

        try {
            socket = new Socket("localhost", p2pHybridServer.SERVER_PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Tạo một thread mới để gửi tin nhắn
        Thread sendMessageThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                client.sendMessageToOtherClients();
            }
        });

        sendMessageThread.start();
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // Nhận danh sách client từ server
                byte[] serializeModelList = new byte[1024];
                int length = socket.getInputStream().read(serializeModelList);
                if (length > 0) {
                    receivedModelList = ModelSerializationUtil.deserializeModelList(serializeModelList);
                    System.out.println("Received client list: " + receivedModelList);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToOtherClients() {
        try {
            while (true) {
                System.out.println("Enter message: ");
                String message = new Scanner(System.in).nextLine();
                if (message != null && !message.isEmpty()) {
                    sendMessage("message" + name + ": " + message);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) throws IOException {
        synchronized (receivedModelList) {
            for (ClientModel client : receivedModelList) {
                if (client.getPort() != socket.getLocalPort()) {
                    Socket clientSocket = new Socket(client.getAddress(), client.getPort());
                    clientSocket.getOutputStream().write(message.getBytes());
                    clientSocket.close();
                }
            }
        }
    }
}
