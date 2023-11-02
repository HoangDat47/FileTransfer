package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainServer extends Thread{
    public static final int SERVER_PORT = 12345;
    private final List<ClientModel> clients = new ArrayList<>();

    public static void main(String[] args) {
        MainServer server = new MainServer();
        server.start();
    }

    //tcp p2p hybrid server
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server started on port " + SERVER_PORT);

            while (!serverSocket.isClosed()) {
                Socket connectionSocket = serverSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                String clientMessage = inFromClient.readLine();
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                boolean clientExists = false;
                ClientModel clientModel = new ClientModel(connectionSocket.getInetAddress(), connectionSocket.getPort());
                for (ClientModel client : clients) {
                    if (client.equals(clientModel)) {
                        clientExists = true;
                        break;
                    }
                }
                if (!clientExists) {
                    clients.add(clientModel);
                    System.out.println("Client connected " + clientModel.getAddress() + ":" + clientModel.getPort());
                }

                // Gửi danh sách clients đến máy khách sau khi họ kết nối
                sendClientList(outToClient);

                if (clientMessage.equals("exit")) {
                    System.out.println("Client disconnected");
                    removeClient(clientModel);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendClientList(DataOutputStream outToClient) throws IOException {
        byte[] clientListBytes = Serialization.serializeModelList(clients);
        outToClient.write(clientListBytes);
    }


    private void removeClient(ClientModel clientModel) {
        clients.remove(clientModel);
    }

}
