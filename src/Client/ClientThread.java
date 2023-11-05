package Client;

import java.io.DataInputStream;

public class ClientThread extends Thread{
    private boolean run;
    private Main_client client;
    private DataInputStream dis;

    public ClientThread(Main_client client, DataInputStream dis) {
        this.run = true;
        this.client = client;
        this.dis = dis;
        this.start();
    }

    @Override
    public void run() {

    }

    public void stopThread(){
        this.run=false;
    }
}
