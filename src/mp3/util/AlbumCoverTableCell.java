package mp3.util;

import javafx.scene.control.TableCell;
import javafx.scene.image.ImageView;
import mp3.model.Album;

public class AlbumCoverTableCell extends TableCell<Boolean, Album> {

    public AlbumCoverTableCell(){

    }

    @Override
    protected void updateItem(Album item, boolean empty) {
        if (item != null){
            ImageView imageView = new ImageView(item.getPicPath());
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            setGraphic(imageView);
        }

    }
}
