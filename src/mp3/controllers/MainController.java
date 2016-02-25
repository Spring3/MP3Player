package mp3.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import mp3.dao.DAOPlaylist;
import mp3.dao.DAOSong;
import mp3.model.Album;
import mp3.model.Playlist;
import mp3.model.Song;
import mp3.util.Config;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML
    private TableColumn<?, ?> tCol_Duration;

    @FXML
    private TableView<Playlist> table_playlists;

    @FXML
    private TableColumn<?, ?> tCol_Artist;

    @FXML
    private TableColumn<?, ?> tCol_song;

    @FXML
    private Button btn_addAlbums;

    @FXML
    private MenuItem mItem_Close;

    @FXML
    private TableColumn<?, ?> tCol_Album;

    @FXML
    private TableColumn<Album, String> tCol_albums;

    @FXML
    private MenuItem mItem_newPlaylist;

    @FXML
    private MenuItem mItem_selFolder;

    @FXML
    private MenuItem mItem_newAlbum;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private TableColumn<Playlist, String> tCol_playlists;

    @FXML
    private TableColumn<?, ?> tCol_Bitrate;

    @FXML
    private TableView<Album> table_albums;

    @FXML
    private Button btn_addPlaylist;

    @FXML
    private Button btn_prev;

    @FXML
    private Button btn_play;

    @FXML
    private TableView<?> table;

    @FXML
    private Button btn_next;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tCol_playlists.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();
        playlists.addAll(new DAOPlaylist().getAll());
        table_playlists.setItems(playlists);
        tryGetMusic();
    }

    private void tryGetMusic(){
        if (Config.getInstance().getParameter(Config.MUSIC_FOLDER_PATH) != null)
        {
            DAOSong dao = new DAOSong();
            File file = new File(Config.getInstance().getParameter(Config.MUSIC_FOLDER_PATH));
            for(File f : file.listFiles()){
                if (f.getPath().contains(".mp3")) {
                    Media media = new Media(f.toURI().toString());
                    MediaPlayer player = new MediaPlayer(media);
                    player.setOnReady(() -> {

                        /*for (Map.Entry<String, Object> entry : media.getMetadata().entrySet()){
                            System.out.println(entry.getKey() + ": " + entry.getValue());
                        }        */
                        int duration = (int)media.getDuration().toSeconds();
                        int bitrate = (int) (f.length() * 8 / duration / 1000);

                        Song song = new Song(f.getName(), f.getPath(), duration , bitrate, Song.Quality.MEDIUM);
                        dao.create(song);
                    });

                }
            }
        }
    }

    public void init(){
        btn_play.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.SPACE), () -> btn_play.fire());
        btn_prev.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT), () -> btn_prev.fire());
        btn_next.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT), () -> btn_next.fire());
    }

    @FXML
    void mItemSelectFolderClicked(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(btn_play.getScene().getWindow());
        if (selectedDirectory != null) {
            Config.getInstance().setParameter(Config.MUSIC_FOLDER_PATH, selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    void createPlayList(ActionEvent actionEvent) {
        Playlist newPlayList;
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Enter the name of the new playlist");
        dialog.setContentText("Please enter the name of the playlist you wish to create:");
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            newPlayList = new Playlist(result.get());
            DAOPlaylist dao = new DAOPlaylist();
            if(dao.create(newPlayList)) {
                newPlayList = dao.get(result.get());
                table_playlists.getItems().add(newPlayList);
            }
        }
    }

    @FXML
    void createAlbum(ActionEvent actionEvent) {
    }
}
