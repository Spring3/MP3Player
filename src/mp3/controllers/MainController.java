package mp3.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.stage.DirectoryChooser;
import mp3.util.Config;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML
    private TableColumn<?, ?> tCol_Duration;

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
    private MenuItem mItem_newPlaylist;

    @FXML
    private MenuItem mItem_selFolder;

    @FXML
    private MenuItem mItem_newAlbum;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private ListView<?> list_Playlists;

    @FXML
    private ListView<?> list_Albums;

    @FXML
    private TableColumn<?, ?> tCol_Bitrate;

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
}
