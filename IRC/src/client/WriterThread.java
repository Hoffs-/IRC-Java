package client;

/**
 * Created by Hoffs-Laptop on 2016-09-14.
 */
import java.io.*;
import java.lang.Runnable;
import java.net.Socket;

public class WriterThread implements Runnable {

    private String ip;
    private String user;
    private String token;
    private Socket irc;
    public BufferedWriter outputIRC;

    public WriterThread(String ip, String user, String token, Socket c) {
        this.ip = ip;
        this.user = user;
        this.token = token;
        this.irc = c;
    }

    public void run() {
        try {
            outputIRC = new BufferedWriter(new OutputStreamWriter(irc.getOutputStream()));
            authenticate();
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }
    public String toString(){
        return "Writer";
    }

    private void authenticate() {
        try {
            outputIRC.write("PASS " + this.token + "\r\n");
            outputIRC.write("NICK " + this.user + "\r\n");
            outputIRC.write("CAP REQ :twitch.tv/membership\r\n");
            outputIRC.write("CAP REQ :twitch.tv/commands\r\n");
            outputIRC.write("CAP REQ :twitch.tv/tags\r\n");
            outputIRC.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void join(String channel) {
        try {
            outputIRC.write("JOIN " + channel + "\r\n");
            outputIRC.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, String channel) {
        try {
            outputIRC.write("PRIVMSG " + channel + " :" + message + "\n");
            outputIRC.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exit() {
        try {
            outputIRC.write("QUIT Leaving...\r\n");
            outputIRC.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}