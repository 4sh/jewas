package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.content.pojos.ContentPublicationDetail;
import fr.fsh.bbeeg.content.pojos.ContentStatus;
import fr.fsh.bbeeg.content.pojos.ContentStatusQueryObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author driccio
 */
public class UpdateContentStatusRoute extends AbstractRoute {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(UpdateContentStatusRoute.class);

    private ContentResource contentResource;

    public UpdateContentStatusRoute(ContentResource _contentResource) {
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/status/[id]"));
        this.contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest httpRequest, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                ContentStatusQueryObject csqo = toContentObject(bodyParameters, ContentStatusQueryObject.class);

                contentResource.updateContentStatus(oi.id(),
                        ContentStatus.valueOf(csqo.newStatus()),
                        new ContentPublicationDetail(
                        csqo.startPublicationDate(),
                        csqo.endPublicationDate(),
                        csqo.comments()));
                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
