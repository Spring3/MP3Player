package mp3.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Class, containing the main info about the playlist
 */
public class Playlist {

    /**
     * Creates a default playlist instance
     */
    public Playlist(){
        id = new SimpleIntegerProperty();
        name = new SimpleStringProperty();
    }

    /**
     * Creates a playlist with a given name
     * @param name the name of the playlist
     */
    public Playlist(String name){
        this.name = new SimpleStringProperty(name);
        id = new SimpleIntegerProperty();
    }

    private IntegerProperty id;
    private StringProperty name;

    /**
     * Gets the id of the playlist
     * @return the id, which will be the same as in the database. Will return null if the object was not updated from the database
     */
    public int getId(){
        return id.get();
    }

    /**
     * Sets the id of the playlist
     * @param id the identifier of the playlist in the database
     */
    public void setId(int id){
        this.id.set(id);
    }

    /**
     * Gets the name of the playlist
     * @return the name of the playlist
     */
    public String getName(){
        return name.get();
    }

    /**
     * Sets the name of the playlist
     * @param name the name of the playlist
     */
    public void setName(String name){
        this.name.set(name);
    }

    /**
     * Gets the property, containing the name of the playlist
     * @return the property, containing the name of the playlist
     */
    public StringProperty getNameProperty(){
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Playlist playlist = (Playlist) o;

        if (getId() != playlist.getId()) return false;
        return getName().equals(playlist.getName());

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        return result;
    }

    @Override
    public String toString(){
        return getName();
    }

}
