package ca.utoronto.utm.mcs;

import java.net.InetSocketAddress;

import java.io.IOException;

import com.sun.net.httpserver.HttpServer;


public class App {

   static int PORT = 8000;
   //final static String API_URL = "http://localhost:8004";
   public static void main(String[] args) throws IOException{
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
      server.createContext("/location", new location());
      server.createContext("/user", new user());
      server.createContext("/trip", new trip());
    
      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
