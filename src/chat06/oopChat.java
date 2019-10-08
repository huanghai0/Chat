package chat06;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class oopChat {
    private static CopyOnWriteArrayList<Channel> all = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws IOException {

        System.out.println("---server---");
        ServerSocket server = new ServerSocket(8888);

        while (true) {
            Socket client = server.accept();
            System.out.println("一个Client建立了连接");
            Channel c = new Channel(client);
            all.add(c);//管理所有的成员
            new Thread(c).start();
        }
    }

    //一个客户代表一个Channel
    static class Channel implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket client;
        private boolean isRunning;
        private String name;

        public Channel(Socket client) {
            this.client = client;
            isRunning = true;
            try {
                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                //获取名称
                this.name = receive();
                this.send("欢迎你的到来");
                sendOthers(this.name + "进入了聊天室！", true);
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
            try {
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                System.out.println("---发送异常");
                relese();
            }
        }

        //
        private void sendOthers(String msg, boolean isSys) {
            for (Channel other : all) {
                if (other == this) {
                    continue;
                }
                if (!isSys) {
                    other.send(this.name + ":" + msg);
                } else {
                    other.send("系统消息:" + msg);
                }
            }
        }

        //释放资源
        private void relese() {
            this.isRunning = false;
            Utils.close(dis, dos, client);
            all.remove(this);
            sendOthers(this.name + "离开了聊天室", true);
        }

        @Override
        public void run() {
            while (isRunning) {
                String msg = receive();
                if (!msg.equals("")) {
                    //send(msg);
                    sendOthers(msg, false);
                }
            }
        }
    }
}