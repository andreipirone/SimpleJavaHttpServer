package com.andrei;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SimpleHttpServer {
    private final int port;

    SimpleHttpServer(int port){
        this.port = port;
    }

    void start(){
        try(ServerSocket serverSocket = new ServerSocket(this.port)) {
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
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))){
            List<String> requestLines = new ArrayList<>();

            long startTime = System.currentTimeMillis();
            System.out.println("Thread " + Thread.currentThread().getId() + " started at: " + startTime);
            //Thread.sleep(5000);

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
            } else if (requestURL.startsWith("/files/")) {
                String fileName = requestURL.substring(7);

                BufferedOutputStream bos = new BufferedOutputStream(clientSocket.getOutputStream());
                ClassLoader classLoader = getClass().getClassLoader();
                try{
                    File file = new File(classLoader.getResource(fileName).getFile());
                    long size = file.length();
                    String response = "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=UTF-8\r\nContent-Length: " + size +"\r\n\r\n";
                    //out.write(response);
                    bos.write(response.getBytes(), 0, response.length());
                    bos.flush();
                    this.sendFile(file, bos);
                } catch (NullPointerException e) {
                    out.write("HTTP/1.1 404 Not Found\r\n\r\n");
                }
            } else {
                out.write("HTTP/1.1 404 Not Found\r\n\r\n");
            }
            System.out.println("Thread " + Thread.currentThread().getId() + " finished at: " + (System.currentTimeMillis() - startTime) + "ms");
        } catch (IOException e){
            System.out.println("IOException: " + e.getMessage());
        }
    }

    public void sendFile(File file, BufferedOutputStream out) throws IOException {
        InputStream fileInputStream = Files.newInputStream(file.toPath());
        byte[] buffer = new byte[4 * 1024];
        int bytes;
        while ((bytes=fileInputStream.read(buffer))!=-1){
            out.write(buffer,0,bytes);
            out.flush();
        }
        fileInputStream.close();
    }
}
