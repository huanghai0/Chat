package chat05;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TMultiChat {
    public static void main(String[] args) throws IOException {
        System.out.println("---server---");
        ServerSocket server = new ServerSocket(8888);

        while (true) {
            Socket client = server.accept();
            System.out.println("一个Client建立了连接");

            new Thread(new Channel(client)).start();
        }
    }

    //一个客户代表一个Channel
    static class Channel implements Runnable{
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket client;
        private  boolean isRunning;

        public Channel(Socket client) {
            this.client = client;
            isRunning = true;
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                relese();
            }
        }

        //接收消息
        private String receive() {
            String msg = "";
            try {
                msg = dis.readUTF();
            } catch (IOException e) {
                System.out.println("---接收异常");
                relese();
            }
            return msg;
        }

        //发送消息
        private void send(String msg) {
            try{
                dos.writeUTF(msg);
                dos.flush();
            }catch (IOException e){
                System.out.println("---发送异常");
                relese();
            }
        }

        //释放资源
        private void relese() {
            this.isRunning = false;
            Utils.close(dis, dos, client);
        }
        @Override
        public void run(){
            while (isRunning){
                String msg = receive();
                if(!msg.equals("")){
                    send(msg);
                }
            }
        }
    }
}