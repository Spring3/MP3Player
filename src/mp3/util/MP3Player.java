package mp3.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import mp3.model.Song;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * MP3 player class. Uses separate thread for UI rendering.
 * Renders duration (current and maximum) as well as slider progress.
 * Also, renders the play button text depending on the player status.
 */
public class MP3Player {

    //singleton instance of the mp3 player
    public static MP3Player instance;
    //javafx standard plauer
    private static volatile MediaPlayer player;
    //javafx.concurrent.task for UI updating
    private final Task task;
    //the thread in which the UI will be updated
    private final Thread thread;
    //a thread safe indicator of the thread start
    private AtomicBoolean wasThreadStarted;
    private AtomicBoolean rewinding;

    //the songs to be played. Not thread safe
    private final LinkedList<Song> songsQueue;
    //a thread safe index of the song that is being played from the songsQueue
    private volatile AtomicInteger indexPlayed;

    /**
     * Initializes player queue and a thread for UI updating.
     * The thread updates current duration, max duration, slider progress, play button text
     * The thread is of type Daemon.
     * The thread runs until the application ends.
     */
    private MP3Player(){
        songsQueue = new LinkedList<>();
        // creating a new task
        task = new Task(){
            @Override
            protected Object call() {
                while (true) {
                    try {
                        //if player was initialized
                        if (player != null) {

                            //get its status
                            switch (player.getStatus()) {
                                //if playing
                                case PLAYING: {
                                    //get media file total duration in seconds
                                    double duration = player.getMedia().getDuration().toSeconds();
                                    //and player's current duration
                                    double currentTime = player.getCurrentTime().toSeconds();
                                    //set button text to "pause"
                                    updateMessage("||");
                                    //update slider
                                    updateValue(100 * currentTime / duration);
                                    //get duration in minutes
                                    int minutes = (int) currentTime / 60;
                                    //and seconds
                                    int seconds = (int) currentTime % 60;
                                    int minutesTotal = (int) duration / 60;
                                    int secondTotal = (int) duration % 60;
                                    //update time label
                                    updateTitle(String.format("%d:%d/%d:%d", minutes, seconds, minutesTotal, secondTotal));
                                    break;
                                }
                                //if stopped
                                case STOPPED: {
                                    updateMessage(">");
                                    break;
                                }
                                //if paused
                                case PAUSED: {
                                    //change button text to "go"
                                    updateMessage(">");
                                    break;
                                }
                            }

                        } else {
                            //if not playing anything, set values o default
                            updateMessage(">");
                            updateTitle("0:0/0:0");
                            updateValue(0);
                        }

                        Thread.sleep(100);

                    } catch (Exception ex) {
                    }
                }
            }
        };

        //initialize thread
        thread = new Thread(task);
        //set it to be a daemon
        thread.setDaemon(true);
        thread.setName("UI updater");
    }

    /**
     * Get the singleton instance of the mp3 player. Is thread-safe
     * @return the singleton instance of the mp3 player
     */
    public static MP3Player getInstance(){
        if (instance == null){
            synchronized (Object.class){
                if (instance == null){
                    instance = new MP3Player();
                    instance.indexPlayed = new AtomicInteger(0);
                    instance.wasThreadStarted = new AtomicBoolean(false);
                    instance.rewinding = new AtomicBoolean(false);
                }
            }
        }
        return instance;
    }

    /**
     * Adds a collection of songs to the player queue.
     * @param songs the collection, containing Song objects.
     */
    public synchronized void addToQueue(Collection<Song> songs){
        songsQueue.addAll(songs);
    }

    /**
     * Adds a single song into the player queue
     * @param song song to add to the queue
     */
    public synchronized void addToQueue(Song song){
        songsQueue.add(song);
    }

    /**
     * Clears the player queue and adds new songs to it.
     * @param songs the collection of Song objects to be added and played with the player
     */
    public synchronized void clearAndAddToQueue(Collection<Song> songs){
        stop(true);
        songsQueue.clear();
        songsQueue.addAll(songs);
        indexPlayed.set(0);
    }

    /**
     * Clear the player queue and add a single song to it.
     * @param song the Song to be played.
     */
    public synchronized void clearAndAddToQueue(Song song){
        stop(true);
        songsQueue.clear();
        songsQueue.add(song);
        indexPlayed.set(0);
    }

