package mp3.model;

/**
 * Created by Spring on 2/25/2016.
 */
public class Playlist {

    public Playlist(){

    }

    public Playlist(String name){
        setName(name);
    }

    private int id;
    private String name;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
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
