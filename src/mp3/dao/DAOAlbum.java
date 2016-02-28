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
 * Data access object for the album instance
 * DAO pattern of design
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
                result = new Album(name, picPath);
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

    /**
     * Gets the album from the database by the given name
     * @param name the name of the album to fetch
     * @return the album object if it was found. Otherwise, returns null
     */
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
                result = new Album(albumname, picPath);
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
                Album album = new Album(name, picPath);
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


    /**
     * Checks the database table for existence of the given song object in the album
     * @param album the album to search in
     * @param song the sound file to look for
     * @return true if such sound file was already added to the album . Otherwise - false
     */
    public boolean containsSong(Album album, Song song){
        return getAllSongs(album).contains(song);
    }

    /**
     * Gets all songs from the album
     * @param album the object of the album
     * @return the list of songs, found in the album. If none, returns an empty list
     */
    public List<Song> getAllSongs(Album album){
        List<Song> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song INNER JOIN song_album ON song_id = song.id WHERE album_id = ?");
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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO album (name, imgPath) VALUES(?, ?)");
            statement.setString(1, value.getName());
            statement.setString(2, value.getPicPath());
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
            PreparedStatement statement = connection.prepareStatement("UPDATE album SET name=?, imgPath=? WHERE id=?");
            statement.setString(1, value.getName());
            statement.setString(2, value.getPicPath());
            statement.setInt(3, value.getId());
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
