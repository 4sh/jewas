package jewas.collection;

import java.util.List;

/**
 * @author fcamblor
 * Providing additionnal information on List types
 */
public interface TypedList<T> extends List<T> {
    public Class<T> getComponentType();
}
