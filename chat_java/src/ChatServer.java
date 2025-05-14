import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final String SERVER_IP = "192.168.8.32";
    private static final int SERVER_PORT = 5000;
    private static final Set<Socket> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
            System.out.println("[*] Servidor escutando em " + SERVER_IP + ":" + SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clients.add(clientSocket);
                System.out.println("[*] Conexão recebida de " + clientSocket.getInetAddress().getHostAddress());

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String message = clientSocket.getInetAddress().getHostAddress() + ": " + inputLine;
                System.out.println("[*] Recebido: " + message);
                broadcast(message, clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Cliente desconectado.");
        } finally {
            try {
                clients.remove(clientSocket);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void broadcast(String message, Socket sender) {
        for (Socket client : clients) {
            if (!client.equals(sender)) {
                try {
                    PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                    out.println(message);
                } catch (IOException e) {
                    try {
                        client.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    clients.remove(client);
                }
            }
        }
    }
}