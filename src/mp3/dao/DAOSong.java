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
 * Data access object for the song instance
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
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(6));
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

    /**
     * Searches for the song instance by the name
     * @param name the name of the song
     * @return the song object if it was found. Otherwise, returns null
     */
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
                Song.Quality quality = Song.Quality.valueOf(resultSet.getString(6));
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
            return false;
        }
    }

    /**
     * Marks the song file as added to the given playlist
     * @param playlist the playlist that will be the parent for the song file
     * @param song the song that will be connected to the playlist
     * @return true if the song was added to the playlist. Otherwise returns false
     */
    public boolean addToPlaylist(Playlist playlist, Song song) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO song_playlist VALUES(?, ?);");
            statement.setInt(1, song.getId());
            statement.setInt(2, playlist.getId());
            statement.executeUpdate();

            statement.close();
            connection.close();
            return true;
        }
        catch (SQLException ex){
            return false;
        }
    }

    /**
     * Marks the song file as added to the given playlist
     * @param album the album that will be the parent for the song file
     * @param song the song that will be connected to the album
     * @return true if the song was added to the album. Otherwise returns false
     */
    public boolean addToAlbum(Album album, Song song) {
        try{
            Connection connection = manager.getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO song_album VALUES(?, ?);");
            statement.setInt(1, song.getId());
            statement.setInt(2, album.getId());
            statement.executeUpdate();

            statement.close();
            connection.close();
            return true;
        }
        catch (SQLException ex){
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
