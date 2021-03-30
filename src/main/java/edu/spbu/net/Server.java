package edu.spbu.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class Server {
    private final int port;
    private final String directory;
    private static final String NOT_FOUND_MESSAGE = "NOT FOUND";

    public Server(int port, String directory) {
        this.port = port;
        this.directory = directory;
    }

    void start() {
        try (ServerSocket server = new ServerSocket(this.port)) {
            System.out.println("Server started!");

            while (true) {
                try (Socket socket = server.accept();
                     PrintStream writer = new PrintStream(socket.getOutputStream());
                     Scanner reader = new Scanner(socket.getInputStream());
                ) {
                    String url = getRequestUrl(reader);
                    Path filePath = Path.of(this.directory,url);
                    if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                        byte[] fileBytes = Files.readAllBytes(filePath);
                        this.sendResponse(writer, 200, "OK", "text/html", fileBytes.length);
                        writer.write(fileBytes);
                    } else {
                        this.sendResponse(writer, 404, "Not Found", "text/plain", NOT_FOUND_MESSAGE.length());
                        writer.write(NOT_FOUND_MESSAGE.getBytes());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getRequestUrl(Scanner reader) {
        String request = reader.nextLine();
        System.out.println("Request: " + request);
        return request.split(" ")[1];
    }

    private void sendResponse(PrintStream writer, int statusCode, String statusText, String type, long length) {
        writer.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        System.out.printf("Response: HTTP/1.1 %s %s%n", statusCode, statusText);
        writer.printf("Content-Type: %s%n", type);
        System.out.printf("          Content-Type: %s%n", type);
        writer.printf("Content-Length: %s%n%n", length);
        System.out.printf("          Content-Length: %s%n%n", length);
        writer.flush();
    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        String directory = args[1];
        new Server(port, directory).start();
    }
}