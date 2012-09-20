package jewas.http.data;

import java.util.Collections;
import java.util.List;

/**
 * @author fcamblor
 * HttpData aggregator for body content
 */
public interface BodyParameters {

    public static enum Types{
        FORM {
            @Override
            public BodyParameters createBodyParameters(List contentData) {
                return new FormBodyParameters(Collections.<NamedHttpData>unmodifiableList(contentData));
            }
        }, STREAMED {
            @Override
            public BodyParameters createBodyParameters(List<HttpData> contentData) {
                return new StreamedBodyParameters((StreamedContent)contentData);
            }
        }, EMPTY {
            @Override
            public BodyParameters createBodyParameters(List<HttpData> contentData) {
                return new EmptyBodyParameters();
            }
        };

        public abstract BodyParameters createBodyParameters(List<HttpData> contentData);
    }
}
