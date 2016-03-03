# MP3Player

Written in Java using JavaFX for UI and sqlite database as the main data keep.
Requires Java 8 Update 43 + since this app uses JavaFX Dialogs, which were included in the Java sdk since Update 43

###Threads:
- 1) Using javafx.concurrent.Task to update slider and timer
- 2) Thread for highlighting the song that is currently being played.
- 3) Thread for parallel importing of the new data (mp3 or wav files)

