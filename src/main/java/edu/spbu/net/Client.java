package edu.spbu.net;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final int port;
    private final String host;
    private static final String URL = "index.html";

    public Client(int port, String host){
        this.port = port;
        this.host = host;
    }

    void start(){
        try (Socket socket = new Socket(this.host, this.port);
             PrintStream writer = new PrintStream(socket.getOutputStream());
             Scanner reader = new Scanner(socket.getInputStream());
        ) {
            System.out.println("Connected to server!");
            String request = "GET /" + URL + " HTTP/1.1\r\nHost: " + this.host + "\r\nConnection: close\r\n\r\n\"";
            writer.println(request);
            writer.flush();
            System.out.println("Request: " + request);
            String response = reader.nextLine();
            System.out.println("Response: " + response);
            while (reader.hasNextLine()){
                System.out.println(reader.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        String host = args[1];
        new Client(port, host).start();
    }
}