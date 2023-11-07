package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread{
    public Socket client;
    public Main_server server;
    private DataOutputStream dos;
    private DataInputStream dis;
    private String tenUser;
    private boolean run;
    public ClientHandler(Main_server server, Socket client) {
        try {
            this.server=server;
            this.client=client;
            dos= new DataOutputStream(client.getOutputStream());
            dis= new DataInputStream(client.getInputStream());
            run=true;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        // xữ lý đăng nhập
        String msg;
        while(run){
            tenUser=getMSG();

            // tenUser = 0 la thoat
            if(tenUser.compareTo("0")==0){
                logout();
            }
            else {
                if(checkNick(tenUser)){
                    sendMSG("0");
                }
                else{
                    server.tar_user.append(tenUser+" đã kết nối với room\n");
                    server.sendAll(tenUser,tenUser+" đã vào room\n");
                    server.listUser.put(tenUser, this);
                    server.sendAllUpdate(tenUser);
                    sendMSG("1");
                    diplayAllUser();
                    // xử lý tin nhắn
                    while(run){
                        int stt = Integer.parseInt(getMSG());
                        switch(stt){
                            case 0:
                                run=false;
                                server.listUser.remove(this.tenUser);
                                exit();
                                break;
                            case 1:
                                msg = getMSG();
                                server.sendAll(tenUser,tenUser+" : "+msg);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void logout() {
        try {
            dos.close();
            dis.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void exit(){
        try {
            server.sendAllUpdate(tenUser);
            dos.close();
            dis.close();
            client.close();
            server.tar_user.append(tenUser+" đã thoát\n");
            server.sendAll(tenUser,tenUser+" đã thoát\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean checkNick(String nick){
        return server.listUser.containsKey(nick);
    }
    private void sendMSG(String data){
        try {
            dos.writeUTF(data);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //voi mesg1 la stt, msg2 la data
    public void sendMSG(String msg1,String msg2){
        sendMSG(msg1);
        sendMSG(msg2);
    }
    private String getMSG(){
        String data=null;
        try {
            data=dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    //hàm diplayAllUser gởi tên tất cả client tới client hiện tại
    private void diplayAllUser(){
        String name = server.getAllName();
        sendMSG("4");
        sendMSG(name);
    }
}
