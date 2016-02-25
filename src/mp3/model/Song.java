package mp3.model;

import javafx.beans.property.*;

/**
 * Created by Spring on 2/25/2016.
 */
public class Song {

    public enum Quality{
        BAD,
        MEDIUM,
        GOOD
    }

    public Song(){
        name = new SimpleStringProperty();
        path = new SimpleStringProperty();
        duration = new SimpleIntegerProperty();
        bitrate = new SimpleIntegerProperty();
    }

    public Song(String name, String path, int duration, int bitrate, Quality quality){
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
        this.duration = new SimpleIntegerProperty(duration);
        this.bitrate = new SimpleIntegerProperty(bitrate);
        setQuality(quality);
    }



    private int id;
    private StringProperty name;
    private StringProperty path;
    private IntegerProperty duration;
    private IntegerProperty bitrate;
    private Quality quality;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty getNameProperty(){
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getPath() {
        return path.get();
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    public StringProperty getPathProperty(){
        return path;
    }

    public int getDuration() {
        return duration.get();
    }

    public void setDuration(int duration) {
        this.duration.set(duration);
    }

    public IntegerProperty getDurationProperty(){
        return duration;
    }

    public int getBitrate() {
        return bitrate.get();
    }

    public void setBitrate(int bitrate) {
        this.bitrate.set(bitrate);
    }

    public IntegerProperty getBitrateProperty(){
        return bitrate;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Song song = (Song) o;

        if (id != song.id) return false;
        if (getDuration() != song.getDuration()) return false;
        if (getBitrate() != song.getBitrate()) return false;
        if (!getName().equals(song.getName())) return false;
        if (!getPath().equals(song.getPath())) return false;
        return getQuality() == song.getQuality();

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPath().hashCode();
        result = 31 * result + getDuration();
        result = 31 * result + getBitrate();
        result = 31 * result + getQuality().hashCode();
        return result;
    }
}
