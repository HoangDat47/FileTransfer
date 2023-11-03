import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

class ServerThread extends Thread {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public ServerThread(Socket socket) {
        this.clientSocket = socket;
    }

    public void run() {
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

            // Gửi danh sách các người dùng đến client mới kết nối
            List<Socket> clients = Server.getClients();
            for (Socket client : clients) {
                out.writeUTF(client.getInetAddress().getHostAddress());
            }

            // Lắng nghe và chuyển tiếp tin nhắn từ client này đến tất cả các client khác
            while (true) {
                String message = in.readUTF();
                if (message.equals("exit")) {
                    clients.remove(clientSocket);
                    clientSocket.close();
                    break;
                }
                for (Socket client : clients) {
                    if (client != clientSocket) {
                        DataOutputStream clientOut = new DataOutputStream(client.getOutputStream());
                        clientOut.writeUTF(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}