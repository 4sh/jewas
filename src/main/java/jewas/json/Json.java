package jewas.json;

import jewas.lang.Objects;
import jewas.reflection.Properties;
import jewas.reflection.Property;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

/**
 * Utility class used for JSON from/to conversions.
 */
public class Json {

    /**
     * Converts an object into a JSON object.
     *
     * @param sourceObject the Java object to convert in JSON object
     * @return a JSON string representation of the conversion.
     */
    public static String toJsonString(Object sourceObject) {
        if (Objects.isNull(sourceObject)) {
            return "{}";
        } else {
            StringBuilder json = new StringBuilder("{");
            Properties<?> props = Properties.properties(sourceObject.getClass());
            int i = 0;
            for (Property p : props.asList()) {
                json.append('"')
                        .append(p.name())
                        .append('"')
                        .append(":");
                //noinspection unchecked
                json.append(getJsonValue(p.get(sourceObject)));
                if (!isLastElement(props.asList().size(), i)) {
                    json.append(",");
                }
                i++;
            }
            json.append("}");
            return json.toString();
        }
    }

    /**
     * Test whether the given <code>elementIndex</code> is the last element of the given <code>collectionSize</code>
     *
     * @param collectionSize the total size of the collection
     * @param elementIndex   the index to test
     * @return <code>true</code> if the given element index is the last for the given collection size.
     */
    private static boolean isLastElement(int collectionSize, int elementIndex) {
        return elementIndex == collectionSize - 1;
    }

    /**
     * Convert the given object into its JSON representation.
     *
     * @param o the object to convert.
     * @return a JSON string representation of the given object.
     */
    private static String getJsonValue(Object o) {
        if (o == null) {
            return "null";
        }
        Class clazz = o.getClass();
        if (String.class.equals(clazz)) {
            return "\"" + o + "\"";
        } else if (o instanceof Collection) {
            return getJsonArray(((Collection) o).toArray());
        } else if (clazz.isArray()) {
            return getJsonArray((Object[]) o);
        } else if (isWrapperType(clazz) || clazz.isPrimitive() || clazz.equals(BigDecimal.class)) {
            return String.valueOf(o);
        } else {
            return toJsonString(o);
        }
    }

    /**
     * Convert the given array into a JSON array.
     *
     * @param array the array to convert in JSON.
     * @return a string representing the JSON conversion result.
     */
    private static String getJsonArray(Object[] array) {
        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (Object item : array) {
            json.append(getJsonValue(item));
            if (!isLastElement(array.length, i)) {
                json.append(",");
            }
            i++;
        }
        json.append("]");
        return json.toString();
    }

    /**
     * Test whether the given class is a wrapper for a Java primitive type.
     *
     * @param clazz the class to test
     * @return <code>true</code> the given class is a wrapper for a Java primitive type
     */
    public static boolean isWrapperType(Class clazz) {
        return clazz.equals(Boolean.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class);
    }
}
