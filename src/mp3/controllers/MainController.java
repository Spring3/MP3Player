package mp3.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import mp3.dao.DAOAlbum;
import mp3.dao.DAOPlaylist;
import mp3.dao.DAOSong;
import mp3.model.Album;
import mp3.model.Playlist;
import mp3.model.Song;
import mp3.model.SongsContainer;
import mp3.util.Config;
import mp3.util.MP3Player;

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

/**
 * Main view of this app.
 * JavaFX works using MVC (Model View Controller) pattern out of box.
 * Hence, controllers, views and models are separated and they are only slightly dependent from each other.
 * Why? Because this makes life of programmers and designers easier, letting each of them to do the work they like and
 * not try to mess with the things they don't know.
 *
 * Initializable is a class, which contains a method, which is called once, just before the view is being shown to the
 * user.
 */
public class MainController implements Initializable{

    //Here go the main elements from the view.
    //@FXML is a dependency injection. It will make sure these variables are initialized when the app runs.
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
    private Label label_header;

    @FXML
    private TableColumn<Album, String> tCol_albums;

    @FXML
    private MenuItem mItem_selFolder;

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

    @FXML
    public  AnchorPane bottomAnchorPane;

    //A concurrent collection. Contains MusicPlayer objects.
    //Is used for fetching music metadata and other required data.
    private BlockingQueue<MediaPlayer> players = new LinkedBlockingQueue<>();

    //A collection where the imported songs are kept
    private List<Song> importedSongs;

