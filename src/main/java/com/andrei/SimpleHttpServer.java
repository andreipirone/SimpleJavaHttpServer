package com.andrei;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SimpleHttpServer {
    private int port;

    SimpleHttpServer(int port){
        this.port = port;
    }

    void start(){
        try(ServerSocket serverSocket = new ServerSocket(this.port);) {
            //serverSocket.setReuseAddress(true);
            System.out.println("Server listening on port 4221");
            while(true){
                Socket clientSocket = serverSocket.accept();
                //System.out.println("Accepted connection from " + clientSocket.getRemoteSocketAddress());
                Thread t = new Thread(() -> this.clientHandler(clientSocket));
                t.start();
            }

            //System.out.println("Closing connection...");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    void clientHandler(Socket clientSocket) {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));){
            List<String> requestLines = new ArrayList<>();

            long startTime = System.currentTimeMillis();
            System.out.println("Thread " + Thread.currentThread().getId() + " started at: " + startTime);
            Thread.sleep(5000);

            String line;
            while((line = in.readLine()) != null){
                if(line.isEmpty()){
                    break;
                }
                requestLines.add(line);
                //System.out.println(line);
            }

            String requestURL = requestLines.get(0).split(" ")[1];

            if(requestURL.equals("/")){
                out.write("HTTP/1.1 200 OK\r\n\r\n");
            } else if (requestURL.startsWith("/echo/")) {
                String text = requestURL.substring(6);
                out.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + text.length() +"\r\n\r\n" + text);
            } else if (requestURL.equals("/user-agent")) {
                String text = requestLines.get(2).split(":", 2)[1].trim();
                out.write("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: " + text.length() +"\r\n\r\n" + text);
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n");
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " finished at: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (IOException e){
            System.out.println("IOException: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
