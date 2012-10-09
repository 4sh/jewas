package jewas.resources;

/**
 * @author fcamblor
 */
public abstract class AbstractResource implements Resource {
    private String path;

    protected AbstractResource(String path) {
        this.path = path;
    }

    public AbstractResource path(String _path) {
        this.path = _path;
        return this;
    }

    public String path() {
        return this.path;
    }
}