    /**
     * This method is implemented in the Initializable interface.
      * @param location URL to the view
     * @param resources is usually used for localization purposes, not supported in this app.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Initializes playlist tableView and a context menu for it
        initializePlaylistsTableAndMenu();
        //Initializes songs tableView and a context menu for it
        initializeSongsTableAndMenu();
        //Initializes albums tableView and a context menu for it
        initializeAlbumTableAndMenu();

        //get the mp3 player
        MP3Player player = MP3Player.getInstance();
        //bind "play" button to the updates from another thread. This will change the text of the button in runtime
        btn_play.textProperty().bind(player.getTask().messageProperty());
        //bind "duration" label to the updates from another thread. This will display the duration for the sound file
        lbl_duration.textProperty().bind(player.getTask().titleProperty());
        //start javafx.concurrent.Task in a separate Thread
        //Since then, the values to the bound objects will be automatically updated and refreshed once per frame
        player.startTask();

    }

    /**
     * Initializes the TableView for playlists.
     * Also, initializes context menu for it.
     */
    private void initializePlaylistsTableAndMenu(){
        //Binding string property from the class Playlist to the column of the tableView.
        //After then all the data will be automatically refreshed as soon as it will be modified from anywhere.
        //By the way. Uses Observable pattern of design
        tCol_playlists.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ObservableList<Playlist> playlists = FXCollections.observableArrayList(); //this list tracks all the changes to its data.
        playlists.addAll(new DAOPlaylist().getAll()); //fetching all the playlists from the database
        table_playlists.setItems(playlists); //and adding them to the tableView

        //Context menu menuItem.
        MenuItem menuShowSongsFromPlaylist = new MenuItem("Show songs");
        //on click
        menuShowSongsFromPlaylist.setOnAction(t -> {
            try {
                //get the selected playlist from the tableView
                //Context meny could have been called without selecting any playlists, hence a NullPointerException will
                //be raised. So, try catch is used.
                Playlist item = table_playlists.getItems().get(table_playlists.getSelectionModel().getSelectedIndex());

                //and add new data, fetched from the database.
                //here we get all the songs from the playlist
                List<Song> songs = new DAOPlaylist().getAllSongs(item);
                if( songs.size() > 0){
                    //clear all the data, currently present in the tableView
                    table.getItems().clear();
                    label_header.setText(String.format("Songs from '%s'", item));
                    table.getItems().addAll(songs);
                }
            }
            catch (Exception ex){}
        });

        //Option for context menu
        MenuItem importMusicFoler = new MenuItem("Import music folder");
        //on click
        importMusicFoler.setOnAction(t -> {
            try {
                //get the selected playlist from the tableView
                //Context meny could have been called without selecting any playlists, hence a NullPointerException will
                //be raised. So, try catch is used.
                Playlist item = table_playlists.getItems().get(table_playlists.getSelectionModel().getSelectedIndex());

                final DirectoryChooser directoryChooser = new DirectoryChooser();
                //get the chosen directory
                final File selectedDirectory = directoryChooser.showDialog(btn_play.getScene().getWindow());

                //get the music from it
                getMusicFromFolder(selectedDirectory, item);

                //and add new data, fetched from the database.
                //here we get all the songs from the playlist

                //clear all the data, currently present in the tableView
                table.getItems().clear();
                label_header.setText(String.format("Songs from '%s'", item));
                table.getItems().addAll(new DAOPlaylist().getAllSongs(item));

            }
            catch (Exception ex){}
        });

        //Another option for the context menu. Plays the selected playlist
        MenuItem playPlaylist = new MenuItem("Play");
        //on click
        playPlaylist.setOnAction(t -> {
            try {
                //get the selected playlist from the tableView
                //Context meny could have been called without selecting any playlists, hence a NullPointerException will
                //be raised. So, try catch is used.
                Playlist item = table_playlists.getItems().get(table_playlists.getSelectionModel().getSelectedIndex());
                //Get the instance of the mp3 player (implemented, using thread-safe Singleton pattern)
                MP3Player player = MP3Player.getInstance();
                List<Song> songs = new DAOPlaylist().getAllSongs(item);
                if (songs.size() > 0) {
                    label_header.setText(String.format("Songs from '%s'", item));
                    table.getItems().addAll(songs);
                    //clears the list of music already added to the queue and adds new portion of music instead
                    player.clearAndAddToQueue(table.getItems());
                    //and plays from the beginning.
                    player.play(0);
                    //after we click "play", we want the slider to show the progress of music being played.
                    //that is why we bind it to the property updates, made by another thread in the MP3Player class
                    slider.valueProperty().bind(player.getTask().valueProperty());
                }
            }
            catch (Exception ex){}
        });
        //Adding created options to the context menu
        table_playlists.setContextMenu(new ContextMenu(importMusicFoler, menuShowSongsFromPlaylist, playPlaylist));

        //event handler for table view selection
        table_playlists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            //if the newly selected value is not null
            if (newSelection != null) {
                table_albums.getSelectionModel().clearSelection();
                //fetch songs from the playlist from database
                List<Song> songs = new DAOPlaylist().getAllSongs(newSelection);
                //if (songs.size() > 0) {
                    //clear the tableview
                    table.getItems().clear();
                    label_header.setText(String.format("Songs from '%s'", newSelection));
                    table.getItems().addAll(songs);
                    //clear the tableView with albums
                    table_albums.getItems().clear();
                    //get all the albums from the selected playlist
                    table_albums.getItems().addAll(new DAOPlaylist().getAllAlbums(newSelection));
                //}
            }

        });

    }

    /**
     * Initializes the TableView for songs
     * Also, initializes context menu for it.
     */
    private void initializeSongsTableAndMenu(){
        //binding properties from the Song class to the tableView columns
        //it will be then automatically updated/refreshed when modified from anywhere
        tCol_song.setCellValueFactory(new PropertyValueFactory<Song, String>("name"));
        tCol_Bitrate.setCellValueFactory(new PropertyValueFactory<Song, Integer>("bitrate"));
        tCol_Duration.setCellValueFactory(new PropertyValueFactory<Song, Integer>("durationHumanFriendly"));
        //Creating ObservalbeList to keep track of our songs
        ObservableList<Song> songs = FXCollections.observableArrayList();
        //Adding all the songs, already saved into the local db
        songs.addAll(new DAOSong().getAll());
        //and sending them to the tableView
        table.setItems(songs);

        //Option for context menu. Adds the selected song to the playlist.
        MenuItem menuToPlayList = new MenuItem("Add to PlayList");
        //when selected
        menuToPlayList.setOnAction(t -> {
            try {
                //get the selected song
                Song item = table.getItems().get(table.getSelectionModel().getSelectedIndex());
                //if not null
                if (item != null) {
                    //show dialog, asking about to which playlist the selected song should be added
                    showChoiceBoxForPlaylistAdding();
                }
            }
            catch (Exception ex){}
        });

        //Option for context menu. Adds the selected song to the album.
        MenuItem menuToAlbum = new MenuItem("Add to Album");
        //when selected
        menuToAlbum.setOnAction(t -> {
            try {
                //get the selected song
                Song item = table.getItems().get(table.getSelectionModel().getSelectedIndex());
                if (item != null) {
                    //show dialog asking about the album in which the selected music will be saved
                    showChoiceBoxForAlbumAdding();
                }
            }
            catch (Exception ex){}
        });

        //Option for the context menu. Plays the selected music.
        MenuItem playSong = new MenuItem("Play");
        //When selected
        playSong.setOnAction(t -> {
            //get the index of the selected song
            int selectedSongIndex = table.getSelectionModel().getSelectedIndex();
            //getting the mp3 player instance
            MP3Player player = MP3Player.getInstance();
            //clear the existing music queue and add songs from the tableView
            player.clearAndAddToQueue(table.getItems());
            //the index of songs in the table and in the queue are equal. Hence, we just call play n-th song.
            //that's it. Really ;)
            player.play(selectedSongIndex);
            //binding the slider to show the progress of music being played
            slider.valueProperty().bind(player.getTask().valueProperty());
        });

        //adding these options to the context menu
        table.setContextMenu(new ContextMenu(menuToPlayList, menuToAlbum, playSong));
    }

    /**
     * Initializes the TableView for albums.
     * Also, initializes context menu for it.
     */
    private void initializeAlbumTableAndMenu(){
        //binding album name property from Album class to the tableView column
        tCol_albums.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        //when the album is selected
        table_albums.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //if exists
            if (newValue != null){
                List<Song> songs = new DAOAlbum().getAllSongs(newValue);
                if (songs.size() > 0) {
                    label_header.setText(String.format("Songs from '%s'", newValue));
                    //clear all the songs from the table
                    table.getItems().clear();
                    //add songs from the album instead
                    table.getItems().addAll(songs);
                }
                table_playlists.getSelectionModel().clearSelection();
            }

        });

        //Option for the context menu. Shows songs from the album.
        MenuItem menuShowSongsFromAlbum = new MenuItem("Show songs");
        //When the option is selected
        menuShowSongsFromAlbum.setOnAction(t -> {
            try {
                //get the selected album
                Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());
                List<Song> songs = new DAOAlbum().getAllSongs(item);
                if (songs.size() > 0) {
                    label_header.setText(String.format("Songs from '%s'", item));
                    //clear songs tableView
                    table.getItems().clear();
                    //add songs from the album instead
                    table.getItems().addAll(songs);
                }
            }
            catch (Exception ex){}
        });

        //Option for context menu
        MenuItem importMusicFoler = new MenuItem("Import music folder");
        //on click
        importMusicFoler.setOnAction(t -> {
            try {
                //get the selected playlist from the tableView
                //Context meny could have been called without selecting any playlists, hence a NullPointerException will
                //be raised. So, try catch is used.
                Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());

                final DirectoryChooser directoryChooser = new DirectoryChooser();
                //get the chosen directory
                final File selectedDirectory = directoryChooser.showDialog(btn_play.getScene().getWindow());

                //get the music from it
                getMusicFromFolder(selectedDirectory, item);

                //and add new data, fetched from the database.
                //here we get all the songs from the playlist

                //clear all the data, currently present in the tableView
                table.getItems().clear();
                label_header.setText(String.format("Songs from '%s'", item));
                table.getItems().addAll(new DAOAlbum().getAllSongs(item));

            }
            catch (Exception ex){}
        });

        //Option for the context menu. Shows album cover.
        MenuItem showCover = new MenuItem("Show Cover");
        //When selected
        showCover.setOnAction(t -> {
            //get selected item
            Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());
            //Create dialog (JavaFX Dialogs library, included in Java8)
            Dialog<String> dialog = new Dialog<>();
            //create image view container
            ImageView imageView = new ImageView(item.getPicPath());
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            //add OK button
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
            //create grid layout
            GridPane grid = new GridPane();
            //with some space between the cells
            grid.setHgap(10);
            grid.setVgap(10);
            //set padding
            grid.setPadding(new Insets(20, 150, 10, 10));
            //add imageView in the first cell and the first row. Span it on 2 cells and 2 rows
            grid.add(imageView, 0, 0, 2, 2);
            //put it onto the dialog box
            dialog.getDialogPane().setContent(grid);
            //when OK button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    //since we just wanted to display the album cover, the return value is just a stub
                    return "";
                }
                //if closed - return null
                return null;
            });
            //show and wait for user to interact with it
            dialog.showAndWait();
        });

        //Option for the context menu. Plays the selected album
        MenuItem playAlbum = new MenuItem("Play");
        //when selected
        playAlbum.setOnAction(t -> {
            try {
                //get mp3 player instance
                MP3Player player = MP3Player.getInstance();
                Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());
                List<Song> songs = new DAOAlbum().getAllSongs(item);
                if (songs.size() > 0) {
                    //clear the queue. add the songs from the album instead.
                    player.clearAndAddToQueue(table.getItems());
                    label_header.setText(String.format("Songs from '%s'", table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex())));
                    //play from the beginning
                    player.play(0);
                    //bind slider to visualize the process of playing the sound file
                    slider.valueProperty().bind(player.getTask().valueProperty());
                }
            }
            catch (Exception ex){}
        });
        //add this to the context menu
        table_albums.setContextMenu(new ContextMenu(importMusicFoler, menuShowSongsFromAlbum, playAlbum, showCover));
    }

    /**
     * Shows JavaFX ChoiceDialog to choose the playlist, into which the selected sound file will be included
     */
    private void showChoiceBoxForPlaylistAdding(){
        //List with choices for choicebox
        List<Playlist> choices = new ArrayList<>();
        for(Playlist playlist : new DAOPlaylist().getAll()){
            //adding all the playlists saved to the database
            choices.add(playlist);
        }

        //Initializing the choicedialog
        //setting the first choice to be the default one
        ChoiceDialog<Playlist> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Select playlist");
        dialog.setHeaderText("The song will be added to the selected playlist");
        dialog.setContentText("Choose the playlist to add the song to");

        //getting dialog result. In this case it is a Playlist
        Optional<Playlist> result = dialog.showAndWait();
        //getting the selected song
        Song selectedSong = table.getItems().get(table.getSelectionModel().getSelectedIndex());
        //if the result exists, add it to the database and link to the playlist
        result.ifPresent(playlist -> {
            if (!new DAOPlaylist().containsSong(playlist, selectedSong))
                new DAOSong().addToPlaylist(playlist, selectedSong);
        });
    }

    /**
     * Shows JavaFX ChoiceDialog to choose the album, into which the selected sound file will be included
     */
    private void showChoiceBoxForAlbumAdding(){
        List<Album> choices = new ArrayList<>();

        //add all the albums, fetched from the database, which are linked to the selected playlist
        for(Album album : new DAOAlbum().getAll()){
            choices.add(album);
        }

        //if no choices
        if (choices.size() == 0){
            //Showing an error dialog
            showWarning("Error", "No albums found", "There are no albums saved in the database. Please, create a new album and try again");
            return;
        }

        //if everythings is OK, initializing the ChoiceDialog
        ChoiceDialog<Album> dialog = new ChoiceDialog<>(choices.get(0), choices);
        dialog.setTitle("Select album");
        dialog.setHeaderText("The song will be added to the selected album");
        dialog.setContentText("Choose the album to add the song to");

        //showing and waiting for the result
        Optional<Album> result = dialog.showAndWait();
        //getting the selected song
        Song selectedSong = table.getItems().get(table.getSelectionModel().getSelectedIndex());
        //and assigning in to the album
        result.ifPresent(album -> {
            DAOSong dao = new DAOSong();
            if (!new DAOPlaylist().containsSong(album.getPlayList(), selectedSong))
                dao.addToPlaylist(album.getPlayList(), selectedSong);
            if (!new DAOAlbum().containsSong(album, selectedSong))
                dao.addToAlbum(album, selectedSong);
        });
    }

    /**
     * Shows user a dialog box of type Warning.
     * @param title is a title for the dialog box
     * @param header the short caption of the warning
     * @param description a more detailed description of the warning
     */
    private void showWarning(String title, String header, String description){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(description);

        alert.showAndWait();
    }

    /**
     * Show a dialog box for album creation.
     */
    private void showAlbumCreationPopup(){
        //Creating a custom dialog. The result will be of type Album
        Dialog<Album> dialog = new Dialog<>();
        dialog.setTitle("New Album");
        dialog.setHeaderText("Create new album");

        //Adding "Create" and "Cancel" buttons
        ButtonType btnCreate = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCreate, ButtonType.CANCEL);

        //adding a grid layout.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        //a textfield for the name of the album
        TextField name = new TextField();
        name.setPromptText("Album name");
        //and the path to the image
        TextField imagePath = new TextField();
        imagePath.setPromptText("Album image path");
        //and a button to call the file chooser
        Button fileChooser = new Button("Choose");
        //when called
        fileChooser.setOnAction( event -> {
            //initialize it
            final FileChooser fileSelector = new FileChooser();
            //getting the selected file
            final File selectedFile = fileSelector.showOpenDialog(btn_play.getScene().getWindow());
            //if exists
            if (selectedFile != null) {
                //set URI path to the file into the appropriate text field
                imagePath.setText(selectedFile.toURI().toString());
            }
        });

        //initializing the choicebox
        ChoiceBox<Playlist> playListChoiceBox = new ChoiceBox<>();
        //adding all the playlists from the database
        playListChoiceBox.setItems(FXCollections.observableArrayList(new DAOPlaylist().getAll()));

        //positioning the elements into the grid
        grid.add(new Label("Album name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(imagePath, 0, 1);
        grid.add(fileChooser, 1, 1);
        grid.add(new Label("Playlist:"), 0, 2);
        grid.add(playListChoiceBox, 1, 2);

        //applying it to the view
        dialog.getDialogPane().setContent(grid);

        //Concurrent call to automatically request a focus to the first text field when the dialog box will be shown
        Platform.runLater(() -> name.requestFocus());

        //processing result
        dialog.setResultConverter(dialogButton -> {
            //if no empty fields found
            if (dialogButton == btnCreate && !name.getText().trim().isEmpty() && !imagePath.getText().trim().isEmpty() && playListChoiceBox.getSelectionModel().getSelectedItem() != null) {
                //creating a new album
                Album newAlbum = new Album(name.getText(), imagePath.getText(), playListChoiceBox.getValue());
                //getting Data Access Object class for the album
                DAOAlbum dao = new DAOAlbum();
                //saving the instance to the database
                dao.create(newAlbum);
                //fetching the recently saved album to get the unique id
                return  dao.get(newAlbum.getName(), playListChoiceBox.getValue());
            }
            else{
                //if some data is missing, show a warning dialog
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

    /**
     * Function for concurrent music directory parsing
     */
    private void tryGetMusic(){
        //if a config file contains the path to the directory
        if (Config.getInstance().getParameter(Config.MUSIC_FOLDER_PATH) != null)
        {
            //creating a new thread
            Thread musicParsingThread = new Thread(() -> {
                //getting Data access object for the Song class
                DAOSong dao = new DAOSong();
                //to detect when no more music files remain unnoticed
                int repeats = 0;
                while(repeats < 25){
                    //if BlockingListQueue contains at least one player
                    if (players.size() > 0){
                        //get only those players, status of which is READY. and for each of them
                        players.stream().filter(player -> player.getStatus() == MediaPlayer.Status.READY).forEach(player -> {
                            //get the duration of the sound file
                            int duration = (int) player.getMedia().getDuration().toSeconds();
                            //get the OS dependent source of the file
                            File file = decodeStringAndGetFile(player.getMedia().getSource());

                            //calculate bitrate. Standard formula for the bitrate calculation. file.length() the amount of memory, taken by a file
                            int bitrate = (int) (file.length() * 8 / duration / 1000);

                            Song song = null;
                            try {
                                //creating a Song object
                                song = new Song(URLDecoder.decode(file.getName(), "UTF-8").replace("%20", " "), file.toURI().toString(), duration, bitrate, Song.Quality.MEDIUM);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            //if saved successfully
                            if (dao.create(song)) {
                                //add it to the tableView
                                table.getItems().add(dao.get(file.getName()));
                                //and remove the player from the collection
                                players.remove(player);

                            }
                            else{
                                //probably already added or unsupported. So just removing it
                                players.remove(player);
                            }
                        });
                    }
                    else{
                        //if no more players in the collection
                        try {
                            //wait till the thread dies
                            Thread.currentThread().join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });

            //set thead to be daemon. Hence, if user closes the app, these threads will die not letting them to idle
            //and keeping the app to be shown as running
            musicParsingThread.setDaemon(true);
            //getting the music folder from the config file
            File file = new File(Config.getInstance().getParameter(Config.MUSIC_FOLDER_PATH));


            //for each file in the directory
            for(File f : file.listFiles()){
                //if it's extension is of type .mp3 or .wav
                if (f.getPath().contains(".mp3") || f.getPath().contains(".wav")) {
                    //create new media file
                    Media media = new Media(f.toURI().toString());
                    //add it to the player
                    MediaPlayer player = new MediaPlayer(media);
                    //add to the collection to wait until it becomes ready
                    players.add(player);
                }
            }
            //starting the thread, described above
            musicParsingThread.start();
        }
    }

    /**
     * Decodes the string to the uri path type. (file://file/path.mp3) and returns the file.
     * @param source the string to decode
     * @return the file, that has the decoded string address.
     */
    private File decodeStringAndGetFile (String source){
        //convert it to a URI path
        source = source.substring(source.lastIndexOf("file:/") + 6, source.length());
        File file = null;
        try {
            //decode the URI path to match UTF-8 localization and replace %20 with spaces
            file = new File(URLDecoder.decode(source, "UTF-8").replace("%20", " "));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * Function for concurrent music directory parsing
     */
    private void getMusicFromFolder(File folder, SongsContainer container){
        //creating a new thread
        Thread musicParsingThread = new Thread(() -> {
            //getting Data access object for the Song class
            DAOSong dao = new DAOSong();
            //to detect when no more music files remain unnoticed
            int repeats = 0;
            while(repeats < 25){
                //if BlockingListQueue contains at least one player
                if (players.size() > 0){
                    //get only those players, status of which is READY. and for each of them
                    players.stream().filter(player -> player.getStatus() == MediaPlayer.Status.READY).forEach(player -> {
                        //get the duration of the sound file
                        int duration = (int) player.getMedia().getDuration().toSeconds();
                        //get the OS dependent source of the file
                        File file = decodeStringAndGetFile(player.getMedia().getSource());

                        //calculate bitrate. Standard formula for the bitrate calculation. file.length() the amount of memory, taken by a file
                        int bitrate = (int) (file.length() * 8 / duration / 1000);

                        Song song = null;
                        try {
                            //creating a Song object
                            song = new Song(URLDecoder.decode(file.getName(), "UTF-8").replace("%20", " "), file.toURI().toString(), duration, bitrate, Song.Quality.MEDIUM);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //if saved successfully
                        dao.create(song);
                        if (!container.containsSong(container, dao.get(song.getName()))) {
                            //adding the song to the container
                            container.assignSong(dao.get(song.getName()));
                            table.getItems().add(dao.get(file.getName()));
                        }
                        //And removing it
                        players.remove(player);
                    });
                }
                else{
                    //if no more players in the collection
                    try {
                        //wait till the thread dies
                        Thread.currentThread().join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        });

        //set thead to be daemon. Hence, if user closes the app, these threads will die not letting them to idle
        //and keeping the app to be shown as running
        musicParsingThread.setDaemon(true);

        //for each file in the directory
        for(File f : folder.listFiles()){
            //if it's extension is of type .mp3 or .wav
            if (f.getPath().contains(".mp3") || f.getPath().contains(".wav")) {
                //create new media file
                Media media = new Media(f.toURI().toString());
                //add it to the player
                MediaPlayer player = new MediaPlayer(media);
                //add to the collection to wait until it becomes ready
                players.add(player);
            }
        }
        //starting the thread, described above
        musicParsingThread.start();
    }

    /**
     * Initializes shortcuts
     */
    public void init(){
        btn_addPlaylist.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> btn_addPlaylist.fire());
        btn_addAlbums.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN), () -> btn_addAlbums.fire());
        tryGetMusic();

        btn_play.getScene().getWindow().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()){
                    case SPACE:{
                        btn_play.fire();
                        break;
                    }
                    case LEFT:{
                        btn_prev.fire();
                        break;
                    }
                    case RIGHT:{
                        btn_next.fire();
                        break;
                    }
                }
            }

        });
    }

    /**
     * Event handler for the "Select folder" menuItem click
     * @param event actionEvent
     */
    @FXML
    void mItemSelectFolderClicked(ActionEvent event) {
        //open a directory chooser
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        //get the chosen directory
        final File selectedDirectory = directoryChooser.showDialog(btn_play.getScene().getWindow());
        if (selectedDirectory != null) {
            //saveits path to the config
            Config.getInstance().setParameter(Config.MUSIC_FOLDER_PATH, selectedDirectory.getAbsolutePath());
            //get the music from it
            tryGetMusic();
        }
    }

    /**
     * Event handler for "New Playlist" menuItem click
     * @param actionEvent
     */
    @FXML
    void createPlayList(ActionEvent actionEvent) {
        Playlist newPlayList;
        //showing the textinputdialog
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New playlist");
        dialog.setHeaderText("Enter the name of the new playlist");
        dialog.setContentText("Please enter the name of the playlist you wish to create:");
        //getting the result => the name of the playlist
        Optional<String> result = dialog.showAndWait();
        //if not null
        if (result.isPresent()){
            //creating a playlist
            newPlayList = new Playlist(result.get());
            //get data access object
            DAOPlaylist dao = new DAOPlaylist();
            //save the playlist to the database
            if(dao.create(newPlayList)) {
                //get the recently saved one to get the id
                newPlayList = dao.get(result.get());
                //add it to the table view
                table_playlists.getItems().add(newPlayList);
            }
        }
    }

    /**
     * Play button click event handler
     * @param event actionEvent
     */
    @FXML
    void playSong(ActionEvent event) {
        //get the player
        MP3Player mp3Player = MP3Player.getInstance();
        //if the player is not playing
        if (mp3Player.getPlayerStatus() == MediaPlayer.Status.UNKNOWN){
            //get the index of the song from the tableView
            int selectedSongIndex = table.getSelectionModel().getSelectedIndex();

            //if one is selected
            if (selectedSongIndex != -1) {
                //add songs from the tableView to the queue
                mp3Player.clearAndAddToQueue(table.getItems());
                //play the selected song
                mp3Player.play(selectedSongIndex);
            }
            else{
                //if none selected. add all the songs
                mp3Player.clearAndAddToQueue(table.getItems());
                //and play from the begining
                mp3Player.play(0);
            }
            //bind the slider value property to the Task thread to show the progress of a music file being played
            slider.valueProperty().bind(mp3Player.getTask().valueProperty());

            //if player is playing
        } else if (mp3Player.getPlayerStatus() == MediaPlayer.Status.PLAYING){
            //pause it
            mp3Player.pause();
            //unbind the slider
            slider.valueProperty().unbind();
            //let the user rewind the music
            slider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    //rewind the music
                    mp3Player.rewind(newValue.doubleValue());
                    //then bind again
                    slider.valueProperty().removeListener(this);
                    slider.valueProperty().bind(mp3Player.getTask().valueProperty());
                }
            });
            //if it is paused
        } else if (mp3Player.getPlayerStatus() == MediaPlayer.Status.PAUSED){
            //continue playing the music file
            mp3Player.proceed();
            //bind the slider value property
            slider.valueProperty().bind(mp3Player.getTask().valueProperty());
        }
    }

    /**
     * Create album menuItem click event handler
     * @param actionEvent actionEvent
     */
    @FXML
    void createAlbum(ActionEvent actionEvent) {
        //show dialog for album creation
        showAlbumCreationPopup();
    }

    /**
     * "Exit" menuItem click event handler
     * @param actionEvent actionEvent
     */
    @FXML
    void shutdown(ActionEvent actionEvent) {
        Platform.exit(); //shuts down the application
    }

    /**
     * Button >> click event handler
     * @param actionEvent actionEvent
     */
    @FXML
    void playNext(ActionEvent actionEvent) {
        //get mp3 player and play next sound
        MP3Player.getInstance().playNext();
    }

    /**
     * Button << click event handler
     * @param actionEvent actionEvent
     */

    @FXML
    void playPrev(ActionEvent actionEvent) {
        //get mp3 player and play prev sound file
        MP3Player.getInstance().playPrev();
    }

    @FXML
    void mItem_showAllSongs(ActionEvent actionEvent){
        label_header.setText(String.format("All songs"));
        //clear all the currently displayed data
        table.getItems().clear();
        //add all songs from the database to the tableView
        table.getItems().addAll(new DAOSong().getAll());
        table_playlists.getSelectionModel().clearSelection();
        table_albums.getSelectionModel().clearSelection();

    }

}
