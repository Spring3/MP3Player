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
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    private TableColumn<Song, String> tCol_Duration;

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
    private Slider phantomSlider;

    @FXML
    private TableColumn<Playlist, String> tCol_playlists;

    @FXML
    private TableColumn<Song, String> tCol_Bitrate;

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
    public AnchorPane bottomAnchorPane;

    @FXML
    public BorderPane borderPane;

    //A concurrent collection. Contains MusicPlayer objects.
    //Is used for fetching music metadata and other required data.
    private BlockingQueue<MediaPlayer> players = new LinkedBlockingQueue<>();
    private AtomicBoolean refreshTable = new AtomicBoolean(false);
    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");
    private AtomicInteger hightlightingThreadTimer = new AtomicInteger(0);

    /**
     * This method was implemented from the Initializable interface.
     * This method will be called right before the user sees the view.
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

        phantomSlider.valueProperty().bind(player.getTask().valueProperty());
        phantomSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            slider.valueProperty().set(newValue.doubleValue());
        });
        //start javafx.concurrent.Task in a separate Thread
        //Since then, the values to the bound objects will be automatically updated and refreshed once per frame
        player.startTask();

    }

    /**
     * Initializes shortcuts for such operations as:
     * 1) Creation of a playlist
     * 2) Creation of an album
     * 3) Triggering button play/pause/continue
     * 4) Triggering button playNext for next music playing
     * 5) Triggering button playPrev for previous music playing
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

        table.setRowFactory( tv -> {
            TableRow<Song> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                     refreshTable.set(true);
                }

                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Song song = row.getItem();
                    if (!song.getPath().isEmpty()) {
                        MP3Player player = MP3Player.getInstance();
                        player.clearAndAddToQueue(table.getItems());
                        player.play(table.getItems().indexOf(song));
                        refreshTable.set(false);
                    }
                }
            });

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    hightlightingThreadTimer.set(0);
                    refreshTable.set(true); //to prevent table row highlighting issue
                    int draggableObjectIndex = row.getIndex();
                    Dragboard dragboard = row.startDragAndDrop(TransferMode.MOVE);
                    dragboard.setDragView(row.snapshot(null, null));
                    ClipboardContent clipboardContent = new ClipboardContent();
                    clipboardContent.put(SERIALIZED_MIME_TYPE, draggableObjectIndex);
                    dragboard.setContent(clipboardContent);
                    event.consume();
                }
            });

            row.setOnDragOver(event -> {
                hightlightingThreadTimer.set(0);
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                    if (row.getIndex() != (Integer) dragboard.getContent(SERIALIZED_MIME_TYPE)) {
                        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                        event.consume();
                    }
                }
            });

            row.setOnDragDropped(event -> {
                hightlightingThreadTimer.set(0);
                Dragboard dragboard = event.getDragboard();
                if (dragboard.hasContent(SERIALIZED_MIME_TYPE)) {
                    int draggedObjectIndex = (Integer) dragboard.getContent(SERIALIZED_MIME_TYPE);
                    Song song = table.getItems().get(draggedObjectIndex);

                    int dropIndex;
                    if (row.isEmpty()) {
                        dropIndex = table.getItems().size();
                    } else {
                        dropIndex = row.getIndex();
                    }

                    Song temp = table.getItems().get(dropIndex);
                    table.getItems().set(draggedObjectIndex, temp);
                    table.getItems().set(dropIndex, song);

                    event.setDropCompleted(true);
                    table.getSelectionModel().select(song);
                    MP3Player.getInstance().swapSongs(draggedObjectIndex, dropIndex);
                    event.consume();

                }
            });
            return row ;
        });

        Thread selectionThread = new Thread(() -> {
            MP3Player player = MP3Player.getInstance();
            while(true){
                int index = player.getCurrentSongIndex();

                if (!refreshTable.get() || hightlightingThreadTimer.get() > 6){   //if the item remains selected for more than 6 seconds
                    table.getSelectionModel().select(index);
                    hightlightingThreadTimer.set(0);
                }
                else if (refreshTable.get() && hightlightingThreadTimer.get() <= 6){
                    hightlightingThreadTimer.incrementAndGet();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        selectionThread.setDaemon(true);
        selectionThread.start();


        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MP3Player mp3Player = MP3Player.getInstance();
                if(mp3Player.isRewinding()) {
                    return;
                }

                if ((newValue.doubleValue() - oldValue.doubleValue() < -1 || newValue.doubleValue() - oldValue.doubleValue() >= 2) && newValue.doubleValue() != 0) {
                    //rewind the music
                    mp3Player.rewind(newValue.doubleValue());
                }
            }
        });
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

        //Option for context menu
        MenuItem importMusicFiles = new MenuItem("Import music files to this playlist");
        //on click
        importMusicFiles.setOnAction(t -> {
            try {
                //get the selected playlist from the tableView
                //Context menu could have been called without selecting any playlists, hence a NullPointerException will
                //be raised. So, try catch is used.
                Playlist item = table_playlists.getItems().get(table_playlists.getSelectionModel().getSelectedIndex());

                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose music files");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
                //get the chosen directory
                List<File> selectedFiles = fileChooser.showOpenMultipleDialog(btn_play.getScene().getWindow());
                if (selectedFiles != null){
                    for(File file : selectedFiles){
                        if (file.getPath().contains(".mp3") || file.getPath().contains(".wav")) {
                            //create new media file
                            Media media = new Media(file.toURI().toString());
                            //add it to the player
                            MediaPlayer player = new MediaPlayer(media);
                            //add to the collection to wait until it becomes ready
                            players.add(player);
                        }
                    }
                }
                startParsingThread(item);


                //clear all the data, currently present in the tableView
                table.getItems().clear();
                label_header.setText(String.format("Songs from '%s'", item));
                table.getItems().addAll(new DAOPlaylist().getAllSongs(item));

            }
            catch (Exception ex){}
        });


        //Adding created options to the context menu
        table_playlists.setContextMenu(new ContextMenu(importMusicFiles));

        //event handler for table view selection
        table_playlists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            //if the newly selected value is not null
            if (newSelection != null) {
                borderPane.setRight(null);
                table_albums.getSelectionModel().clearSelection();
                //fetch songs from the playlist from database
                List<Song> songs = new DAOPlaylist().getAllSongs(newSelection);
                //clear the tableview
                table.getItems().clear();
                label_header.setText(String.format("Songs from '%s'", newSelection));
                table.getItems().addAll(songs);
                if (songs.size() == 0)
                    table.getItems().add(new Song());
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
        tCol_Bitrate.setCellValueFactory(new PropertyValueFactory<Song, String>("bitrateString"));
        tCol_Duration.setCellValueFactory(new PropertyValueFactory<Song, String>("durationHumanFriendly"));
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

        //adding these options to the context menu
        table.setContextMenu(new ContextMenu(menuToPlayList, menuToAlbum));
    }

    /**
     * Initializes the TableView for albums.
     * Also, initializes context menu for it.
     */
    private void initializeAlbumTableAndMenu(){
        //binding album name property from Album class to the tableView column
        tCol_albums.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        table_albums.getItems().addAll(new DAOAlbum().getAll());
        //when the album is selected
        table_albums.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //if exists
            if (newValue != null){
                List<Song> songs = new DAOAlbum().getAllSongs(newValue);
                label_header.setText(String.format("Songs from '%s'", newValue));
                //clear all the songs from the table
                table.getItems().clear();
                //add songs from the album instead
                table.getItems().addAll(songs);
                if (songs.size() == 0){
                    table.getItems().add(new Song());
                }
                ImageView imageView = new ImageView(newValue.getPicPath());
                imageView.setFitHeight(250);
                imageView.setFitWidth(330);
                imageView.setLayoutY(100);
                imageView.setTranslateY(42);
                borderPane.setRight(imageView);
                table_playlists.getSelectionModel().clearSelection();
            }

        });

        //Option for context menu
        MenuItem importMusicFiles = new MenuItem("Import music files to this album");
        //on click
        importMusicFiles.setOnAction(t -> {
            try {
                //get the selected playlist from the tableView
                //Context menu could have been called without selecting any playlists, hence a NullPointerException will
                //be raised. So, try catch is used.
                Album item = table_albums.getItems().get(table_albums.getSelectionModel().getSelectedIndex());

                final FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choose music files");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav"));
                //get the chosen directory
                List<File> selectedFiles = fileChooser.showOpenMultipleDialog(btn_play.getScene().getWindow());
                if (selectedFiles != null){
                    for(File file : selectedFiles){
                        if (file.getPath().contains(".mp3") || file.getPath().contains(".wav")) {
                            //create new media file
                            Media media = new Media(file.toURI().toString());
                            //add it to the player
                            MediaPlayer player = new MediaPlayer(media);
                            //add to the collection to wait until it becomes ready
                            players.add(player);
                        }
                    }
                }
                startParsingThread(item);


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

        //add this to the context menu
        table_albums.setContextMenu(new ContextMenu(importMusicFiles, showCover));
    }

    /**
     * Shows JavaFX ChoiceDialog to choose to which playlist should the selected sound file be included
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
     * Shows JavaFX ChoiceDialog to choose to which album should the selected sound file be included
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
            if (!new DAOAlbum().containsSong(album, selectedSong))
                dao.addToAlbum(album, selectedSong);
        });
    }

    /**
     * Shows user a dialog box of type Warning. Describes the error, which occurred.
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
     * Contains textfields for name and album cover image path
     * Also, contains a choicebox with the existing playlists
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
        grid.setHgap(60);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 100, 10, 10));

        //a textfield for the name of the album
        TextField name = new TextField();
        name.setPromptText("Album name");
        //and the path to the image
        TextField imagePath = new TextField();
        imagePath.setPromptText("Album image path");
        //and a button to call the file chooser
        Button fileChooser = new Button("Choose");
        fileChooser.setPadding(new Insets(5, 15, 5, 18));
        fileChooser.setTranslateX(4);
        //when called
        fileChooser.setOnAction( event -> {
            //initialize it
            final FileChooser fileSelector = new FileChooser();
            fileSelector.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            //getting the selected file
            final File selectedFile = fileSelector.showOpenDialog(btn_play.getScene().getWindow());
            //if exists
            if (selectedFile != null) {
                //set URI path to the file into the appropriate text field
                imagePath.setText(selectedFile.toURI().toString());
            }
        });

        //positioning the elements into the grid
        grid.add(new Label("Album name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(imagePath, 1, 1);
        grid.add(fileChooser, 2, 1);

        //applying it to the view
        dialog.getDialogPane().setContent(grid);

        //Concurrent call to automatically request a focus to the first text field when the dialog box will be shown
        Platform.runLater(() -> name.requestFocus());

        //processing result
        dialog.setResultConverter(dialogButton -> {
            //if no empty fields found
            if (dialogButton == btnCreate && !name.getText().trim().isEmpty() && !imagePath.getText().trim().isEmpty()) {
                //creating a new album
                Album newAlbum = new Album(name.getText(), imagePath.getText());
                //getting Data Access Object class for the album
                DAOAlbum dao = new DAOAlbum();
                //saving the instance to the database
                dao.create(newAlbum);
                //fetching the recently saved album to get the unique id
                return  dao.get(newAlbum.getName());
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
     * Navigates to the main music folder, adds each file to the queue and starts a daemon slave-thread, which waits until
     * the files are ready to be processed and then processes the files. The new data is being added to the database
     * and the duplicated is omitted.
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
                //if BlockingListQueue contains at least one player
                while(players.size() > 0) {
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
                            player.dispose();
                            players.remove(player);

                        } else {
                            //probably already added or unsupported. So just removing it
                            player.dispose();
                            players.remove(player);
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                //if no more players in the collection
                try {
                    //wait till the thread dies
                    Thread.currentThread().join();
                    Thread.currentThread().interrupt();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
     * Decodes the string to the uri path type. In format (file://file/path.mp3) and returns the file with such path
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

    private void startParsingThread(SongsContainer container){
        Thread musicParsingThread = new Thread(() -> {
            //getting Data access object for the Song class
            DAOSong dao = new DAOSong();
            //to detect when no more music files remain unnoticed
            int repeats = 0;
            while(players.size() > 0) {
                //if BlockingListQueue contains at least one player
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
                        song = dao.get(file.getName());
                        table.getItems().add(song);
                    }
                    //And removing it
                    player.dispose();
                    players.remove(player);
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //if no more players in the collection
            try {
                //wait till the thread dies
                Thread.currentThread().join();
                Thread.currentThread().interrupt();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

        //set thread to be daemon. Hence, if user closes the app, these threads will die not letting them to idle
        //and keeping the app to be shown as running
        musicParsingThread.setDaemon(true);
        musicParsingThread.start();
    }

    /**
     * Event handler for the "Select folder" menuItem click
     * @param event actionEvent
     */
    @FXML
    void mItemSelectFolderClicked(ActionEvent event) {
        //open a directory chooser
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select your main folder with music");
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
     * Shows a dialog to create a new playlist
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
     * Checks the status of the current player.
     * If the user hadn't selected the music file from the table, the player starts to play the very first file from the
     * queue. Otherwise, the index of the selected music file in the tableView is selected as the first one.
     * If UNKNOWN - initializes a player, sets up the sound files queue and runs the sound file.
     * If PLAYING - pauses the player and unbinds the slider, allowing user to rewind the music
     * If PAUSED - continues to play the music from the moment it stopped.
     * @param event actionEvent
     */
    @FXML
    void playSong(ActionEvent event) {
        if( table.getItems().size() == 0){
            return;
        }
        //get the player
        MP3Player mp3Player = MP3Player.getInstance();
        //If there is only a stub object (when the playlist or album is empty)

        //if the player is not playing
        if (mp3Player.getPlayerStatus() == MediaPlayer.Status.UNKNOWN){
            if (table.getItems().get(0).getPath().isEmpty()){
                return;
            }
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
            //slider.valueProperty().bind(mp3Player.getTask().valueProperty());

            //if player is playing
        } else if (mp3Player.getPlayerStatus() == MediaPlayer.Status.PLAYING){
            //pause it
            mp3Player.pause();
            //if it is paused
        } else if (mp3Player.getPlayerStatus() == MediaPlayer.Status.PAUSED){
            //continue playing the music file
            mp3Player.proceed();
        }
    }

    /**
     * Shows popup for album creation
     * @param actionEvent actionEvent
     */
    @FXML
    void createAlbum(ActionEvent actionEvent) {
        //show dialog for album creation
        showAlbumCreationPopup();
    }

    /**
     * Shuts the application down
     * @param actionEvent actionEvent
     */
    @FXML
    void shutdown(ActionEvent actionEvent) {
        Platform.exit(); //shuts down the application
    }

    /**
     * Commands the player to play the next song
     * @param actionEvent actionEvent
     */
    @FXML
    void playNext(ActionEvent actionEvent) {
        //get mp3 player and play next sound
        MP3Player.getInstance().playNext();
    }

    /**
     * Commands the player to play the previous song
     * @param actionEvent actionEvent
     */

    @FXML
    void playPrev(ActionEvent actionEvent) {
        //get mp3 player and play prev sound file
        MP3Player.getInstance().playPrev();
    }

    /**
     * Shows unsorted songs. Adds them into the tableView.
     * @param actionEvent actionEvent
     */
    @FXML
    void mItem_showAllSongs(ActionEvent actionEvent){
        borderPane.setRight(null);
        label_header.setText(String.format("Unsorted Songs"));
        //clear all the currently displayed data
        table.getItems().clear();
        //add unsorted songs from the database to the tableView
        table.getItems().addAll(new DAOSong().getAll());
        table_playlists.getSelectionModel().clearSelection();
        table_albums.getSelectionModel().clearSelection();

    }
}
