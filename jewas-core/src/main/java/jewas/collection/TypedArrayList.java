package jewas.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fcamblor
 * Added support for component types in array lists
 */
public class TypedArrayList<T> extends ArrayList<T> implements TypedList<T> {

    private Class<T> componentType;

    public TypedArrayList(Class<T> componentType){
        super();
        this.componentType = componentType;
    }

    public TypedArrayList(TypedArrayList<T> anotherList){
        this(anotherList.getComponentType(), anotherList);
    }

    public TypedArrayList(Class<T> clazz, List<T> anotherList){
        super(anotherList);
        this.componentType = clazz;
    }

    @Override
    public Class<T> getComponentType() {
        return componentType;
    }
}
