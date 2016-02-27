package mp3.dao;

import java.util.List;

/**
 * Interface with basic CRUD methods to create, read, update and delete data from/to the database
 * @param <T> type of the data, operated by the methods
 */
public interface IDAO<T> {
    /**
     * Searches for the entity in the database by the given id
     * @param id the key to the desired entity
     * @return fetched object that matches the query. If none was found, returns null
     */
    T get(int id);

    /**
     * Searches for all entities of the type T, where T - depends on the dao, that is used.
     * For example DAOSong has T of type Song
     * @return a list of found objects. If none found - the empty list.
     */
    List<T> getAll();

    /**
     * Saves the object to the database
     * @param value the object to save
     * @return true if saved successfully
     */
    boolean create(T value);

    /**
     * Updates the data for the entity
     * @param value the original object
     * @return true if updated successfully
     */
    boolean update(T value);

    /**
     * Deletes the entity from the database table
     * @param value original object
     * @return true if deleted successfully
     */
    boolean delete(T value);

}
