import client.ReaderThread;
import client.WriterThread;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Hoffs-Laptop on 2016-09-14.
 */
public class Client {
    private String ircIp;
    private String ircUser;
    private String ircToken;
    private Socket socketIRC;
    ReaderThread ircReader;
    WriterThread ircWriter;
    ThreadPoolFixed poolFixed;

    public Client(String ip, String user, String token, ThreadPoolFixed pool) {
        this.ircIp = ip;
        this.ircUser = user;
        this.ircToken = token;
        this.poolFixed = pool;
    }

    public void connect() {
        try {
            socketIRC = new Socket(ircIp, 6667);
            ircReader = new ReaderThread(ircIp, ircUser, ircToken, socketIRC);
            ircWriter = new WriterThread(ircIp, ircUser, ircToken, socketIRC);
            poolFixed.startThread(ircReader);
            poolFixed.startThread(ircWriter);
            Thread.sleep(2000);
            ircWriter.join("#" + this.ircUser);
            Thread.sleep(2000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
