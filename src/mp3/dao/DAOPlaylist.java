package mp3.dao;

import mp3.model.Playlist;
import mp3.util.DbManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spring on 2/25/2016.
 */
public class DAOPlaylist implements IDAO<Playlist> {

    public DAOPlaylist(){

    }

    private static final DbManager manager = DbManager.getInstance();

    @Override
    public Playlist get(int id) {
        Playlist result = null;
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM playlist WHERE id=?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int playListId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                result = new Playlist(name);
                result.setId(playListId);
            }

            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }

    public Playlist get(String name) {
        Playlist result = null;
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM playlist WHERE name=?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int playListId = resultSet.getInt(1);
                String playlistname = resultSet.getString(2);
                result = new Playlist(playlistname);
                result.setId(playListId);
            }

            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Playlist> getAll() {
        List<Playlist> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM playlist");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int playListId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Playlist playlist = new Playlist(name);
                playlist.setId(playListId);
                result.add(playlist);
            }

            resultSet.close();
            statement.close();
            connection.close();
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean create(Playlist value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO playlist (name) VALUES(?)");
            statement.setString(1, value.getName());
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Playlist value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE playlist SET name=? WHERE id=?");
            statement.setString(1, value.getName());
            statement.setInt(2, value.getId());
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(Playlist value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM playlist WHERE id=?");
            statement.setInt(1, value.getId());
            statement.executeUpdate();
            statement.close();
            connection.close();
            return true;
        }
        catch (SQLException ex){
            ex.printStackTrace();
            return false;
        }
    }
}
