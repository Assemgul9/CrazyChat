package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
    private static Connection connection;
    private static Statement statement;
    private static final String CREATE_REQUEST = "create table if not exists clients" +
            "(id integer primary key autoincrement, login text unique, password text unique, nickname text unique);";
    private  static final String SIMPLE_INSERT_REQUEST = "insert into clients (login, password, nickname) values ('qwe', 'qwe','qwe'),('asd','asd','asd');";


    public static void main(String[] args) {
        try {
            connect();
            createTable();
            insertRequest();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:db/clients.db");
        statement = connection.createStatement();

    }
    public static void createTable() throws SQLException{
        statement.executeUpdate(CREATE_REQUEST);


    }
    public static void insertRequest() throws SQLException{
        statement.executeUpdate(SIMPLE_INSERT_REQUEST);
    }
}

