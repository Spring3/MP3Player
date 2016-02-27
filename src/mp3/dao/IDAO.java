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
     * @return fetched object that matches the query
     */
    T get(int id);

    /**
     * Searches for all entities of type T
     * @return a list of found objects. If none found - the list is empty.
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
     * Deleted the entity from the database table
     * @param value original object
     * @return ture if deleted successfully
     */
    boolean delete(T value);

}
