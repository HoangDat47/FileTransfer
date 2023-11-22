package P2P;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class p2pHybridServer extends Thread {
    public static final int SERVER_PORT = 1999;
    private final List<ClientModel> clientList = new ArrayList<>();

    public static void main(String[] args) {
        new p2pHybridServer().start();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started on port " + SERVER_PORT);

            while (!serverSocket.isClosed()){
                //chờ yêu cầu từ client
                Socket socket = serverSocket.accept();

                //tạo một thread để xử lý yêu cầu từ client
                Thread clientThread = new Thread(() -> {
                    try {
                        ClientModel client = new ClientModel(socket.getInetAddress(), socket.getPort());
                        synchronized (clientList) {
                            if (!clientList.contains(client)) {
                                clientList.add(client);
                                System.out.println("Client " + client + " connected");
                                sendClientList(socket);
                            }
                        }
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                clientThread.start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendClientList(Socket socket) {
        try {
            synchronized (clientList) {
                byte[] serializeModelList = ModelSerializationUtil.serializeModelList(clientList);
                socket.getOutputStream().write(serializeModelList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}