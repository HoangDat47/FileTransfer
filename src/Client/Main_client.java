package Client;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Main_client extends JFrame {
    private Socket client;
    private DataOutputStream dos;
    private DataInputStream dis;
    public Main_client() {
        super("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                exit();
            }
        });
        setSize(400, 400);
        init();
        setVisible(true);
    }

    private void init() {

    }

    public void go() {
        try {
            client = new Socket("localhost",12345);
            dos=new DataOutputStream(client.getOutputStream());
            dis=new DataInputStream(client.getInputStream());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Không thể kết nối đến server");
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Main_client().go();
    }

    private void exit(){
        try {
            sendMSG("0");
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        System.exit(0);
    }

    //hàm sendMSG dùng để gởi tin nhắn tới server
    private void sendMSG(String data){
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
