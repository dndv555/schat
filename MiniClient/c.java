import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class c {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            
            System.out.print("Server IP: ");
            String server = scanner.nextLine();
            if (server.isEmpty()) server = "example.com";
            
            System.out.print("Port: ");
            String portStr = scanner.nextLine();
            int port = portStr.isEmpty() ? 8190: Integer.parseInt(portStr);
            
            System.out.print("Name: ");
            String username = scanner.nextLine();
            
            
            Socket socket = new Socket(server, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            out.println(username);
            System.out.println("Connected! /exit to exit");
            
         
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server");
                }
            }).start();
            
            
            String input;
            while (!(input = scanner.nextLine()).equals("/exit")) {
                out.println(input);
            }
            
            socket.close();
            scanner.close();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}