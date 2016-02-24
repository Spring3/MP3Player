package mp3.model;

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

    }

    public Song(String name, String path, int duration, int bitrate, Quality quality){
        setName(name);
        setPath(path);
        setDuration(duration);
        setBitrate(bitrate);
        setQuality(quality);
    }



    private int id;
    private String name;
    private String path;
    private int duration;
    private int bitrate;
    private Quality quality;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
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
