package mp3.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML
    private TableColumn<Song, Integer> tCol_Duration;

    @FXML
    private TableView<Playlist> table_playlists;

    @FXML
    private TableColumn<Song, String> tCol_song;

    @FXML
    private Button btn_addAlbums;

    @FXML
    private MenuItem mItem_Close;

    @FXML
    private TableColumn<Album, String> tCol_Album;

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
    private TableColumn<Song, Integer> tCol_Bitrate;

    @FXML
    private TableView<Album> table_albums;

    @FXML
    private Button btn_addPlaylist;

    @FXML
    private Button btn_prev;

    @FXML
    private Button btn_play;

    @FXML
    private TableView<Song> table;

    @FXML
    private Button btn_next;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tCol_playlists.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();
        playlists.addAll(new DAOPlaylist().getAll());
        table_playlists.setItems(playlists);

        tCol_song.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        tCol_Bitrate.setCellValueFactory(new PropertyValueFactory<Song, Integer>("bitrate"));
        tCol_Duration.setCellValueFactory(new PropertyValueFactory<Song, Integer>("duration"));
        ObservableList<Song> songs = FXCollections.observableArrayList();
        songs.addAll(new DAOSong().getAll());
        table.setItems(songs);

        MenuItem menuToPlayList = new MenuItem("Add to PlayList");
        menuToPlayList.setOnAction(t -> {
            try {
                Song item = table.getItems().get(table.getSelectionModel().getSelectedIndex());
                if (item != null) {
                    showChoiceBoxForPlaylistAdding();
                }
            }
            catch (Exception ex){

            }
        });

        MenuItem menuToAlbum = new MenuItem("Add to Album");
        menuToAlbum.setOnAction(t -> {
            try {
                Song item = table.getItems().get(table.getSelectionModel().getSelectedIndex());
                if (item != null) {
                    showChoiceBoxForAlbumAdding();
                }
            }
            catch (Exception ex){

            }
        });

        MenuItem menuShowAllSongs = new MenuItem("Show all songs");
        menuShowAllSongs.setOnAction(t -> {
            table.getItems().clear();
            table.getItems().addAll(new DAOSong().getAll());
        });
        table.setContextMenu(new ContextMenu(menuToPlayList, menuToAlbum, menuShowAllSongs));

        table_playlists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                table.getItems().clear();
                table.getItems().addAll(new DAOPlaylist().getAllSongs(newSelection));
            }
        });

        tryGetMusic();
    }

    private void showChoiceBoxForPlaylistAdding(){
        List<Playlist> choices = new ArrayList<>();
        for(Playlist playlist : new DAOPlaylist().getAll()){
            choices.add(playlist);
        }

        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Select playlist");
        dialog.setHeaderText("The song will be added to the selected playlist");
        dialog.setContentText("Choose the playlist to add the song to");

        // Traditional way to get the response value.
        Optional<Playlist> result = dialog.showAndWait();
        Song selectedSong = table.getItems().get(table.getSelectionModel().getSelectedIndex());
        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(playlist -> new DAOSong().addToPlaylist(playlist, selectedSong));
    }

    private void showChoiceBoxForAlbumAdding(){
        List<Album> choices = new ArrayList<>();

        Playlist selectedPlayList = null;
        try{
            selectedPlayList = table_playlists.getItems().get(table_playlists.getSelectionModel().getSelectedIndex());
        }
        catch (Exception ex){

            showWarning("Error", "Playlist not selected", "Select the playlist to get the albums");
            return;
        }

        for(Album album : new DAOPlaylist().getAllAlbums(selectedPlayList)){
            choices.add(album);
        }

        if (choices.size() == 0){
            showWarning("Error", "No albums found", "There are no albums for the playlist selected. Please, create a new album and try again");
            return;
        }

        ChoiceDialog<Album> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Select album");
        dialog.setHeaderText("The song will be added to the selected album");
        dialog.setContentText("Choose the album to add the song to");

        // Traditional way to get the response value.
        Optional<Album> result = dialog.showAndWait();
        Song selectedSong = table.getItems().get(table.getSelectionModel().getSelectedIndex());
        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(album -> new DAOSong().addToAlbum(album, selectedSong));
    }

    private void showWarning(String title, String header, String description){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(description);

        alert.showAndWait();

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
        btn_addPlaylist.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> mItem_newPlaylist.fire());
        btn_addAlbums.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN), () -> btn_addAlbums.fire());
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
