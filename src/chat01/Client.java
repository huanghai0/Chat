package chat01;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        System.out.println("---client---");
        //建立连接
        Socket client = new Socket("localhost",8888);
        //创建键盘输入流
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String msg = console.readLine();
        //客户端发送消息
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        dos.writeUTF(msg);
        dos.flush();
        //客户端接收消息
        DataInputStream dis = new DataInputStream(client.getInputStream());
        msg = dis.readUTF();
        System.out.println(msg);

        dos.close();
        dis.close();
        client.close();
    }
}
