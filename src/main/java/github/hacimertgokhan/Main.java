package github.hacimertgokhan;

import github.hacimertgokhan.denisdb.CreateSecureToken;
import github.hacimertgokhan.logger.DDBLogger;
import github.hacimertgokhan.readers.ReadDDBProp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {
    static DDBLogger ddbLogger = new DDBLogger(Main.class);
    static ReadDDBProp readDDBProp = new ReadDDBProp();
    static int PORT = Integer.parseInt(readDDBProp.getProperty("ddb-port"));
    static String TOKEN = String.valueOf(readDDBProp.getProperty("ddb-token"));

    public static void main(String[] args) {
        ddbLogger.warn("Welcome to DDB, create cache based database with '-use' or manage your ddb with '-man'");
        Scanner scanner = new Scanner(System.in);
        String mode = scanner.nextLine();
        if (mode.equalsIgnoreCase("-use")) {
            if (TOKEN.length() == 128) {
                scanner.close();
                try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                    ddbLogger.info("Server running on port " + PORT);
                    while (true) {
                        ddbLogger.info("Waiting for client connection...");
                        Socket clientSocket = serverSocket.accept();
                        ddbLogger.info("Client connected: " + clientSocket.getInetAddress());
                        new Thread(() -> {
                            DDBServer ddbServer = new DDBServer(clientSocket);
                            ddbServer.handleClient(clientSocket);
                        }).start();
                    }
                } catch (IOException e) {
                    ddbLogger.error("IOException occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                ddbLogger.error("You cannot use DDB without correct token value. (Token length must be 128)");
            }
        } else if (mode.equalsIgnoreCase("-man")) {
            ddbLogger.warn("Hello, you're in Management mode, Please becareful.");
            String[] actCommand = scanner.nextLine().split(" ");
            boolean man = true;
            if(actCommand==null) {
                ddbLogger.error("Commands cannot be null.");
                return;
            }
            while (man) {
                switch (actCommand[0]) {
                    case "--exit" -> {
                        ddbLogger.error("You're leaving DDB Man mode, Bye bye.");
                        man = false;
                    }
                    case "--create" -> {
                        if (actCommand.length == 2) {
                            if (actCommand[1].equals("-token")) {
                                CreateSecureToken createSecureToken = new CreateSecureToken();
                                String token = createSecureToken.getToken();
                                ddbLogger.info("Your unique DDB token created successfully;");
                                ddbLogger.info(token);
                                break;
                            }
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + actCommand);
                }
            }

        }
    }



}