package mp3.model;

import mp3.dao.DAOPlaylist;

/**
 * Created by Spring on 2/25/2016.
 */
public class Album {

    public Album(){

    }

    public Album(String name, String picPath, Playlist playlist){
        setName(name);
        setPicPath(picPath);
        setPlaylistId(playlist.getId());
    }

    public Album(String name, String picPath, int playlistId){
        setName(name);
        setPicPath(picPath);
        setPlaylistId(playlistId);
    }



    private int id;
    private String name;
    private String picPath;
    private int playlistId;


    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPicPath(){
        return picPath;
    }

    public void setPicPath(String path){
        picPath = path;
    }

    public Playlist getPlayList(){
        return new DAOPlaylist().get(playlistId);
    }

    public void setPlaylistId(int id){
        this.playlistId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Album album = (Album) o;

        if (getId() != album.getId()) return false;
        if (getName() != null ? !getName().equals(album.getName()) : album.getName() != null) return false;
        return !(getPicPath() != null ? !getPicPath().equals(album.getPicPath()) : album.getPicPath() != null);

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getPicPath() != null ? getPicPath().hashCode() : 0);
        return result;
    }


}
