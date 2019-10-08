package chat04;

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

            new Thread(() -> {
                DataInputStream dis = null;
                DataOutputStream dos = null;
                try {
                    dis = new DataInputStream(client.getInputStream());
                    dos = new DataOutputStream(client.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean isRunning = true;
                while (isRunning) {
                    try {
                        String msg = dis.readUTF();
                        dos.writeUTF(msg);
                        dos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        isRunning = false;
                    }
                }

                try {
                    if (null == dos) {
                        dos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (null == dis) {
                        dis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (null == client) {
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        }
    }
}