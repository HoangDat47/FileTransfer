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
    private JPanel jPanel1;
    public JTextArea tar_user;
    private ServerSocket serverSocket;
    private Hashtable<String, ClientHandler> listUser;

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
        jPanel1 = new JPanel(new BorderLayout());

        tar_user = new JTextArea(10,20);
        tar_user.setEditable(false);
        jPanel1.add(new JScrollPane(tar_user),BorderLayout.CENTER);
        jPanel1.add(new JPanel(),BorderLayout.EAST);
        jPanel1.add(new JPanel(),BorderLayout.WEST);

        add(jPanel1);
    }

    public void sendAll(String from, String msg){
        Enumeration e = listUser.keys();
        String name;
        while(e. hasMoreElements()){
            name=(String) e.nextElement();
            System.out.println(name);
            if(name.compareTo(from)!=0) listUser.get(name).sendMSG("3",msg);
        }
    }

    private void go() {
        try {
            listUser = new Hashtable<>();
            serverSocket = new ServerSocket(12345);
            tar_user.append("Server is running...\n");
            System.out.println("Server is running...");
            while (true) {
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
