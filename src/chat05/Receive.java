package chat05;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;


public class Receive implements  Runnable {
    private DataInputStream dis;
    private Socket client;
    private boolean isRunning;
    public Receive(Socket client){
        this.client = client;
        this.isRunning = true;
        try {
            dis = new DataInputStream(client.getInputStream());
        }catch (IOException e){
            System.out.println("===客户端接收异常");
            relese();
        }
    }
    //接收消息
    private String receive() {
        String msg = "";
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
            System.out.println("===接收异常");
            relese();
        }
        return msg;
    }
    private void relese() {
        this.isRunning = false;
        Utils.close(dis, client);
    }

    @Override
    public void run() {
        while (isRunning){
            String msg = receive();
            if(!msg.equals("")){
                System.out.println(msg);
            }
        }
    }
}
