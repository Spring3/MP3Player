package mp3.model;

import java.util.List;

/**
 * Created by Spring on 2/27/2016.
 */
public interface SongsContainer {
    /**
     * Gets the list of songs from the container
     * @return the list of songs
     */
    List<Song> getSongs();

    /**
     * Assigns the song with a current container
     * @param song song to assign to
     * @return true if the song was assigned successfully
     */
    boolean assignSong(Song song);

    /**
     * Checks if the song was already added to the song container
     * @param container the container to add the music to
     * @param song song to be added
     * @return true if the container doesn't contain the song
     */
    boolean containsSong(SongsContainer container, Song song);
}
