package mp3.util;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mp3.model.Song;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Spring on 2/25/2016.
 */
public class MP3Player {

    public static MP3Player instance;
    private static volatile MediaPlayer player;
    private final Task task;
    private final Thread thread;
    private BlockingQueue<Song> songsQueue;

    private MP3Player(){
        songsQueue = new LinkedBlockingDeque<>();

        task = new Task(){
            @Override
            protected Object call() throws Exception{
                while(true){
                    if (player != null) {
                        switch (player.getStatus()) {

                            case PLAYING: {
                                double duration = player.getMedia().getDuration().toSeconds();
                                double currentTime = player.getCurrentTime().toSeconds();
                                updateProgress(100 * currentTime / duration, 100);
                                updateMessage("pause");
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
                        updateProgress(0, 1);
                    }
                }
            }
        };
        thread = new Thread(task);
        thread.setName("UI updater");
    }

    public static MP3Player getInstance(){
        if (instance == null){
            synchronized (Object.class){
                if (instance == null){
                    instance = new MP3Player();
                }
            }
        }
        return instance;
    }

    public void addToQueue(Collection<Song> songs){
        songsQueue.addAll(songs);

    }

    public void clearAndAddToQueue(Collection<Song> songs){
        songsQueue.clear();
        songsQueue.addAll(songs);
    }

    public void play(){
        if (songsQueue.size() == 0)
            return;

        try {
            stop();
            player = new MediaPlayer(new Media(songsQueue.take().getPath()));
            player.play();
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
        player.setOnEndOfMedia(() -> {

            if (songsQueue.size() > 0) {
                play();
            }

        });
    }

    public void pause(){
        if (player != null)
            player.pause();
    }

    public void proceed(){
        if (player != null)
            player.play();
    }

    public Task getTask(){
        return task;
    }

    public void startTask(){
        thread.start();
    }

    public void stop(){
        if (player != null)
            player.stop();
    }
}
