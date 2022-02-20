package com.example.demo.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class RegController {
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField nicknameField;
    @FXML
    public TextArea textArea;
    @FXML

  private HelloController helloController;

    public void setHelloController(HelloController helloController) {
        this.helloController = helloController;
    }
    @FXML
    public void clickButtonReg(ActionEvent actionEvent) {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nickname = nicknameField.getText().trim();
        helloController.tryToReg(login, password, nickname);

    }


    public void regStatus(String result){
        if(result.equals("/reg OK")){
            textArea.appendText("Регистрация прошла успешно");
        }else{
            textArea.appendText("Регистрация не получилась, логин или никнэйм заняты");
        }
    }
}
