package mp3.util;

import java.io.*;
import java.util.Properties;

/**
 * Created by Spring on 2/24/2016.
 */
public class Config {

    public static Config instance;
    private Properties properties;
    private static final String CONFIG_FILE_PATH = "config.properties";
    public static final String MUSIC_FOLDER_PATH = "MUSIC_FOLDER_PATH";

    private Config(){

    }

    public static Config getInstance(){
        if (instance == null){
            synchronized (Object.class) {
                if (instance == null) {
                    instance = new Config();
                    instance.properties = new Properties();
                    try {
                        File configFile = new File(CONFIG_FILE_PATH);
                        if (!configFile.exists()){
                            configFile.createNewFile();
                        }
                        instance.properties.load(new FileInputStream(CONFIG_FILE_PATH));
                    }
                    catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    public String getParameter(String key){

        return getInstance().properties.getProperty(key);
    }

    public void setParameter(String key, String value){
        getInstance().properties.setProperty(key, value);
        try {
            getInstance().properties.store(new FileOutputStream(CONFIG_FILE_PATH), null);
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
