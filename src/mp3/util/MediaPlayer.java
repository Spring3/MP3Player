package mp3.util;

import javafx.scene.media.Media;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;

/**
 * Created by Spring on 2/25/2016.
 */
public class MediaPlayer {

    public static MediaPlayer instance;
    private static javafx.scene.media.MediaPlayer player;

    private MediaPlayer(){

    }

    public static MediaPlayer getInstance(){
        if (instance == null){
            synchronized (Object.class){
                if (instance == null){
                    instance = new MediaPlayer();
                }
            }
        }
        return instance;
    }

    public void play(Media media){
        player = new javafx.scene.media.MediaPlayer(media);
        player.play();
    }

    public void pause(){
        player.pause();
    }

    public void proceed(){
        player.play();
    }

    public void stop(){
        player.stop();
    }
}
