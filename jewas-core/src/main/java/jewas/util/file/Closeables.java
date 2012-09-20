package jewas.util.file;

import java.io.Closeable;
import java.io.IOException;

/**
 * @author fcamblor
 */
public class Closeables {
    public static void defensiveClose(Closeable closeable){
        if(closeable == null){
            return;
        }

        try {
            closeable.close();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
