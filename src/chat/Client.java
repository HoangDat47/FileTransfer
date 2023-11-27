package chat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame {

    private Thread thread;
    private BufferedWriter os;
    private BufferedReader is;
    private Socket socketOfClient;
    private List<String> onlineList;
    private int id;

    //des components UI
    private JPanel chatPanel, filePanel, jPanel1, jPanel2, jPanel22, jPanel3;
    private JButton jButton1;
    private JComboBox<String> jComboBox1;
    private JLabel jLabel1, jLabel2, jLabel3;
    private JTabbedPane jTabbedPane1;
    private JTextArea jTextArea1, jTextArea2;
    private JTextField jTextField1;

    public Client() {
        initComponents();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setSize(600, 400);
        jTextArea1.setEditable(false);
        jTextArea2.setEditable(false);
        onlineList = new ArrayList<>();
        setUpSocket();
        id = -1;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Tạo JTabbedPane để chứa các tab
        jTabbedPane1 = new JTabbedPane();

        // Panel cho tab "Chat"
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // Panel cho tab "File"
        filePanel = new JPanel();
        filePanel.setLayout(new BorderLayout());

        // Thêm tab "Chat" vào JTabbedPane
        jTabbedPane1.addTab("Chat", chatPanel);
        jTabbedPane1.addTab("File", filePanel);

        // Khởi tạo các button
        jButton1 = new JButton();
        jButton1.setText("Gửi");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        // Khởi tạo các combobox
        jComboBox1 = new JComboBox<>();
        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        // Khởi tạo các label
        jLabel1 = new JLabel();
        jLabel1.setText("Chọn người nhận");
        jLabel2 = new JLabel();
        jLabel2.setText("Danh sách online");
        jLabel3 = new JLabel();
        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setText("{Người nhận}");

        // Khởi tạo các textarea
        jTextArea1 = new JTextArea(10, 20);
        jTextArea2 = new JTextArea();

        jTextArea1.setEditable(false);
        jTextArea2.setEditable(false);

        // Khởi tạo các textfield
        jTextField1 = new JTextField(30);

        // Khởi tạo các panel
        jPanel1 = new JPanel();
        jPanel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        jPanel2 = new JPanel();
        jPanel2.setLayout(new BorderLayout());
        jPanel22 = new JPanel();
        jPanel22.setLayout(new FlowLayout(FlowLayout.CENTER));
        jPanel3 = new JPanel();
        jPanel3.setLayout(new FlowLayout(FlowLayout.LEFT));

        // Thêm các component vào panel
        jPanel1.add(jLabel1);
        jPanel1.add(jComboBox1);

        jPanel22.add(jLabel2);
        jPanel2.add(jPanel22, BorderLayout.NORTH);
        jPanel2.add(new JScrollPane(jTextArea2), BorderLayout.CENTER);
        jPanel2.add(new JLabel("     "), BorderLayout.SOUTH);
        jPanel2.add(new JLabel("     "), BorderLayout.EAST);
        jPanel2.add(new JLabel("     "), BorderLayout.WEST);

        jPanel3.add(jTextField1);
        jPanel3.add(jButton1);
        jPanel3.add(jLabel3);

        // Thêm các panel vào chatPanel
        chatPanel.add(new JScrollPane(jTextArea1), BorderLayout.CENTER);
        chatPanel.add(jPanel1, BorderLayout.NORTH);
        chatPanel.add(jPanel2, BorderLayout.EAST);
        chatPanel.add(jPanel3, BorderLayout.SOUTH);
        chatPanel.add(new JLabel("     "), BorderLayout.WEST);

        // Thêm JTabbedPane vào BorderLayout.CENTER
        add(jTabbedPane1, BorderLayout.CENTER);

        // Tạo JFrame
        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        String messageContent = jTextField1.getText();
        if (messageContent.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa nhập tin nhắn");
            return;
        }
        if (jComboBox1.getSelectedIndex() == 0) {
            try {
                write("send-to-global" + "," + messageContent + "," + this.id);
                jTextArea1.setText(jTextArea1.getText() + "Bạn: " + messageContent + "\n");
                jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, "Có lỗi xảy ra");
            }
        } else {
            try {
                String[] parner = ((String) jComboBox1.getSelectedItem()).split(" ");
                write("send-to-person" + "," + messageContent + "," + this.id + "," + parner[1]);
                jTextArea1.setText(jTextArea1.getText() + "Bạn (tới Client " + parner[1] + "): " + messageContent + "\n");
                jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(rootPane, "Có lỗi xảy ra");
            }
        }
        jTextField1.setText("");
    }

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (jComboBox1.getSelectedIndex() == 0) {
            jLabel3.setText("Global");
        } else {
            jLabel3.setText("Đang nhắn với " + jComboBox1.getSelectedItem());
        }
    }

    private void setUpSocket() {
        try {
            thread = new Thread(() -> {

                try {
                    // Gửi yêu cầu kết nối tới Server đang lắng nghe
                    // trên máy 'localhost' cổng 7777.
                    socketOfClient = new Socket("localhost", 7777);
                    System.out.println("Kết nối thành công!");
                    // Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
                    os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
                    // Luồng đầu vào tại Client (Nhận dữ liệu từ server).
                    is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
                    String message;
                    while (true) {

                        message = is.readLine();
                        if (message == null) {
                            break;
                        }
                        String[] messageSplit = message.split(",");
                        if (messageSplit[0].equals("get-id")) {
                            setID(Integer.parseInt(messageSplit[1]));
                            setIDTitle();
                        }
                        if (messageSplit[0].equals("update-online-list")) {
                            onlineList = new ArrayList<>();
                            String online = "";
                            String[] onlineSplit = messageSplit[1].split("-");
                            for (int i = 0; i < onlineSplit.length; i++) {
                                onlineList.add(onlineSplit[i]);
                                online += "Client " + onlineSplit[i] + "\n";
                            }
                            jTextArea2.setText(online);
                            updateCombobox();
                        }
                        if (messageSplit[0].equals("global-message")) {
                            jTextArea1.setText(jTextArea1.getText() + messageSplit[1] + "\n");
                            jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
                        }
                    }
                } catch (UnknownHostException e) {
                    System.err.println("Don't know about host " + "localhost");
                } catch (IOException e) {
                    System.err.println("Couldn't get I/O for the connection to " + "localhost");
                }
            });
            thread.run();
        } catch (Exception e) {

        }
    }

    private void updateCombobox() {
        jComboBox1.removeAllItems();
        jComboBox1.addItem("Gửi tất cả");
        String idString = "" + this.id;
        for (String e : onlineList) {
            if (!e.equals(idString)) {
                jComboBox1.addItem("Client " + e);
            }
        }

    }

    private void setIDTitle() {
        this.setTitle("Client " + this.id);
    }

    private void setID(int id) {
        this.id = id;
    }

    private void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }

    public static void main(String args[]) {
        new Client();
    }
}