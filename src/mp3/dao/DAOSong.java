package mp3.dao;

import mp3.model.Album;
import mp3.model.Playlist;
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
public class DAOSong implements IDAO<Song>{

    public DAOSong(){
    }

    private static final DbManager manager = DbManager.getInstance();


    @Override
    public Song get(int id) {
        Song result = null;
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song WHERE id=?");
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int songId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String path = resultSet.getString(3);
                int duration = resultSet.getInt(4);
                int bitrate = resultSet.getInt(5);
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(5));
                result = new Song(name, path, duration, bitrate, quality);
                result.setId(songId);

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

    public Song get(String name) {
        Song result = null;
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song WHERE name=?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int songId = resultSet.getInt(1);
                String songname = resultSet.getString(2);
                String path = resultSet.getString(3);
                int duration = resultSet.getInt(4);
                int bitrate = resultSet.getInt(5);
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(5));
                result = new Song(songname, path, duration, bitrate, quality);
                result.setId(songId);

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
    public List<Song> getAll() {
        List<Song> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song");
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int songId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String path = resultSet.getString(3);
                int duration = resultSet.getInt(4);
                int bitrate = resultSet.getInt(5);
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(5));
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

    public List<Song> getAll(Playlist playlist) {
        List<Song> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song INNER JOIN song_playlist as sp ON sp.song_id = song.id INNER JOIN playlist ON playlist.id = ? ");
            statement.setInt(1, playlist.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int songId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String path = resultSet.getString(3);
                int duration = resultSet.getInt(4);
                int bitrate = resultSet.getInt(5);
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(5));
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

    public List<Song> getAll(Album album) {
        List<Song> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song INNER JOIN song_album as sa ON sa.song_id = song.id INNER JOIN album ON album.id = ? ");
            statement.setInt(1, album.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int songId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String path = resultSet.getString(3);
                int duration = resultSet.getInt(4);
                int bitrate = resultSet.getInt(5);
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(5));
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
    public boolean create(Song value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO song (name, path, duration, bitrate, quality) VALUES(?, ?, ?, ?, ?);");
            statement.setString(1, value.getName());
            statement.setString(2, value.getPath());
            statement.setInt(3, value.getDuration());
            statement.setInt(4, value.getBitrate());
            statement.setString(5, value.getQuality().name());
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
    public boolean update(Song value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE song SET name = ?, path = ?, duration = ?, bitrate = ?, quality = ? WHERE id = ?");
            statement.setString(1, value.getName());
            statement.setString(2, value.getPath());
            statement.setInt(3, value.getDuration());
            statement.setInt(4, value.getBitrate());
            statement.setString(5, value.getQuality().name());
            statement.setInt(6, value.getId());
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
    public boolean delete(Song value) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM song WHERE id=?");
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
