package Server;

import Example.ClientConnect;

import javax.swing.*;
import java.net.ServerSocket;
import java.util.Hashtable;

public class MainServer{
    private JButton close;
    public JTextArea user;
    private ServerSocket server;
    public Hashtable<String, ClientConnect> listUser;
}
