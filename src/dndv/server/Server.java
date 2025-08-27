package dndv.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Properties;

public class Server {
    private static int PORT = 8190;
    private static String HOST = "0.0.0.0"; 
    private static Set<ClientHandler> clientHandlers = new HashSet<>();
    private static final String CONFIG_FILE = "server.properties";

    public static void main(String[] args) {
 
        loadConfig();
        
        System.out.println("Server starting...");
		System.out.println("If you find a bug, please let me know by email. Email: dndv_dev@hotmail.com");
        
        try (ServerSocket serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(HOST))) {
            System.out.println("Server started on " + HOST + ":" + PORT);
           
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
    
    private static void loadConfig() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);
        

        if (!configFile.exists()) {
            createDefaultConfig();
            
        }
        
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            
        
            HOST = props.getProperty("server.host", "0.0.0.0");
            PORT = Integer.parseInt(props.getProperty("server.port", "8190"));
            
      
            if (PORT < 1 || PORT > 65535) {
                System.out.println("");
                PORT = 8190;
            }
            
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error reading config: " + e.getMessage());
            System.out.println("Starting to use default config");
        }
    }
    
    private static void createDefaultConfig() {
        Properties props = new Properties();
        props.setProperty("server.host", "0.0.0.0");
        props.setProperty("server.port", "8190");

        
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            props.store(output, "Server Config File");
        } catch (IOException e) {
            System.out.println("Failed to create config file: " + e.getMessage());
        }
    }
    
    public static void broadcastMessage(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }
    
    public static void removeClient(ClientHandler client) {
        clientHandlers.remove(client);
        System.out.println("Client disconnected. Clients remaining: " + clientHandlers.size());
    }
    
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                

                username = in.readLine();
                System.out.println(username + " joined the chat.");
                
                Server.broadcastMessage("SERVER: " + username + " joined the chat.", this);
                
                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println(username + ": " + clientMessage);
                    Server.broadcastMessage(username + ": " + clientMessage, this);
                }
            } catch (IOException e) {
                System.out.println("Client processing error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Error closing socket: " + e.getMessage());
                }
                Server.removeClient(this);
                if (username != null) {
                    Server.broadcastMessage("SERVER: " + username + " left the chat.", this);
                }
            }
        }
        
        public void sendMessage(String message) {
            out.println(message);
        }
    }
}