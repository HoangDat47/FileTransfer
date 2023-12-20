package p2p;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Client {
    private Node node;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton, sendPrivateButton, exitButton, updateButton, printListButton, refreshButton;
    private JTextField privateInputField;
    private JList<String> nodeList;
    private JComboBox<String> comboBox;
    private HashMap<String, JTextArea> privateChats;
    private List<FileInfo> fileInfoList;
    private JScrollPane currentPrivateChatScrollPane;
    private JPanel privateChatPanel;
    private DefaultTableModel tableModel;
    private JTable tableFileSharing;

    public Client() throws IOException {
        initComponents();
        initListeners();
        fileInfoList = new ArrayList<>();
        updateLocalFileList();
        node.connect();
        node.startListening();
    }

    private void initComponents() throws IOException {
        String name = showUsernameDialog();
        privateChats = new HashMap<>();
        nodeList = new JList<>();
        comboBox = new JComboBox<>();
        currentPrivateChatScrollPane = new JScrollPane();

        JFrame frame = new JFrame("File transfer - " + name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        chatArea = createChatArea(name);
        node = new Node(name, this);
        JTabbedPane tabbedPane = createTabbedPane();
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private JTextArea createChatArea(String name) {
        JTextArea chat = new JTextArea();
        chat.setEditable(false);
        return chat;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setSize(800, 100);

        JPanel generalChat = createGeneralChatPanel();
        JPanel privateChatPanel = createPrivateChatPanel();
        JPanel fileSharingPanel = createFileSharingPanel();

        tabbedPane.add("General", generalChat);
        tabbedPane.add("Private", privateChatPanel);
        tabbedPane.add("File Sharing", fileSharingPanel);

        return tabbedPane;
    }

    private JPanel createGeneralChatPanel() {
        JPanel generalChat = new JPanel(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(nodeList);
        Dimension preferredSize = new Dimension(200, 600);

        JPanel bottomPanel = createBottomPanel();

        generalChat.add(scrollPane, BorderLayout.EAST);
        generalChat.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        generalChat.add(bottomPanel, BorderLayout.SOUTH);

        scrollPane.setPreferredSize(preferredSize);

        return generalChat;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        sendButton = new JButton("Send");
        exitButton = new JButton("Exit");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        bottomPanel.add(exitButton, BorderLayout.WEST);

        return bottomPanel;
    }

    private JPanel createPrivateChatPanel() {
        privateChatPanel = new JPanel(new BorderLayout());
        JPanel bottomSendPanel = new JPanel(new BorderLayout());

        sendPrivateButton = new JButton("Send");
        privateInputField = new JTextField();

        bottomSendPanel.add(privateInputField, BorderLayout.CENTER);
        bottomSendPanel.add(sendPrivateButton, BorderLayout.EAST);

        privateChatPanel.add(comboBox, BorderLayout.NORTH);
        privateChatPanel.add(bottomSendPanel, BorderLayout.SOUTH);

        return privateChatPanel;
    }

    private JPanel createFileSharingPanel() {
        JPanel fileSharingPanel = new JPanel(new BorderLayout());
        JPanel btnPanel = new JPanel(new FlowLayout());
        updateButton = new JButton("Update");
        printListButton = new JButton("Print List");
        refreshButton = new JButton("Refresh");

        btnPanel.add(updateButton);
        btnPanel.add(printListButton);
        btnPanel.add(refreshButton);
        fileSharingPanel.add(btnPanel, BorderLayout.NORTH);
        fileSharingPanel.add(new JScrollPane(createFileSharingTable()), BorderLayout.CENTER);

        //thêm sự kiện click vào 1 hàng trong bảng
        tableFileSharing.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = tableFileSharing.getSelectedRow();
                if (selectedRow != -1) {
                    // Lấy thông tin từ hàng được chọn
                    String fileName = (String) tableModel.getValueAt(selectedRow, 0);
                    String fileOwner = (String) tableModel.getValueAt(selectedRow, 1);
                    String fileSize = (String) tableModel.getValueAt(selectedRow, 2);
                    String status = (String) tableModel.getValueAt(selectedRow, 3);

                    if (Objects.equals(status, "Đã tải về")) {
                        // Hiển thị thông tin hoặc thực hiện các hành động khác tùy thuộc vào nhu cầu của bạn
                        JOptionPane.showMessageDialog(null, "Selected File:\n" +
                                "Name: " + fileName + "\n" +
                                "Owner: " + fileOwner + "\n" +
                                "Size: " + fileSize + "\n" +
                                "Status: " + status);
                    } else {
                        // Hien len thong bao ban co muon tai file ve khong neu co thi gui yeu cau lay file
                        int result = JOptionPane.showConfirmDialog(null, "Selected File:\n" +
                                "Name: " + fileName + "\n" +
                                "Owner: " + fileOwner + "\n" +
                                "Size: " + fileSize + "\n" +
                                "Status: " + status + "\n" +
                                "Do you want to download this file?", "Download", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            Message requestFileMessage = new Message("REQUEST_FILE", node.getName(), fileName);
                            try {
                                Message.sendMessageObject(node.getSocket(), requestFileMessage, node.getNodes().get(fileOwner));
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        return fileSharingPanel;
    }

    private JTable createFileSharingTable() {
        tableModel = new DefaultTableModel();
        tableFileSharing = new JTable(tableModel);

        tableModel.addColumn("File Name");
        tableModel.addColumn("File owner");
        tableModel.addColumn("File size");
        tableModel.addColumn("Status");

        return tableFileSharing;
    }

    private void refreshTable() {
        clearTable();
        for (FileInfo fileInfo : fileInfoList) {
            int fileSize = fileInfo.getFileSize();
            String fileSizeString = FileInfo.formatBytes(fileSize);
            String status = FileInfo.checkFileInsideFolder("src/share/" + node.getName(), fileInfo.getFileName());
            tableModel.addRow(new Object[]{fileInfo.getFileName(), fileInfo.getFileOwner(), fileSizeString, status});
        }
    }

    private void clearTable() {
        // Lấy số lượng dòng hiện tại trong bảng
        int rowCount = tableModel.getRowCount();

        // Xóa tất cả các dòng từ cuối cùng đến đầu
        for (int i = rowCount - 1; i >= 0; i--) {
            tableModel.removeRow(i);
        }
    }

    private void initListeners() {
        exitButton.addActionListener(e -> {
            try {
                node.disconnect();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        privateInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendPrivateButton.doClick();
                }
            }
        });

        sendButton.addActionListener(e -> sendGeneralMessage());
        sendPrivateButton.addActionListener(e -> sendPrivateMessage());
        comboBox.addActionListener(e -> updatePrivateChat());
        updateButton.addActionListener(e -> fileManager());
        printListButton.addActionListener(e -> printList());
        refreshButton.addActionListener(e -> refreshTable());
    }

    private void printList() {
        System.out.println("--------------------------");
        for (FileInfo fileInfo : fileInfoList) {
            System.out.println(fileInfo.toString("\\|"));
        }
    }

    private void updateLocalFileList() {
        String localFolderPath = "src/share/" + node.getName();
        fileInfoList = FileInfo.getFileInsideFolder(localFolderPath, node.getName());
    }

    private void fileManager() {
        String folderPath = "src/share/" + node.getName();

        if (!new File(folderPath).exists()) {
            new File(folderPath).mkdirs();
        } else {
            try {
                // Retrieve file information and update the local list
                List<FileInfo> receivedFiles = FileInfo.getFileInsideFolder(folderPath, node.getName());

                for (FileInfo receivedFile : receivedFiles) {
                    int existingIndex = fileInfoList.indexOf(receivedFile);

                    if (existingIndex != -1) {
                        // Update the existing entry
                        fileInfoList.set(existingIndex, receivedFile);
                    } else {
                        // Add the new entry to the list
                        fileInfoList.add(receivedFile);
                    }
                }

                if (!fileInfoList.isEmpty()) {
                    String fileInfoString = Utils.listToString(fileInfoList, "\\|");
                    //System.out.println("File Info String: " + fileInfoString); // Debugging output
                    Message fileInfoMessage = new Message("FILE_INFO_LIST", node.getName(), fileInfoString);
                    Message.broadcast(node.getMulticastSocket(), fileInfoMessage, node.getGroup());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateFileList(String content) {
        List<FileInfo> receivedFiles = Utils.stringToList(content, "\\|");

        for (FileInfo receivedFile : receivedFiles) {
            int existingIndex = fileInfoList.indexOf(receivedFile);

            if (existingIndex != -1) {
                // Update the existing entry
                fileInfoList.set(existingIndex, receivedFile);
            } else {
                // Add the new entry to the list
                fileInfoList.add(receivedFile);
            }
        }

        // Print or display the updated file list
        //System.out.println("Updated File List: " + Utils.listToString(fileInfoList, "\\|"));
    }

    private void sendGeneralMessage() {
        String message = inputField.getText();
        if (!message.isEmpty() && !message.contains(":")) {
            try {
                Message msg = new Message("MULTICAST_MESSAGE", node.getName(), message);
                Message.broadcast(node.getMulticastSocket(), msg, node.getGroup());
                chatArea.append("You: " + message + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        inputField.setText("");
    }

    private void sendPrivateMessage() {
        String message = privateInputField.getText();
        String recipient = (String) comboBox.getSelectedItem();
        if (!message.isEmpty() && !message.contains(":")) {
            try {
                Message msg = new Message("PRIVATE_MESSAGE", node.getName(), message);
                Message.sendMessageObject(node.getSocket(), msg, node.getNodes().get(recipient));
                System.out.println("Sending private message to " + node.getNodes().get(recipient));
                privateChats.get(recipient).append("You: " + message + "\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        privateInputField.setText("");
    }

    private void updatePrivateChat() {
        String selectedRecipient = (String) comboBox.getSelectedItem();
        if (selectedRecipient != null) {
            if (!privateChats.containsKey(selectedRecipient)) {
                // Create a new private chat area
                JTextArea privateChatArea = new JTextArea();
                privateChatArea.setEditable(false);
                privateChats.put(selectedRecipient, privateChatArea);
            }

            if (currentPrivateChatScrollPane != null) {
                privateChatPanel.remove(currentPrivateChatScrollPane);
            }

            currentPrivateChatScrollPane = new JScrollPane(privateChats.get(selectedRecipient));
            privateChatPanel.add(currentPrivateChatScrollPane, BorderLayout.CENTER);
            privateChatPanel.revalidate();
            privateChatPanel.repaint();
        }
    }

    public void updateNodeList() {
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String nodeName : node.getNodes().keySet()) {
            listModel.addElement(nodeName);
        }
        nodeList.setModel(listModel);
    }

    public void updateComboBox() {
        String[] list = node.getNodes().keySet().toArray(new String[0]);
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(list);
        comboBox.setModel(model);
    }

    public void updatePrivateChatArea(String message, String sender) {
        if (!privateChats.containsKey(sender)) {
            JTextArea privateChat = new JTextArea();
            privateChat.setEditable(false);
            privateChats.put(sender, privateChat);
        }
        privateChats.get(sender).append(message + "\n");
    }

    public void updateChatArea(String message) {
        chatArea.append(message + "\n");
    }

    public void sendingFile(String fileName, int recipientPort) {
        //gui thong bao cho nguoi nhan biet la se gui file
        Message msg = new Message("SEND_FILE", node.getName(), fileName);
        try {
            Message.sendMessageObject(node.getSocket(), msg, recipientPort);
            // Introduce a delay or synchronization here to ensure the recipient processes the message
            Thread.sleep(1000); // Add an appropriate delay (1 second in this example)
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Start the file transfer after a delay or synchronization
        FileInfo.sendFile(node.getSocket(), "src/share/" + node.getName() + "/" + fileName, recipientPort);
    }

    public void receivingFile(String fileName, int senderPort) {
        //tao mot thread moi de nhan file su dung ham receiveFile o FileInfo
        new Thread(() -> {
            FileInfo.receiveFile(node.getSocket(), "src/share/" + node.getName() + "/" + fileName, senderPort);
        }).start();
    }

    private String showUsernameDialog() {
        String username = "";
        JTextField usernameField = new JTextField(20);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Enter your name:"));
        panel.add(usernameField);
        int result = JOptionPane.showConfirmDialog(null, panel, "Username", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            username = usernameField.getText();
        } else {
            System.exit(0);
        }

        return username;
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new Client();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
