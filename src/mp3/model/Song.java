package mp3.model;

import javafx.beans.property.*;

/**
 * Class for keeping the song info
 */
public class Song {

    public enum Quality{
        BAD,
        MEDIUM,
        GOOD
    }

    /**
     * Creates the default song instance
     */
    public Song(){
        name = new SimpleStringProperty("");
        path = new SimpleStringProperty("");
        duration = new SimpleIntegerProperty();
        bitrate = new SimpleIntegerProperty();
        durationHumanFriendly = new SimpleStringProperty("");
        bitrateString = new SimpleStringProperty("");
        album = new SimpleObjectProperty<>(new Album());
    }

    /**
     * Initializes the song class.
     * @param name name of the song
     * @param path uri path to the sound file
     * @param duration duration in seconds
     * @param bitrate bitrate of the sound file
     * @param quality quality of the sound file
     */
    public Song(String name, String path, int duration, int bitrate, Quality quality){
        this.name = new SimpleStringProperty(name);
        this.path = new SimpleStringProperty(path);
        this.duration = new SimpleIntegerProperty(duration);
        this.bitrate = new SimpleIntegerProperty(bitrate);
        setDurationHumanFriendly(duration);
        bitrateString = new SimpleStringProperty(bitrate + "");
        album = new SimpleObjectProperty<>(new Album());
        setQuality(quality);
    }

    private int id;
    private StringProperty name;
    private StringProperty durationHumanFriendly;
    private StringProperty bitrateString;
    private StringProperty path;
    private IntegerProperty duration;
    private IntegerProperty bitrate;
    private ObjectProperty<Album> album;
    private Quality quality;

    /**
     * Returns the id of this object in the database.
     * @return Returns null if the object was not updated from the database
     */
    public int getId(){
        return id;
    }

    /**
     * Sets the id of this object
     * @param id id from the database
     */
    public void setId(int id){
        this.id = id;
    }

    /**
     * Get the name of the sound file
     * @return Returns the name of the sound file
     */
    public String getName() {
        return name.get();
    }

    /**
     * Get the property, containing the name of the sound file
     * @return the property, containing the name of the sound file
     */
    public StringProperty getNameProperty(){
        return name;
    }

    /**
     * Sets the name for the sound file
     * @param name the name of the sound file. Will be added to the name property
     */
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * Gets the uri path to the sound file on local file system
     * @return the uri path to the sound file
     */
    public String getPath() {
        return path.get();
    }

    /**
     * Sets the uri path to the sound file on local file system
     * @param path the uri path to the sound file
     */
    public void setPath(String path) {
        this.path.set(path);
    }

    /**
     * Gets the property, which contains the path of the sound file
     * @return the property, which contains the path of the sound file
     */
    public StringProperty getPathProperty(){
        return path;
    }

    /**
     * Gets the duration of the sound file
     * @return the duration in seconds
     */
    public int getDuration() {
        return duration.get();
    }

    /**
     * Sets the duration of the sound file
     * @param duration the duration in seconds
     */
    public void setDuration(int duration) {
        this.duration.set(duration);
    }

    /**
     * Gets the property, containing the duration of the sound file
     * @return the property, containing the duration of the sound file
     */
    public IntegerProperty getDurationProperty(){
        return duration;
    }

    /**
     * Gets the bitrate of the sound file
     * @return the bitrate of the sound file
     */
    public int getBitrate() {
        return bitrate.get();
    }

    /**
     * Sets the bitrate of the sound file
     * @param bitrate the bitrate of the sound file
     */
    public void setBitrate(int bitrate) {
        this.bitrate.set(bitrate);
        this.bitrateString.set(bitrate + "");
    }

    public void setAlbumProperty(Album album){
        this.album.set(album);
    }

    public ObjectProperty<Album> getAlbumProperty(){
        return album;
    }

    /**
     * Gets the property, containing the bitrate of the sound file
     * @return the property, containing the bitrate of the sound file
     */
    public IntegerProperty getBitrateProperty(){
        return bitrate;
    }

    /**
     * Gets the song's bitrate as string
     * @return the song's bitrate as string
     */
    public String getBitrateString(){
        return bitrateString.get();
    }

    /**
     * Gets the song's bitrate string property
     * @return the property, which contains the song's bitrate
     */
    public StringProperty getBitrateStringProperty(){
        return bitrateString;
    }

    /**
     * Gets the quality of the sound file
     * @return the quality of the sound file
     */
    public Quality getQuality() {
        return quality;
    }

    /**
     * Sets the quality of the sound file
     * @param quality the quality of the sound file
     */
    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    /**
     * Converts the duration in seconds to tha human friendly representation in format of #mins:#seconds
     * @param seconds the duration in seconds
     */
    private void setDurationHumanFriendly(int seconds){
        int minutes = seconds / 60;
        int secodns = seconds % 60;
        durationHumanFriendly = new SimpleStringProperty(String.format("%d:%d", minutes, secodns));
    }

    /**
     * Gets the human friendly variant of the sound file duration
     * @return the human friendly variant of the sound file duration
     */
    public String getDurationHumanFriendly(){
        return durationHumanFriendly.get();
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
