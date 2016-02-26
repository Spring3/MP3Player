package mp3.util;

import javafx.concurrent.Task;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import mp3.model.Song;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Spring on 2/25/2016.
 */
public class MP3Player {

    public static MP3Player instance;
    private static MediaPlayer player;
    private final Task task;
    private final Thread thread;
    private BlockingQueue<Song> songsQueue;

    private MP3Player(){
        songsQueue = new LinkedBlockingDeque<>();

        task = new Task(){
            @Override
            protected Object call() throws Exception{
                while(true){
                    switch (player.getStatus()){
                        case PLAYING:{
                            double duration = player.getMedia().getDuration().toSeconds();
                            double currentTime = player.getCurrentTime().toSeconds();
                            this.updateProgress(100 * currentTime / duration, duration);
                            this.updateMessage("pause");
                            int minutes = (int) currentTime / 60;
                            int seconds = (int) currentTime % 60 * 60;
                            int minutesTotal = (int) duration / 60;
                            int secondTotal = (int) duration % 60 * 60;
                            this.updateTitle(String.format("%d:%d/%d:%d", minutes, seconds, minutesTotal, secondTotal));
                            System.out.println("Playing");
                            break;
                        }
                        case STOPPED:{
                            this.updateMessage("play");
                            System.out.println("Stopped");
                            break;
                        }
                        case PAUSED:{
                            this.updateMessage("continue");
                            System.out.println("Continue");
                            break;
                        }
                        default:{
                            this.updateMessage("play");
                            this.updateTitle("0:0/0:0");
                            System.out.println("Default");
                            break;
                        }
                    }
                }
            }
        };

        thread = new Thread(task);
    }

    public static MP3Player getInstance(){
        if (instance == null){
            synchronized (Object.class){
                if (instance == null){
                    instance = new MP3Player();
                    instance.thread.start();
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
    }

    public synchronized void play(){
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
            try {
                if (songsQueue.size() > 0) {
                    player = new MediaPlayer(new Media(songsQueue.take().getPath()));

                }
            }
            catch (InterruptedException ex){
                ex.printStackTrace();
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

    public void stop(){
        if (player != null)
            player.stop();
    }
}
