package Client;

import Server.ClientModel;
import Server.Serialization;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class ClientThread extends Thread{
    private Socket clientSocket;
    private List<ClientModel> clients;

    public ClientThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    sendExitMessage();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }));

            while(true) {
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String receiveMessage = inFromServer.readLine();
                if(receiveMessage.startsWith("message")) {
                    System.out.println(receiveMessage.substring(7));
                } else if(receiveMessage.startsWith("clientList")) {
                    clients = Serialization.deserializeModelList(receiveMessage.getBytes());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) throws IOException {
        for (ClientModel client : clients) {
            if(client.getPort() != clientSocket.getPort()) {
                Socket socket = new Socket(client.getAddress(), client.getPort());
                DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
                outToServer.writeBytes(message);
            }
        }
    }

    public void sendExitMessage() throws IOException {
        if(clientSocket != null && !clientSocket.isClosed()) {
            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            outToServer.writeBytes("exit");
            clientSocket.close();
        }
    }

    public void receiveClientList() throws IOException {
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes("requestClientList");
    }
}
