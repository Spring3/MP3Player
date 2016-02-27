package mp3.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The class for setting up the database
 */
public class DbManager {

    //singleton instance
    private static DbManager instance;
    //jdbc uri
    private static final String DB_URI = "jdbc:sqlite:data.db";

    private DbManager (){
    }

    /**
     * Get the singleton instance of the database manager
     * @return the singleton instance of the database manager
     */
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

    /**
     * Gets the new connection to the database
     * @return the new connection to the previously initialized database
     */
    public Connection getConnection(){
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URI);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return connection;
    }

    /**
     * Initializes the database. Creates the main tables.
     */
    public void init(){
        try {
            createTables();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Creates tables for the main entities, used in this app.
     * @throws SQLException if the sql query is incorrect.
     */
    private void createTables() throws SQLException{
        List<String> queries = new ArrayList<>();
        queries.add("CREATE TABLE IF NOT EXISTS song (id integer primary key autoincrement, name varchar(60) unique, path text, duration integer, bitrate integer, quality varchar(10));");
        queries.add("CREATE TABLE IF NOT EXISTS playlist (id integer primary key autoincrement, name varchar(60) unique);");
        queries.add("CREATE TABLE IF NOT EXISTS song_playlist (song_id integer, playlist_id integer, foreign key(song_id) references song(id), foreign key(playlist_id) references playlist(id));");
        queries.add("CREATE TABLE IF NOT EXISTS album (id integer primary key autoincrement, name varchar(50), imgPath text, playlist_id integer, foreign key(playlist_id) references playlist(id));");
        queries.add("CREATE TABLE IF NOT EXISTS song_album (song_id integer, album_id integer, foreign key(song_id) references song(id), foreign key(album_id) references album(id));");

        Connection connection = getConnection();

        for(String query : queries){
            //create a statement
            Statement statement = connection.createStatement();
            //execute the query
            statement.execute(query);
            statement.close();
        }

        connection.close();

        System.out.println("Database connection established successfully");

    }
}
