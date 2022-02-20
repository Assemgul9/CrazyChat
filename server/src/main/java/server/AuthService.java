package server;

public interface AuthService {

    /**
     * Получение никнэйма по логину и паролю
     * возвращает никнэйм, если учетка есть
     * null, если пары логин-пароль не нашлось
     */
    String getNicknameByLoginPassword(String login, String password);

    /**
     * Регистрация нового пользователя;
     * при успешной регистрации (логин и никнэйм не заняты) вернет true;
     * иначе вернет false;
     */

    boolean registration(String login, String password, String nickname);

    boolean changeNick(String oldNick, String newNick);
}
