package com.andrei;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class App {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            System.out.println("Server listening on port 4221");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            String message = "HTTP/1.1 200 OK\r\n\r\n";
            outputStream.write(message.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
