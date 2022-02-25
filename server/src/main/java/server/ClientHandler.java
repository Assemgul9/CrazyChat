package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private  DataOutputStream out;
    private  DataInputStream in;
    private boolean authenticated;
    private String nickname;
    private String login;


    public ClientHandler (Server server, Socket socket){

        this.server = server;
        this.socket = socket;


        try {

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());




            new Thread(() -> {

                try {
                    socket.setSoTimeout(5000);


                    //цикл аутентификации
                    while (true) {

                        String str = in.readUTF();

                        if (str.equals("/end")) {
                            sendMessage("/end");
                            break;
                        }
                        if (str.startsWith("/auth")) {
                            String[] token = str.split(" ", 3);
                            if (token.length < 3) {
                                continue;
                            }
                            String newNick = server.getAuthService().getNicknameByLoginPassword(token[1], token[2]);
                            if (newNick != null) {
                                login = token[1];
                                if (!server.isLoginAuthenticated(login)) {
                                    authenticated = true;
                                    nickname = newNick;
                                    sendMessage("/authOK" + nickname);
                                    server.subscribe(this);
                                    System.out.println("Client: " + nickname + " authenticated");
                                    break;
                                } else {
                                    sendMessage("/Этот логин уже используется \n");
                                }

                            } else {
                                sendMessage("/Неверный логин/пароль \n");
                            }
                        }
                        if (str.startsWith("/reg")) {
                            String[] token = str.split(" ", 4);
                            if (token.length < 4) {
                                continue;
                            }
                            if (server.getAuthService().registration(token[1], token[2], token[3])) {
                                sendMessage("/reg OK");

                            } else {
                                sendMessage("/reg NO");

                            }


                        }
                    }

                    socket.setSoTimeout(0);
                    // цикл работы
                    while (authenticated) {

                        String str = in.readUTF();
                        if (str.startsWith("/")) {

                            if (str.equals("/end")) {
                                sendMessage("/end");
                                break;
                            }

                            if (str.startsWith("/w")) {

                                String[] token = str.split(" ", 3);
                                if (token.length < 3) {
                                    continue;
                                }
                                server.privateMessage(this, token[1], token[2]);

                            }
                            if(str.startsWith("/chnick ")){
                                String [] token = str.split ("\\s+", 2);
                                if(token.length<2){
                                    continue;
                                }
                                if (token[1].contains(" ")){
                                    sendMessage("Ник не может содержать пробелов");
                                    continue;
                                }
                                if(server.getAuthService().changeNick(this.nickname, token[1])){
                                    sendMessage("/yournickis "+ token[1]);
                                    sendMessage("Ваш ник изменен на " + token[1]);
                                    this.nickname = token[1];
                                    server.sendMessageToClientList();

                                } else {
                                    sendMessage("Не удалось изменить ник");
                                }
                            }
                        } else {
                            server.sendMessageToAll(this, str);
                        }

                    }




                }catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    sendMessage("/end");



                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                    System.out.println("Client disconnect!");
                    server.unsubscribe(this);

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }).start();




        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message){
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname(){
        return nickname;
    }

    public String getLogin() {
        return login;
    }
}
