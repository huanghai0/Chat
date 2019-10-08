package chat06;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("---client---");
        //建立连接
        Socket client = new Socket("localhost", 8888);

        BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入用户名：");
        String name = br.readLine();

        //客户端发送消息
        new Thread(new Send(client,name)).start();

        //客户端接收消息
        new Thread(new Receive(client)).start();

    }
}
