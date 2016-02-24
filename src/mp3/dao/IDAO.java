package mp3.dao;

import java.util.List;

/**
 * Created by Spring on 2/25/2016.
 */
public interface IDAO<T> {
    T get(int id);
    List<T> getAll();
    boolean create(T value);
    boolean update(T value);
    boolean delete(T value);

}
