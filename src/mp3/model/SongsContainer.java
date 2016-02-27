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
}
