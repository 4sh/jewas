package jewas.http.data;

/**
 * @author fcamblor
 * Class representation of named http data read in body content
 * Particularly while reading basic form submissions
 */
public abstract class NamedHttpData implements HttpData {
    protected String name;

    protected NamedHttpData(String name){
        this.name = name;
    }

    public NamedHttpData name(String _name){
        this.name = _name;
        return this;
    }

    public String name(){
        return this.name;
    }
}
