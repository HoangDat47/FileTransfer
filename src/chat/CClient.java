package chat;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class CClient extends JFrame {

    private Thread thread;
    private BufferedWriter os;
    private BufferedReader is;
    private Socket socketOfClient;
    private List<String> onlineList;
    private int id;

    //des components UI
    private JPanel jPanel1, jPanel2, jPanel3, jPanel33, jPanel4;
    private JButton jButton1;
    private JComboBox<String> jComboBox1;
    private JLabel jLabel1, jLabel2, jLabel3;
    private JScrollPane jScrollPane1, jScrollPane2;
    private JTabbedPane jTabbedPane1;
    private JTextArea jTextArea1, jTextArea2;
    private JTextField jTextField1;

    public CClient() {
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
        jPanel3 = new JPanel();
        jTabbedPane1 = new JTabbedPane();
        jPanel1 = new JPanel();
        jScrollPane2 = new JScrollPane();
        jTextArea2 = new JTextArea();
        jPanel2 = new JPanel();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        jTextField1 = new JTextField();
        jButton1 = new JButton();
        jComboBox1 = new JComboBox<>();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();

        GroupLayout jPanel3Layout = new GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        /*jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jScrollPane2, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 425, Short.MAX_VALUE)
        );*/

        jTabbedPane1.addTab("Danh sách online", jPanel1);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setText("Gửi");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jComboBox1.addActionListener(this::jComboBox1ActionPerformed);

        jLabel1.setText("Chọn người nhân");

        jLabel2.setText("Nhập tin nhắn");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel3.setText("{Người nhận}");

        jPanel2.add(jLabel3);
        jPanel2.add(jScrollPane1);
        jPanel2.add(jScrollPane2);
        jPanel2.add(jLabel1);
        jPanel2.add(jComboBox1);
        jPanel2.add(jLabel2);
        jPanel2.add(jTextField1);
        jPanel2.add(jButton1);


        jTabbedPane1.addTab("Nhắn tin", jPanel2);
        add(jTabbedPane1, BorderLayout.CENTER);

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
        new CClient();
    }
}