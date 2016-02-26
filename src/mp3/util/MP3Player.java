package mp3.util;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import mp3.model.Song;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Spring on 2/25/2016.
 */
public class MP3Player {

    public static MP3Player instance;
    private static volatile MediaPlayer player;
    private final Task task;
    private final Thread thread;
    private final LinkedList<Song> songsQueue;
    private AtomicInteger indexPlayed;

    private MP3Player(){
        songsQueue = new LinkedList<>();

        task = new Task(){
            @Override
            protected Object call() throws Exception{
                while(true){
                    if (player != null) {
                        switch (player.getStatus()) {

                            case PLAYING: {
                                double duration = player.getMedia().getDuration().toSeconds();
                                double currentTime = player.getCurrentTime().toSeconds();
                                updateMessage("pause");
                                updateValue(100 * currentTime / duration);
                                int minutes = (int) currentTime / 60;
                                int seconds = (int) currentTime % 60;
                                int minutesTotal = (int) duration / 60;
                                int secondTotal = (int) duration % 60;
                                updateTitle(String.format("%d:%d/%d:%d", minutes, seconds, minutesTotal, secondTotal));
                                break;
                            }
                            case STOPPED: {
                                updateMessage("play");
                                break;
                            }
                            case PAUSED: {
                                updateMessage("continue");
                                break;
                            }
                        }
                    }
                    else{
                        updateMessage("play");
                        updateTitle("0:0/0:0");
                        updateValue(0);
                    }
                }
            }
        };
        thread = new Thread(task);
        thread.setDaemon(true);
        thread.setName("UI updater");
    }

    public static MP3Player getInstance(){
        if (instance == null){
            synchronized (Object.class){
                if (instance == null){
                    instance = new MP3Player();
                    instance.indexPlayed = new AtomicInteger(0);
                }
            }
        }
        return instance;
    }

    public synchronized void addToQueue(Collection<Song> songs){
        songsQueue.addAll(songs);

    }

    public synchronized void clearAndAddToQueue(Collection<Song> songs){
        songsQueue.clear();
        songsQueue.addAll(songs);
        indexPlayed.set(0);
    }

    public synchronized void play(){
        if (songsQueue.size() == 0)
            return;

        stop();
        synchronized (songsQueue) {
            player = new MediaPlayer(new Media(songsQueue.get(indexPlayed.get()).getPath()));
            player.play();
            player.setOnEndOfMedia(() -> {
                synchronized (songsQueue) {
                    if (indexPlayed.incrementAndGet() == songsQueue.size())
                        indexPlayed.set(0);
                    play();
                }

            });
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void playNext(){
        if (player != null){
            stop();
            synchronized (songsQueue) {
                if (indexPlayed.incrementAndGet() == songsQueue.size())
                    indexPlayed.set(0);
                play();
            }
        }
    }

    public synchronized void playPrev(){
        if (player != null){
            stop();
            synchronized (songsQueue) {
                if (indexPlayed.decrementAndGet() < 0)
                    indexPlayed.set(songsQueue.size() - 1);
                player = new MediaPlayer(new Media(songsQueue.get(indexPlayed.get()).getPath()));
                player.play();
                player.setOnEndOfMedia(() -> {
                    synchronized (songsQueue) {
                        if (indexPlayed.incrementAndGet() == songsQueue.size())
                            indexPlayed.set(0);
                        play();
                    }

                });
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void pause(){
        if (player != null)
            player.pause();
    }

    public synchronized void proceed(){
        if (player != null) {
            player.play();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void rewind(double percent){
        if (player != null){
            pause();
            double duration = player.getTotalDuration().toSeconds();
            double rewindToTime = duration * percent / 100;
            player.seek(new Duration(rewindToTime * 1000));
            proceed();
        }
    }

    public Task getTask(){
        return task;
    }

    public void startTask(){
        thread.start();
    }

    public synchronized void stop(){
        if (player != null)
            player.stop();
    }

    public synchronized MediaPlayer.Status getPlayerStatus(){
        if (player != null){
            return player.getStatus();
        }
        return MediaPlayer.Status.UNKNOWN;
    }
}
