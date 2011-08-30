package jewas.http.data;

/**
 * @author fcamblor
 */
public class NamedString extends NamedHttpData {

    private final String value;

    public NamedString(String name, String value){
        super(name);
        this.value = value;
    }

    @Override
    public boolean isCompleted() {
        // by default, String read will always be completed
        return true;
    }

    public String value(){
        return this.value;
    }
}
