package com.example.demo.demo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    public TextField textField;
    @FXML
    public TextArea textArea;
    @FXML
    public PasswordField passwordField;
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public HBox MessagePanel;
    @FXML
    public ListView<String> clientList;



    private  Socket socket;
    private  DataOutputStream out;
    private  DataInputStream in;
    private  final String ADDRESS = "localhost";
    private  final int  PORT = 8189;

    private boolean authenticated;
    private String nickname;
    private Stage stage;
    private Stage regStage;
    private RegController regController;

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authPanel.setVisible(!authenticated);
        authPanel.setManaged(!authenticated);
        MessagePanel.setVisible(authenticated);
        MessagePanel.setManaged(authenticated);
        clientList.setManaged(true);
        clientList.setManaged(true);

        if(!authenticated){
            nickname = "";
        }

        setTitle(nickname);
        textArea.clear();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage)  textField.getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                System.out.println("Bye");
                if(socket != null && socket.isClosed()){
                    try {
                        out.writeUTF("/end");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        });
        setAuthenticated(false);

    }

    @FXML
    public void clickButton(ActionEvent actionEvent) {

        if (textField.getText().length()>0) {
            try {
                out.writeUTF(textField.getText());
                textField.clear();
                textField.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    public void connect (){
        try {
            socket = new Socket(ADDRESS, PORT);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {

                    //цикл аутентификации

                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                break;
                            }
                            if (str.startsWith("/authOK")) {
                                nickname = str.split(" ")[0];
                                setAuthenticated(true);
                                break;
                            }
                            if (str.startsWith("/reg")) {
                                regController.regStatus(str);

                            }else {
                                textArea.appendText(str);
                            }

                        }
                    }

                    //цикл работы
                    while (authenticated) {
                        String str = in.readUTF();



                        if (str.startsWith("/")) {
                            if (str.equals("/end")) {
                                setAuthenticated(false);
                                break;
                            }
                            if(str.startsWith("/clientList")){
                                String[] token = str.split(" ");
                                Platform.runLater(() -> {
                                    clientList.getItems().clear();
                                    for (int i = 1; i < token.length; i++) {
                                        clientList.getItems().add(token[i]);
                                    }

                                });
                            }
                            if(str.startsWith("/yournickis ")){
                                nickname = str.split(" ")[1];
                                setTitle(nickname);
                            }




                        } else {

                            textArea.appendText(str + "\n");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
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

    public void clickButtonAuth(ActionEvent actionEvent) {
        if(socket == null || socket.isClosed()){
            connect();
        }

        try{
            String message = String.format("/auth %s %s",
                    loginField.getText().trim(),passwordField.getText().trim());
                    out.writeUTF(message);
                    passwordField.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void setTitle(String nickname){
        String title;
        if(nickname.equals("")){
            title = "Chat";
        }else {
            title = String.format("Chat = %s", nickname);
        }

        Platform.runLater(() -> {

            stage.setTitle(title);

        });
    }

    public void clickClientList(MouseEvent mouseEvent) {
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText("/w" +" " + receiver + " ");

    }

    public void clickButtonReg(ActionEvent actionEvent) {

        if(regStage == null){
            createRegWindow();
        }
        regStage.show();


    }

    private void createRegWindow(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("Chat registration");
            regStage.setScene(new Scene(root,600, 245));
            regStage.initModality(Modality.APPLICATION_MODAL);
            regStage.initStyle(StageStyle.UTILITY);

            regController =fxmlLoader.getController();
            regController.setHelloController(this);

            regStage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void tryToReg(String login, String password, String nickname){
        if (socket==null||socket.isClosed()){
            connect();
        }
        String msg = String.format("/reg %s %s %s",login, password, nickname);
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}