package jewas.collection;

import java.util.ArrayList;

/**
 * @author fcamblor
 * Added support for component types in array lists
 */
public class TypedArrayList<T> extends ArrayList<T> implements TypedList<T> {

    private Class<T> componentType;

    public TypedArrayList(Class<T> componentType){
        this.componentType = componentType;
    }

    @Override
    public Class<T> getComponentType() {
        return componentType;
    }
}
