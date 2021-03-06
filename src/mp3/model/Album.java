package mp3.model;

import javafx.beans.property.*;
import mp3.dao.DAOAlbum;
import mp3.dao.DAOSong;

import java.util.List;

/**
 * The class, containing the main info about the album instance
 */
public class Album implements SongsContainer {

    /**
     * Creates the default album instance
     */
    public Album(){
        id = new SimpleIntegerProperty();
        name = new SimpleStringProperty("");
        picPath = new SimpleStringProperty("");
    }

    /**
     * Creates the album instance with the given parameters
     * @param name the name of the album
     * @param picPath the uri path to the album cover
     * @param playlist the parent playlist of this album
     */
    public Album(String name, String picPath, Playlist playlist){
        this.name = new SimpleStringProperty(name);
        this.picPath = new SimpleStringProperty(picPath);
        id = new SimpleIntegerProperty();
    }

    /**
     * Creates the album instance with the given parameters
     * @param name the name of the album
     * @param picPath the uri path to the album cover
     */
    public Album(String name, String picPath){
        this.name = new SimpleStringProperty(name);
        this.picPath = new SimpleStringProperty(picPath);
        id = new SimpleIntegerProperty();
    }

    private IntegerProperty id;
    private StringProperty name;
    private StringProperty picPath;

    /**
     * Gets the id of the playlist
     * @return the id of the playlist, if it was updated from the database. Otherwise, returns null
     */
    public int getId(){
        return id.get();
    }

    /**
     * Sets the value of the id of the album
     * @param id the id of the album in the database
     */
    public void setId(int id){
        this.id.set(id);
    }

    /**
     * Gets the name of the album
     * @return returns the name of the album
     */
    public String getName(){
        return name.get();
    }

    /**
     * Gets the property, containing the name of the album
     * @return returns the property, containing the name of the album
     */
    public StringProperty getNameProperty(){
        return name;
    }

    /**
     * Sets the name of the album
     * @param name the name of the album
     */
    public void setName(String name){
        this.name.set(name);
    }

    /**
     * Gets the uri path to the album cover file
     * @return uri path to the album cover file
     */
    public String getPicPath(){
        return picPath.get();
    }

    /**
     * Sets the uri path to the album cover file
     * @param path uri path to the album cover file
     */
    public void setPicPath(String path){
        picPath.set(path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (getId() != album.getId()) return false;
        if (getName() != null ? !getName().equals(album.getName()) : album.getName() != null) return false;
        return !(getPicPath() != null ? !getPicPath().equals(album.getPicPath()) : album.getPicPath() != null);

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPicPath() != null ? getPicPath().hashCode() : 0);
        return result;
    }

    @Override
    public String toString(){
        return getName();
    }

    @Override
    public synchronized List<Song> getSongs() {
        DAOAlbum dao = new DAOAlbum();
        return dao.getAllSongs(dao.get(getName()));
    }

    @Override
    public synchronized boolean assignSong(Song song) {
        DAOAlbum dao = new DAOAlbum();
        return new DAOSong().addToAlbum(dao.get(getName()), song);
    }

    @Override
    public boolean containsSong(SongsContainer container, Song song) {
        return new DAOAlbum().containsSong((Album)container, song);
    }
}
