package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

public class Client extends JFrame implements ActionListener {
    private JButton send, clear, exit, login, logout;
    private JPanel loginPanel, chatPanel;
    private JTextField nick, nick1, message;
    private JTextArea msg, online;
    private JTabbedPane myTabledPane;

    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
    private DataInputStream dis;

    public Client() {
        super("Client");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        setSize(600, 400);
        iNit();
        setVisible(true);
    }

    private void iNit() {
        // Tạo JTabbedPane để chứa các tab
        myTabledPane = new JTabbedPane();

        // Panel cho tab "Chat"
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // Thêm tab "Chat" vào JTabbedPane
        myTabledPane.addTab("Chat", chatPanel);

        // Khởi tạo các button
        exit = new JButton("Thoát");
        exit.addActionListener(this);
        send = new JButton("Gửi");
        send.addActionListener(this);
        clear = new JButton("Xóa");
        clear.addActionListener(this);
        login = new JButton("Đăng nhập");
        login.addActionListener(this);
        logout = new JButton("Thoát");
        logout.addActionListener(this);

        // Panel cho thông tin người dùng và button
        JPanel p1 = new JPanel();
        p1.setLayout(new FlowLayout(FlowLayout.LEFT));
        nick = new JTextField(20);
        p1.add(new JLabel("Name : "));
        p1.add(nick);
        p1.add(exit);

        // Panel cho danh sách online
        JPanel p2 = new JPanel();
        p2.setLayout(new BorderLayout());

        JPanel p22 = new JPanel();
        p22.setLayout(new FlowLayout(FlowLayout.CENTER));
        p22.add(new JLabel("Danh sách online"));
        p2.add(p22, BorderLayout.NORTH);

        online = new JTextArea(10, 10);
        online.setEditable(false);
        p2.add(new JScrollPane(online), BorderLayout.CENTER);
        p2.add(new JLabel("     "), BorderLayout.SOUTH);
        p2.add(new JLabel("     "), BorderLayout.EAST);
        p2.add(new JLabel("     "), BorderLayout.WEST);

        // Panel cho ô nhập tin nhắn và button gửi
        msg = new JTextArea(10, 20);
        msg.setEditable(false);

        JPanel p3 = new JPanel();
        p3.setLayout(new FlowLayout(FlowLayout.LEFT));
        p3.add(new JLabel("Tin nhắn"));
        message = new JTextField(30);
        p3.add(message);
        p3.add(send);
        p3.add(clear);

        // Thêm các panel vào chatPanel
        chatPanel.add(new JScrollPane(msg), BorderLayout.CENTER);
        chatPanel.add(p1, BorderLayout.NORTH);
        chatPanel.add(p2, BorderLayout.EAST);
        chatPanel.add(p3, BorderLayout.SOUTH);
        chatPanel.add(new JLabel("     "), BorderLayout.WEST);

        // Ẩn JTabbedPane
        myTabledPane.setVisible(false);

        // Thêm JTabbedPane vào BorderLayout.CENTER
        add(myTabledPane, BorderLayout.CENTER);

        // Panel cho đăng nhập và đăng xuất
        loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        loginPanel.add(new JLabel("Nick chát : "));
        nick1 = new JTextField(20);
        loginPanel.add(nick1);
        loginPanel.add(login);
        loginPanel.add(logout);

        // Thêm loginPanel vào BorderLayout.NORTH
        add(loginPanel, BorderLayout.NORTH);
    }

    private void go() {
        try {
            client = new Socket("localhost", 1234);
            dos = new DataOutputStream(client.getOutputStream());
            dis = new DataInputStream(client.getInputStream());
            //client.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Client().go();
    }

    private void sendMSG(String data) {
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getMSG() {
        String data = null;
        try {
            data = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void getMSG(String msg1, String msg2) {
        int stt = Integer.parseInt(msg1);
        switch (stt) {
            //case 1 la tin nhan cua minh//case 2 la tin nhan cua nguoi khac
            // tin nhắn của những người khác
            case 3:
                this.msg.append(msg2);
                break;
            // update danh sách online
            case 4:
                this.online.setText(msg2);
                break;
            // server đóng cửa
            case 5:
                dataStream.stopThread();
                exit();
                break;
            // bổ sung sau
            default:
                break;
        }
    }

    private void checkSend(String msg) {
        if (msg.compareTo("\n") != 0) {
            this.msg.append("Tôi : " + msg);
            sendMSG("1");
            sendMSG(msg);
        }
    }

    private boolean checkLogin(String nick) {
        if (nick.compareTo("") == 0)
            return false;
        else if (nick.compareTo("0") == 0) {
            return false;
        } else {
            sendMSG(nick);
            int sst = Integer.parseInt(getMSG());
            if (sst == 0)
                return false;
            else return true;
        }
    }

    private void exit() {
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

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exit) {
            dataStream.stopThread();
            exit();
        } else if (e.getSource() == clear) {
            message.setText("");
        } else if (e.getSource() == send) {
            checkSend(message.getText() + "\n");
            message.setText("");
        } else if (e.getSource() == login) {
            if (checkLogin(nick1.getText())) {
                myTabledPane.setVisible(true);
                loginPanel.setVisible(false);
                nick.setText(nick1.getText());
                nick.setEditable(false);
                this.setTitle(nick1.getText());
                msg.append("Đã đăng nhập thành công\n");
                dataStream = new DataStream(this, this.dis);
            } else {
                JOptionPane.showMessageDialog(this, "Đã tồn tại níck này trong room, bạn vui lòng nhập lại.", "Message Dialog", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == logout) {
            exit();
        }
    }


}
