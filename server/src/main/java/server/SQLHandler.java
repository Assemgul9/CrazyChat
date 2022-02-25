package server;

import java.sql.*;

public class SQLHandler {
    private  static Connection connection;
    private static PreparedStatement psGetNickName;
    private static PreparedStatement psChangeNick;
    private static PreparedStatement psRegistration;

    public static boolean connect(){
        try {
          connection= DriverManager.getConnection("jdbc:sqlite:db/clients.db");
          prepareAllStatements();
          return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void prepareAllStatements()throws SQLException{
        psGetNickName = connection.prepareStatement("select nickname from clients where login = ? AND password = ?;");
        psRegistration = connection.prepareStatement("insert into clients(login, password, nickname) values (?, ?, ?);");
        psChangeNick = connection.prepareStatement("update clients set nickname = ? where nickname = ?;");

    }
    public static String getNickByLoginAndPassword(String login, String password){
        String nick = null;
        try{
            psGetNickName.setString(1, login);
            psGetNickName.setString(2, password);
            ResultSet rs = psGetNickName.executeQuery();
            if (rs.next()){
                nick = rs.getString(1);
            }rs.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return nick;
    }
    public static boolean  registration(String login, String password, String nickname){
        try {
            psRegistration.setString(1, login);
            psRegistration.setString(2, password);
            psRegistration.setString(3, nickname);
            psRegistration.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changeNick(String oldNickname, String newNickname){
        try{
            psChangeNick.setString(1, newNickname);
            psChangeNick.setString(2, oldNickname);
            psChangeNick.executeUpdate();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    public static void disconnect(){
        try {
            psChangeNick.close();
            psRegistration.close();
            psGetNickName.close();
        }catch (SQLException e){
            e.printStackTrace();

        }
        try {
            connection.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
