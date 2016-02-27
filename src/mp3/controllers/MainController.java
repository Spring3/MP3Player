package mp3.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import mp3.dao.DAOAlbum;
import mp3.dao.DAOPlaylist;
import mp3.dao.DAOSong;
import mp3.model.Album;
import mp3.model.Playlist;
import mp3.model.Song;
import mp3.util.Config;
import mp3.util.MP3Player;

import java.awt.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
    private TableColumn<Album, String> tCol_albums;

    @FXML
    private MenuItem mItem_newPlaylist;

    @FXML
    private MenuItem mItem_selFolder;

    @FXML
    private MenuItem mItem_newAlbum;

    @FXML
    private Slider slider;

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

    @FXML
    private Label lbl_duration;

    private BlockingQueue<MediaPlayer> players = new LinkedBlockingQueue<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tCol_playlists.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ObservableList<Playlist> playlists = FXCollections.observableArrayList();
        playlists.addAll(new DAOPlaylist().getAll());
        table_playlists.setItems(playlists);

        MenuItem menuShowSongsFromPlaylist = new MenuItem("Show songs");
        menuShowSongsFromPlaylist.setOnAction(t -> {
            try {
                Playlist item = table_playlists.getItems().get(table_playlists.getSelectionModel().getSelectedIndex());
                table.getItems().clear();
                table.getItems().addAll(new DAOPlaylist().getAllSongs(item));
            }
            catch (Exception ex){

            }
        });
        MenuItem playPlaylist = new MenuItem("Play");
        playPlaylist.setOnAction(t -> {
            MP3Player player = MP3Player.getInstance();
            player.clearAndAddToQueue(table.getItems());
            player.play(0);
            slider.valueProperty().bind(player.getTask().valueProperty());
        });
        table_playlists.setContextMenu(new ContextMenu(menuShowSongsFromPlaylist, playPlaylist));

        tCol_song.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
        tCol_Bitrate.setCellValueFactory(new PropertyValueFactory<Song, Integer>("bitrate"));
        tCol_Duration.setCellValueFactory(new PropertyValueFactory<Song, Integer>("durationHumanFriendly"));
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

        MenuItem playSong = new MenuItem("Play");
        playSong.setOnAction(t -> {
            int selectedSongIndex = table.getSelectionModel().getSelectedIndex();
            MP3Player player = MP3Player.getInstance();
            player.clearAndAddToQueue(table.getItems());
            player.play(selectedSongIndex);
            slider.valueProperty().bind(player.getTask().valueProperty());
        });
        table.setContextMenu(new ContextMenu(menuToPlayList, menuToAlbum, menuShowAllSongs, playSong));

        table_playlists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                table.getItems().clear();
                table.getItems().addAll(new DAOPlaylist().getAllSongs(newSelection));
                table_albums.getItems().clear();
                table_albums.getItems().addAll(new DAOPlaylist().getAllAlbums(newSelection));
            }
        });

        tCol_albums.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        table_albums.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null){
                table.getItems().clear();
                table.getItems().addAll(new DAOAlbum().getAllSongs(newValue));
            }
        });

        MenuItem menuShowSongsFromAlbum = new MenuItem("Show songs");
        menuShowSongsFromAlbum.setOnAction(t -> {
            try {
                Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());
                table.getItems().clear();
                table.getItems().addAll(new DAOAlbum().getAllSongs(item));
            }
            catch (Exception ex){
            }
        });

        MenuItem showCover = new MenuItem("Show Cover");
        showCover.setOnAction(t -> {
            Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());
            Dialog<String> dialog = new Dialog<>();
            ImageView imageView = new ImageView(item.getPicPath());
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);

            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(imageView, 0, 0, 2, 2);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return "";
                }
                return null;
            });

            dialog.showAndWait();
        });

        MenuItem playAlbum = new MenuItem("Play");
        playAlbum.setOnAction(t -> {
            MP3Player player = MP3Player.getInstance();
            player.clearAndAddToQueue(table.getItems());
            player.play(0);
            slider.valueProperty().bind(player.getTask().valueProperty());
        });


        table_albums.setContextMenu(new ContextMenu(menuShowSongsFromAlbum, playAlbum, showCover));

        MP3Player player = MP3Player.getInstance();
        btn_play.textProperty().bind(player.getTask().messageProperty());
        lbl_duration.textProperty().bind(player.getTask().titleProperty());
        player.startTask();

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

    private void showAlbumCreationPopup(){
        // Create the custom dialog.
        Dialog<Album> dialog = new Dialog<>();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Create new album");

// Set the button types.
        ButtonType btnCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCreate, ButtonType.CANCEL);

// Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Album name");
        TextField imagePath = new TextField();
        imagePath.setPromptText("Album image path");

        Button fileChooser = new Button("Choose");
        fileChooser.setOnAction( event -> {
            final FileChooser fileSelector = new FileChooser();
            final File selectedFile = fileSelector.showOpenDialog(btn_play.getScene().getWindow());
            if (selectedFile != null) {
                imagePath.setText(selectedFile.toURI().toString());
            }
        });

        ChoiceBox<Playlist> playListChoiceBox = new ChoiceBox<>();
        playListChoiceBox.setItems(FXCollections.observableArrayList(new DAOPlaylist().getAll()));

        grid.add(new Label("Album name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(imagePath, 0, 1);
        grid.add(fileChooser, 1, 1);
        grid.add(new Label("Playlist:"), 0, 2);
        grid.add(playListChoiceBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

// Request focus on the username field by default.
        Platform.runLater(() -> name.requestFocus());

// Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnCreate && !name.getText().trim().isEmpty() && !imagePath.getText().trim().isEmpty() && playListChoiceBox.getSelectionModel().getSelectedItem() != null) {
                Album newAlbum = new Album(name.getText(), imagePath.getText(), playListChoiceBox.getValue());
                DAOAlbum dao = new DAOAlbum();
                dao.create(newAlbum);
                return  dao.get(newAlbum.getName());
            }
            else{
                showWarning("Album wasn't created", "The album was not created.", "One or more fields were not filled in properly");
                return null;
            }
        });

        Optional<Album> result = dialog.showAndWait();

        result.ifPresent(album -> {
            System.out.println("New album was created");
            table_albums.getItems().add(album);
        });
    }

    private void tryGetMusic(){
        if (Config.getInstance().getParameter(Config.MUSIC_FOLDER_PATH) != null)
        {
            Thread musicParsingThread = new Thread(() -> {
                DAOSong dao = new DAOSong();
                int repeats = 0;
                while(repeats < 50){
                    if (players.size() > 0){
                        players.stream().filter(player -> player.getStatus() == MediaPlayer.Status.READY).forEach(player -> {
                            int duration = (int) player.getMedia().getDuration().toSeconds();
                            String source = player.getMedia().getSource();
                            source = source.substring(source.lastIndexOf("file:/") + 6, source.length());
                            File file = null;
                            try {
                                file = new File(URLDecoder.decode(source, "UTF-8").replace("%20", " "));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            int bitrate = (int) (file.length() * 8 / duration / 1000);

                            Song song = null;
                            try {
                                song = new Song(URLDecoder.decode(file.getName(), "UTF-8").replace("%20", " "), file.toURI().toString(), duration, bitrate, Song.Quality.MEDIUM);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (dao.create(song)) {
                                table.getItems().add(dao.get(file.getName()));
                                players.remove(player);

                            }
                            else{
                                players.remove(player);
                            }
                        });
                    }
                    else{
                        try {
                            Thread.sleep(100);
                            repeats ++;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Thread.currentThread().join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            musicParsingThread.setDaemon(true);

            File file = new File(Config.getInstance().getParameter(Config.MUSIC_FOLDER_PATH));
            musicParsingThread.start();
            for(File f : file.listFiles()){
                if (f.getPath().contains(".mp3") || f.getPath().contains(".wav")) {
                    Media media = new Media(f.toURI().toString());
                    MediaPlayer player = new MediaPlayer(media);
                    players.add(player);
                }
            }
        }
    }

    public void init(){
        btn_addPlaylist.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> mItem_newPlaylist.fire());
        btn_addAlbums.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN), () -> mItem_newAlbum.fire());
        btn_play.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.ENTER), () -> btn_play.fire());
        btn_prev.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT), () -> btn_prev.fire());
        btn_next.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT), () -> btn_next.fire());
        tryGetMusic();
    }

    @FXML
    void mItemSelectFolderClicked(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        final File selectedDirectory = directoryChooser.showDialog(btn_play.getScene().getWindow());
        if (selectedDirectory != null) {
            Config.getInstance().setParameter(Config.MUSIC_FOLDER_PATH, selectedDirectory.getAbsolutePath());
            tryGetMusic();
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
    void playSong(ActionEvent event) {
        MP3Player mp3Player = MP3Player.getInstance();
        //if (btn_play.getText().equals("play")){
        if (mp3Player.getPlayerStatus() == MediaPlayer.Status.UNKNOWN){
            int selectedSongIndex = table.getSelectionModel().getSelectedIndex();

            if (selectedSongIndex != -1) {
                mp3Player.clearAndAddToQueue(table.getItems());
                mp3Player.play(selectedSongIndex);
            }
            else{
                mp3Player.clearAndAddToQueue(table.getItems());
                mp3Player.play(0);
            }
            slider.valueProperty().bind(mp3Player.getTask().valueProperty());

        } else if (mp3Player.getPlayerStatus() == MediaPlayer.Status.PLAYING){
            mp3Player.pause();
            slider.valueProperty().unbind();
            slider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    mp3Player.rewind(newValue.doubleValue());
                    slider.valueProperty().removeListener(this);
                    slider.valueProperty().bind(mp3Player.getTask().valueProperty());
                }
            });
        } else if (mp3Player.getPlayerStatus() == MediaPlayer.Status.PAUSED){
            mp3Player.proceed();
            slider.valueProperty().bind(mp3Player.getTask().valueProperty());
        }
    }

    @FXML
    void createAlbum(ActionEvent actionEvent) {
        showAlbumCreationPopup();
    }

    @FXML
    void shutdown(ActionEvent actionEvent) {
        Platform.exit();
    }

    @FXML
    void playNext(ActionEvent actionEvent) {
        MP3Player.getInstance().playNext();
    }

    @FXML
    void playPrev(ActionEvent actionEvent) {
        MP3Player.getInstance().playPrev();
    }

}
