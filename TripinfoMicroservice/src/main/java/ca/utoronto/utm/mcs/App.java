package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;


public class App {
   static int PORT = 8000;

   public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
      HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT),0);
      server.createContext("/trip/request", new ReqHandler());
      server.createContext("/trip/confirm", new ConfirmHandler());
      server.createContext("/trip", new AddHandler());
      server.createContext("/trip/driver", new DriverHandler());
      server.createContext("/trip/passenger", new PassengerHandler());
      server.createContext("/trip/driverTime", new TimeHandler());
      server.createContext("/trip/resetDB", new Reset());

      server.start();
      System.out.printf("Server started on port %d...\n", PORT);
   }
}
