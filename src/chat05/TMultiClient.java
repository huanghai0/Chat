package chat05;

import java.io.*;
import java.net.Socket;

public class TMultiClient {
    public static void main(String[] args) throws IOException {
        System.out.println("---client---");
        //建立连接
        Socket client = new Socket("localhost", 8888);

        //客户端发送消息
        new Thread(new Send(client)).start();

        //客户端接收消息
        new Thread(new Receive(client)).start();

    }
}
