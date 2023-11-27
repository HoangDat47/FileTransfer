package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    public static volatile ServerThreadBus serverThreadBus;
    public static Socket socketOfServer;

    public static void main(String[] args) {
        ServerSocket listener = null;
        serverThreadBus = new ServerThreadBus();
        System.out.println("Server is waiting to accept user...");

        // Mở một ServerSocket tại cổng 7777.
        try {
            listener = new ServerSocket(7777);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );
        try {
            while (true) {
                // Chấp nhận một yêu cầu kết nối từ phía Client.
                // Đồng thời nhận được một đối tượng Socket tại server.
                socketOfServer = listener.accept();

                // Đọc số thứ tự từ client
                int clientNumber = readClientNumber(socketOfServer);

                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber);
                serverThreadBus.add(serverThread);
                System.out.println("Số thread đang chạy là: " + serverThreadBus.getLength());
                executor.execute(serverThread);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static int readClientNumber(Socket socket) throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String message = is.readLine();
        if (message != null && message.startsWith("get-")) {
            try {
                // Chuỗi có dạng "get-123"
                String numberPart = message.substring(4);
                return Integer.parseInt(numberPart.trim());
            } catch (NumberFormatException e) {
                // Xử lý nếu không thể chuyển đổi thành số nguyên
                e.printStackTrace();
            }
        }

        // Trả về giá trị mặc định hoặc làm thêm xử lý tùy thuộc vào yêu cầu của bạn
        return 0;
    }


}