    /**
     * Starts the player. The song is being played after this method is called. If the player queue is empty,
     * nothing will happen.
     * @param index of the song in the player queue to play
     */
    public synchronized void play(int index) {
        if (songsQueue.size() == 0)
            return;

        indexPlayed.set(index);
        play();

    }

    /**
     * Plays the music with a player. Initializes OnMediaEnd event handler, which is - to move to the next sound file
     * when the current one ends.
     */
    private synchronized void play(){
        if (songsQueue.size() == 0)
            return;

        stop(true);
        player = new MediaPlayer(new Media(songsQueue.get(indexPlayed.get()).getPath()));
        try {
            //this is required to let the player bufferize the music file
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        player.play();
        //when the sound file ends
        player.setOnEndOfMedia(() -> {
            synchronized (songsQueue) {
                //increment the index of the song to be played.
                //if it is gets out of the queue max index range, play the first file
                if (indexPlayed.incrementAndGet() == songsQueue.size())
                    indexPlayed.set(0);
                play();
            }

        });
    }

    /**
     * Makes the player play the next sound file from the queue
     */
    public synchronized void playNext(){
        if (player != null) {
            stop(true);
            //if the end of the queue, start from the begining
            if (indexPlayed.incrementAndGet() == songsQueue.size())
                indexPlayed.set(0);
            play();
        }
    }

    /**
     * Makes the player play the previous sound file from the queue
     */
    public synchronized void playPrev(){
        if (player != null) {
            stop(true);
            //if the index becomes negative, play the last sound file from the queue
            if (indexPlayed.decrementAndGet() < 0)
                indexPlayed.set(songsQueue.size() - 1);
            player = new MediaPlayer(new Media(songsQueue.get(indexPlayed.get()).getPath()));
            try {
                //give it some time to bufferize
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.play();
            //when the sound file ends
            player.setOnEndOfMedia(() -> {
                synchronized (songsQueue) {
                    //if the next index is above the max index in the queue
                    if (indexPlayed.incrementAndGet() == songsQueue.size())
                        //play the first sound file
                        indexPlayed.set(0);
                    play();
                }

            });
        }
    }

    /**
     * Pauses the player
     */
    public synchronized void pause(){
        if (player != null)
            player.pause();
    }

    /**
     * Continues to play the paused sound file
     */
    public synchronized void proceed(){
        if (player != null) {
            player.play();
            try {
                //To bufferize
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Rewinds the sound file to the selected percent of it's max duration
     * @param percent the percent of the total sound file duration to rewind to.
     */
    public synchronized void rewind(double percent){
        if (player != null) {
            rewinding.set(true);
            //stop(false);
            double duration = player.getTotalDuration().toSeconds();
            double rewindToTime = duration * percent / 100;
            //multiply by 1000 because the seek method requires the time in miliseconds, but we store the duration in seconds
            player.seek(new Duration(rewindToTime * 1000));
            try {
                thread.sleep(100);
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rewinding.set(false);
            //proceed();
        }
    }

    public synchronized void swapSongs(int indexOfFirst, int indexOfSecond){
        Song song = songsQueue.get(indexOfFirst);
        Song song2 = songsQueue.get(indexOfSecond);
        songsQueue.set(indexOfFirst, song2);
        songsQueue.set(indexOfSecond, song);
    }

    /**
     * Get the UI updater Task from the thread.
     * @return UI updater task
     */
    public Task getTask(){
        return task;
    }

    public boolean isRewinding(){
        return rewinding.get();
    }

    /**
     * Starts the task thread if it wasn't started before.
     */
    public void startTask(){
        if (!wasThreadStarted.get()) {
            thread.start();
            wasThreadStarted.set(true);
        }
    }

    /**
     * Stops the player
     */
    public synchronized void stop(boolean dispose){
        if (player != null) {
            player.stop();
            if (dispose)
                player.dispose();
        }
    }

    public synchronized int getCurrentSongIndex(){
        return indexPlayed.get();
    }

    /**
     * Gets the status of the javafx player class
     * @return the current player status if the player was initialized. Otherwise, returns Status.UNKNOWN
     */
    public synchronized MediaPlayer.Status getPlayerStatus(){
        if (player != null){
            return player.getStatus();
        }
        return MediaPlayer.Status.UNKNOWN;
    }
}
