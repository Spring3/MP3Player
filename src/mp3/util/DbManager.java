package mp3.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spring on 2/24/2016.
 */
public class DbManager {

    private static DbManager instance;
    private static final String DB_URI = "jdbc:sqlite:data.db";

    private DbManager (){

    }

    public static DbManager getInstance(){
        if (instance == null){
            synchronized (Object.class){
                if (instance == null){
                    instance = new DbManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URI);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return connection;
    }

    public void init(){
        try {
            createTables();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private void createTables() throws SQLException{
        List<String> queries = new ArrayList<>();
        queries.add("CREATE TABLE IF NOT EXISTS song (id integer primary key autoincrement, name varchar(60) unique, path text, duration integer, bitrate integer, quality varchar(10));");
        queries.add("CREATE TABLE IF NOT EXISTS playlist (id integer primary key autoincrement, name varchar(60) unique);");
        queries.add("CREATE TABLE IF NOT EXISTS song_playlist (song_id integer, playlist_id integer, foreign key(song_id) references song(id), foreign key(playlist_id) references playlist(id));");
        queries.add("CREATE TABLE IF NOT EXISTS album (id integer primary key autoincrement, name varchar(50) unique, imgPath text, playlist_id integer, foreign key(playlist_id) references playlist(id));");
        queries.add("CREATE TABLE IF NOT EXISTS song_album (song_id integer, album_id integer, foreign key(song_id) references song(id), foreign key(album_id) references album(id));");

        Connection connection = getConnection();

        for(String query : queries){
            Statement statement = connection.createStatement();
            statement.execute(query);
            statement.close();
        }

        connection.close();

        System.out.println("Database connection established successfully");

    }
}
