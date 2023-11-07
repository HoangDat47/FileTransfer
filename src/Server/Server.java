package Server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.*;

public class Server extends JFrame implements ActionListener {

    private JButton closeBtn;
    public JTextArea TAUser;
    public JPanel jPanel1;
    private ServerSocket serverSocket;
    public Hashtable<String, ClientConnect> listUser;

    public Server() {
        super("Chat Chit : Server");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    //gởi tin nhắn tới tất cả client
                    serverSocket.close();
                    System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        setSize(400, 400);
        iNit();
        setVisible(true);
    }

    private void iNit() {
        jPanel1 = new JPanel(new BorderLayout());

        TAUser = new JTextArea(10, 20);
        TAUser.setEditable(false);

        closeBtn = new JButton("Close Server");
        closeBtn.addActionListener(this);

        //add(new JLabel("Trạng thái server : \n"), BorderLayout.NORTH);
        jPanel1.add(new JPanel(), BorderLayout.EAST);
        jPanel1.add(new JPanel(), BorderLayout.WEST);
        jPanel1.add(new JScrollPane(TAUser), BorderLayout.CENTER);
        jPanel1.add(closeBtn, BorderLayout.SOUTH);

        TAUser.append("Máy chủ đã được mở.\n");

        add(jPanel1);
    }

    private void go() {
        try {
            listUser = new Hashtable<>();
            serverSocket = new ServerSocket(1234);
            TAUser.append("Máy chủ bắt đầu phục vụ\n");
            while (true) {
                Socket client = serverSocket.accept();
                new ClientConnect(this, client);
            }
        } catch (IOException e) {
            TAUser.append("Không thể khởi động máy chủ\n");
        }
    }

    public static void main(String[] args) {
        new Server().go();
    }

    public void actionPerformed(ActionEvent e) {
        try {
            serverSocket.close();
        } catch (IOException e1) {
            TAUser.append("Không thể dừng được máy chủ\n");
        }
        System.exit(0);
    }

    //hàm sendAll gởi tin nhắn tới tất cả client
    public void sendAll(String from, String msg) {
        Enumeration e = listUser.keys();
        String name;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            System.out.println(name);
            if (name.compareTo(from) != 0) listUser.get(name).sendMSG("3", msg);
        }
    }

    //hàm sendAllUpdate gởi tên tất cả client tới tất cả client
    public void sendAllUpdate(String from) {
        Enumeration e = listUser.keys();
        String name;
        while (e.hasMoreElements()) {
            name = (String) e.nextElement();
            System.out.println(name);
            if (name.compareTo(from) != 0) listUser.get(name).sendMSG("4", getAllName());
        }
    }

    //hàm getAllName trả về tên tất cả client
    public String getAllName() {
        //Enumeration là một interface, nó được sử dụng để liệt kê các phần tử của một đối tượng
        Enumeration e = listUser.keys();
        String name = "";
        while (e.hasMoreElements()) {
            name += e.nextElement() + "\n";
        }
        return name;
    }

}
