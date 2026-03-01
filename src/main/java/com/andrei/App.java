package com.andrei;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;

public class App {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(4221);
            serverSocket.setReuseAddress(true);
            System.out.println("Server listening on port 4221");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String requestURL = in.readLine().split(" ")[1];

            //in.lines().forEach(System.out::println);

            if(requestURL.equals("/")){
                out.write("HTTP/1.1 200 OK\r\n\r\n");
            } else if (requestURL.startsWith("/echo/")) {
                String text = requestURL.substring(6);
                out.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + text.length() +"\r\n\r\n" + text);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n");
            }

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
