package mp3.dao;

import mp3.model.Album;
import mp3.model.Playlist;
import mp3.model.Song;
import mp3.util.DbManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the playlist instance
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

    /**
     * Gets the playlist from the database by given name
     * @param name the name of the desired playlist
     * @return the playlist object if it was found. Otherwise, returns null
     */
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

    /**
     * Gets the list of albums in the playlist
     * @param value the parent playlist
     * @return the list of albums if they exist. Otherwise, returns an empty list
     */
    public List<Album> getAllAlbums(Playlist value) {
        List<Album> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM album WHERE playlist_id = ?");
            statement.setInt(1, value.getId());
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                int albumId = resultSet.getInt(1);
                String albumname = resultSet.getString(2);
                String picPath = resultSet.getString(3);
                int playListId = resultSet.getInt(4);
                Album album = new Album(albumname, picPath, playListId);
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
     * Gets all songs from the playlist
     * @param playlist the parent playlist
     * @return the list of the songs in the playlist, if there is any. Otherwise, returns empty list
     */
    public List<Song> getAllSongs(Playlist playlist){
        List<Song> result = new ArrayList<>();
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM song INNER JOIN song_playlist ON song_id = song.id WHERE playlist_id = ?");
            statement.setInt(1, playlist.getId());
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
