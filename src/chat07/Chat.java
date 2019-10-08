package chat07;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class Chat {
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
        private OutputStreamWriter fwriter;

        public Channel(Socket client) {
            this.client = client;
            isRunning = true;
            try {

                dis = new DataInputStream(client.getInputStream());
                dos = new DataOutputStream(client.getOutputStream());
                //获取名称
                this.name = receiveName();
                fwriter = new OutputStreamWriter(new FileOutputStream("D:\\test\\tolks_" + name + ".txt"));
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

                fwriter.write(msg + "\n");
            } catch (IOException e) {
                System.out.println("---接收异常");
                relese();
            }
            return msg;
        }

        //接收客户端名字
        private String receiveName() {
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
                fwriter.write(msg + "\n");
                dos.writeUTF(msg);
                dos.flush();
            } catch (IOException e) {
                System.out.println("---发送异常");
                relese();
            }
        }

        private void sendOthers(String msg, boolean isSys) {
            boolean isPrivate = msg.startsWith("@");
            if (isPrivate) {    //私聊 格式为 @name:
                int idx = msg.indexOf(":");
                String targetName = msg.substring(1, idx);
                msg = msg.substring(idx + 1);
                for (Channel other : all) {
                    if (other.name.equals(targetName)) {
                        other.send(this.name + "私聊你:" + msg);
                        break;
                    }
                }
            } else {

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
        }


        //释放资源
        private void relese() {
            this.isRunning = false;
            Utils.close(dis, dos, fwriter, client);
            all.remove(this);
            sendOthers(this.name + "离开了聊天室", true);
        }

        @Override
        public void run() {
            while (isRunning) {
                String msg = receive();
                if (!msg.equals("")) {
                    sendOthers(msg, false);
                }
            }
        }
    }
}