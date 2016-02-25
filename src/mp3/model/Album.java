package mp3.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import mp3.dao.DAOPlaylist;

/**
 * Created by Spring on 2/25/2016.
 */
public class Album {

    public Album(){
        id = new SimpleIntegerProperty();
        name = new SimpleStringProperty();
        picPath = new SimpleStringProperty();
        playlistId = new SimpleIntegerProperty();
    }

    public Album(String name, String picPath, Playlist playlist){
        this.name = new SimpleStringProperty(name);
        this.picPath = new SimpleStringProperty(picPath);
        this.playlistId = new SimpleIntegerProperty(playlist.getId());
        id = new SimpleIntegerProperty();
    }

    public Album(String name, String picPath, int playlistId){
        this.name = new SimpleStringProperty(name);
        this.picPath = new SimpleStringProperty(picPath);
        this.playlistId = new SimpleIntegerProperty(playlistId);
        id = new SimpleIntegerProperty();
    }

    private IntegerProperty id;
    private StringProperty name;
    private StringProperty picPath;
    private IntegerProperty playlistId;


    public int getId(){
        return id.get();
    }

    public void setId(int id){
        this.id.set(id);
    }

    public String getName(){
        return name.get();
    }

    public void setName(String name){
        this.name.set(name);
    }

    public String getPicPath(){
        return picPath.get();
    }

    public void setPicPath(String path){
        picPath.set(path);
    }

    public Playlist getPlayList(){
        return new DAOPlaylist().get(playlistId.get());
    }

    public void setPlaylistId(int id){
        this.playlistId.set(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (getId() != album.getId()) return false;
        if (playlistId != album.playlistId) return false;
        if (getName() != null ? !getName().equals(album.getName()) : album.getName() != null) return false;
        return !(getPicPath() != null ? !getPicPath().equals(album.getPicPath()) : album.getPicPath() != null);

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPicPath() != null ? getPicPath().hashCode() : 0);
        result = 31 * result + playlistId.get();
        return result;
    }

}
