package jewas.http.data;

/**
 * @author fcamblor
 */
public class StreamedBodyParameters implements BodyParameters {
    protected final StreamedContent content;

    public StreamedBodyParameters(StreamedContent content){
        this.content = content;
    }
}
