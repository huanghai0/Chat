package chat05;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Send implements Runnable {
    private BufferedReader console;
    private DataOutputStream dos;
    private Socket client;
    private boolean isRunning;

    public Send(Socket client) {
        this.client = client;
        this.isRunning = true;
        console = new BufferedReader(new InputStreamReader(System.in));
        try {
            dos = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            System.out.println("===客户端发送异常");
            this.relese();
        }
    }

    private void relese() {
        this.isRunning = false;
        Utils.close(dos, client);
    }

    private String getStrFromConsole() {
        try {
            return console.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void send(String msg) {
        try {
            dos.writeUTF(msg);
            dos.flush();
        } catch (IOException e) {
            System.out.println("===客户端发送异常");
            relese();
        }
    }


    @Override
    public void run() {
        while (isRunning) {
            String msg = getStrFromConsole();
            if (!msg.equals("")) {
                send(msg);
            }
        }
    }
}
