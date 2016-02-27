package mp3.util;

import java.io.*;
import java.util.Properties;

/**
 * Class for saving / getting data from a config file
 */
public class Config {

    public static Config instance;
    private Properties properties;
    private static final String CONFIG_FILE_PATH = "config.properties";
    public static final String MUSIC_FOLDER_PATH = "MUSIC_FOLDER_PATH";

    private Config(){

    }

    /**
     * Singleton implementation
     * @return singleton instance of an object
     */
    public static Config getInstance(){
        if (instance == null){
            synchronized (Object.class) {
                if (instance == null) {
                    //if not exists, create
                    instance = new Config();
                    //init file
                    instance.properties = new Properties();
                    try {
                        //if not exists
                        File configFile = new File(CONFIG_FILE_PATH);
                        if (!configFile.exists()){
                            //create it
                            configFile.createNewFile();
                        }
                        //load file
                        instance.properties.load(new FileInputStream(CONFIG_FILE_PATH));
                    }
                    catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }
        //return singleton instance
        return instance;
    }

    /**
     * Read value from the property file by the given key
     * @param key that matches the desired value
     * @return value if exists
     */
    public String getParameter(String key){
        return getInstance().properties.getProperty(key);
    }

    /**
     * Save/update value in the config file
     * @param key of the property
     * @param value of the property
     */
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
