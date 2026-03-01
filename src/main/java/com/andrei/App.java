package com.andrei;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        SimpleHttpServer server = new SimpleHttpServer(4221);
        server.start();
    }
}
