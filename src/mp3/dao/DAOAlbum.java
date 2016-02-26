package mp3.dao;

import mp3.model.Album;
import mp3.model.Song;
import mp3.util.DbManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Spring on 2/25/2016.
 */
public class DAOAlbum implements IDAO<Album> {

    public DAOAlbum(){

    }

    private static final DbManager manager = DbManager.getInstance();

    @Override
    public Album get(int id) {
        Album result = null;
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM album WHERE id=?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int albumId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String picPath = resultSet.getString(3);
                int playListId = resultSet.getInt(4);
                result = new Album(name, picPath, playListId);
                result.setId(albumId);
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

    public Album get(String name) {
        Album result = null;
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM album WHERE name=?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int albumId = resultSet.getInt(1);
                String albumname = resultSet.getString(2);
                String picPath = resultSet.getString(3);
                int playListId = resultSet.getInt(4);
                result = new Album(albumname, picPath, playListId);
                result.setId(albumId);
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
    public List<Album> getAll() {
        List<Album> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM album");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int albumId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String picPath = resultSet.getString(3);
                int playlistId = resultSet.getInt(4);
                Album album = new Album(name, picPath, playlistId);
                album.setId(albumId);
                result.add(album);
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

    public List<Song> getAllSongs(Album album){
        List<Song> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song INNER JOIN song_album ON song_id = song.id WHERE album_id=?");
            statement.setInt(1, album.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int songId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String path = resultSet.getString(3);
                int duration = resultSet.getInt(4);
                int bitrate = resultSet.getInt(5);
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(6));
                Song song = new Song(name, path, duration, bitrate, quality);
                song.setId(songId);
                result.add(song);
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
    public boolean create(Album value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO album (name, imgPath, playlist_id) VALUES(?, ?, ?)");
            statement.setString(1, value.getName());
            statement.setString(2, value.getPicPath());
            statement.setInt(3, value.getPlayList().getId());
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
    public boolean update(Album value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE album SET name=?, imgPath=?, playlist_id=? WHERE id=?");
            statement.setString(1, value.getName());
            statement.setString(2, value.getPicPath());
            statement.setInt(3, value.getPlayList().getId());
            statement.setInt(4, value.getId());
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
    public boolean delete(Album value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM album WHERE id=?");
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
