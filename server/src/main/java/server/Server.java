package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private   ServerSocket serverSocket;
    private  Socket socket;
    private  final int  PORT = 8189;

    private List<ClientHandler> clients;
    private AuthService authService;


    public Server() {

        clients = new CopyOnWriteArrayList<>();
        //authService = new SimpleAuthService();
        if(!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к базам данных");
        }authService = new DataBaseAuthService();



        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started");


            while (true){
                socket = serverSocket.accept();
                System.out.println("Client connected" + socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            SQLHandler.disconnect();
            System.out.println("Server stop");

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessageToAll(ClientHandler sender, String msg) {

        String message = String.format("[%s] : %s", sender.getNickname(), msg);

        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }

    }
    public void privateMessage(ClientHandler sender, String receiver, String msg) {

        String message = String.format("[%s] to [%s]: %s", sender.getNickname(), receiver, msg);

        for (ClientHandler c : clients) {
            if (c.getNickname().equals(receiver)) {
               c.sendMessage(message);
                if (!sender.getNickname().equals(receiver)) {
                    sender.sendMessage(message);
                }
                return;
            }
        }

        sender.sendMessage("not found user: " + receiver);
    }

    public boolean isLoginAuthenticated(String login){
        for(ClientHandler c : clients){
            if(c.getLogin().equals(login)){
                return true;
            }

        }return  false;
    }

    public void sendMessageToClientList() {

       StringBuilder sb = new StringBuilder("/clientList");

       for(ClientHandler c: clients){
           sb.append(" ").append(c.getNickname());

       }
        String message = sb.toString();

        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }

    }

    public  void subscribe(ClientHandler clientHandler){

        clients.add(clientHandler);
        sendMessageToClientList();
    }

    public void  unsubscribe(ClientHandler clientHandler){

        clients.remove(clientHandler);
        sendMessageToClientList();
    }

    public AuthService getAuthService() {
        return authService;
    }
}
