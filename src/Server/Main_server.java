package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

public class Main_server extends JFrame {
    public JTextArea tar_user;
    Hashtable<String, ClientHandler> listUser;

    public Main_server() {
        super("Server");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(400, 400);
        init();
        setVisible(true);

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        });
    }

    private void init() {
        JPanel jPanel1 = new JPanel(new BorderLayout());

        tar_user = new JTextArea(10,20);
        tar_user.setEditable(false);
        jPanel1.add(new JScrollPane(tar_user),BorderLayout.CENTER);
        jPanel1.add(new JPanel(),BorderLayout.EAST);
        jPanel1.add(new JPanel(),BorderLayout.WEST);

        add(jPanel1);
    }

    //ham sendAll dung de gui tin nhan cho tat ca cac client
    public void sendAll(String from, String msg){
        Enumeration e = listUser.keys();
        String name;
        while(e. hasMoreElements()){
            name=(String) e.nextElement();
            System.out.println(name);
            if(name.compareTo(from)!=0) listUser.get(name).sendMSG("3",msg);
        }
    }

    //hàm sendAllUpdate gởi tên tất cả client tới tất cả client
    public void sendAllUpdate(String from){
        Enumeration e = listUser.keys();
        String name;
        while(e. hasMoreElements()){
            //nextElement() trả về phần tử tiếp theo của enumeration
            name=(String) e.nextElement();
            System.out.println(name);
            //compareTo() so sánh 2 chuỗi
            if(name.compareTo(from)!=0) listUser.get(name).sendMSG("4",getAllName());
        }
    }

    //hàm getAllName trả về tên tất cả client
    public String getAllName(){
        //Enumeration là một interface, nó được sử dụng để liệt kê các phần tử của một đối tượng
        Enumeration e = listUser.keys();
        String name="";
        //hasMoreElements() trả về true nếu vẫn còn phần tử trong danh sách
        while(e. hasMoreElements()){
            name+= e.nextElement() +"\n";
        }
        return name;
    }

    private void go() {
        try {
            listUser = new Hashtable<>();
            ServerSocket serverSocket = new ServerSocket(12345);
            tar_user.append("Server is running...\n");
            System.out.println("Server is running...");
            while (!serverSocket.isClosed()) {
                Socket client = serverSocket.accept();
                new ClientHandler(this, client);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new Main_server().go();
    }
}
