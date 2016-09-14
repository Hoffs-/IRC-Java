package client;

/**
 * Created by Hoffs-Laptop on 2016-09-14.
 */
import java.io.*;
import java.lang.Runnable;
import java.net.Socket;

public class ReaderThread implements Runnable {

    private String ip;
    private String user;
    private String token;
    private Socket irc;
    public BufferedReader inputIRC;

    public ReaderThread(String ip, String user, String token, Socket c) {
        this.ip = ip;
        this.user = user;
        this.token = token;
        this.irc = c;
    }

    public void run() {
        String line = null;
        try {
            inputIRC = new BufferedReader(new InputStreamReader(irc.getInputStream()));
        } catch (IOException ex) {
            System.out.println(ex);
        }

        try {
            while ((line = inputIRC.readLine( )) != null) {
                System.out.println(line);
                parseLine(line);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    private void parseLine(String line) {
        System.out.println("Parsing...");
        String noSpace[];
        String tags[];
        String type;
        String channel;
        noSpace = line.split(" ");
        if (noSpace[0].startsWith("@")) {
            tags = noSpace[0].split(";");
        }
        if (noSpace.length > 2) {
            type = noSpace[2];
            channel = noSpace[3];
        }
        for (int i = 4; i <= noSpace.length; i++) {
            System.out.print(noSpace[i] + " ");
        }
        System.out.print("\n");
    }
    public String toString(){
        return "Reader";
    }
}