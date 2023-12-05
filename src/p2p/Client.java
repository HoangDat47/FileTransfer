package p2p;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;

import static p2p.FileInfo.sendFileList;

public class Client {
    private Node node;
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton sendPrivateButton;
    private JTextField privateInputField;
    private JButton exitButton;
    private JList<String> nodeList;
    private JComboBox<String> comboBox;
    private HashMap<String, JTextArea> privateChats;
    private JScrollPane currentPrivateChatScrollPane;
    private JPanel privateChatPanel;
    private JPanel fileSharingPanel;

    public Client() throws IOException {
        initComponents();
        initListeners();
        node.connect();
        node.startListening();
    }

    private void initComponents() throws IOException {
        String name = showUsernameDialog();
        privateChats = new HashMap<>();
        nodeList = new JList<>();
        comboBox = new JComboBox<>();
        currentPrivateChatScrollPane = new JScrollPane();

        JFrame frame = new JFrame("File - " + name);
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
        fileSharingPanel = new JPanel(new BorderLayout());

        String f = "src/share/" + node.getName();
        sendFileList(f, node.getName());


        return fileSharingPanel;
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
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick();
                }
            }
        });

        privateInputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendPrivateButton.doClick();
                }
            }
        });

        sendButton.addActionListener(e -> sendGeneralMessage());
        sendPrivateButton.addActionListener(e -> sendPrivateMessage());
        comboBox.addActionListener(e -> updatePrivateChat());
    }

    private void sendGeneralMessage() {
        String message = inputField.getText();
        if (!message.isEmpty() && !message.contains(":")) {
            try {
                Message msg = new Message("MULTICAST_MESSAGE", node.getName(), message, null);
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
                Message msg = new Message("PRIVATE_MESSAGE", node.getName(), message, null);
                Message.sendMessageObject(node.getSocket(), msg, node.getNodes().get(recipient));
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
