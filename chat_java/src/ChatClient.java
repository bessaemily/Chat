import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_IP = "192.168.8.32";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            System.out.println("Conectado ao servidor. Digite suas mensagens:");

            // Thread para receber mensagens
            new Thread(() -> {
                String serverMessage;
                try {
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println("\nMensagem recebida: " + serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("Você foi desconectado do servidor.");
                }
            }).start();

            // Enviando mensagens
            String userInput;
            while ((userInput = inputReader.readLine()) != null) {
                out.println(userInput);
            }

        } catch (IOException e) {
            System.out.println("Erro na conexão: " + e.getMessage());
        }
    }
}
