package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{
    public Socket client;
    public Main_server server;
    private DataOutputStream dos;
    private DataInputStream dis;
    private boolean run;
    public ClientHandler(Main_server server, Socket client) {
        try {
            this.server=server;
            this.client=client;
            dos= new DataOutputStream(client.getOutputStream());
            dis= new DataInputStream(client.getInputStream());
            run=true;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMSG(String msg1,String msg2){
        sendMSG(msg1);
        sendMSG(msg2);
    }

    private void sendMSG(String data){
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
