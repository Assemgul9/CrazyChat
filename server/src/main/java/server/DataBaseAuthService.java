package server;

public class DataBaseAuthService implements AuthService {

    @Override
    public String getNicknameByLoginPassword(String login, String password) {
        return SQLHandler.getNickByLoginAndPassword(login, password);
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        return SQLHandler.registration(login, password,nickname);
    }

    @Override
    public boolean changeNick(String oldNick, String newNick) {
        return SQLHandler.changeNick(oldNick, newNick);
    }
}
