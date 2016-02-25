package mp3.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Spring on 2/25/2016.
 */
public class Playlist {

    public Playlist(){
        id = new SimpleIntegerProperty();
        name = new SimpleStringProperty();
    }

    public Playlist(String name){
        this.name = new SimpleStringProperty(name);
        id = new SimpleIntegerProperty();
    }

    private IntegerProperty id;
    private StringProperty name;

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

}
