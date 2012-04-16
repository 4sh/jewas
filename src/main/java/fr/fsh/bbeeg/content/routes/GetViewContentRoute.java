package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.resources.ContentResource;
import fr.fsh.bbeeg.security.resources.ConnectedUserResource;
import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author driccio
 */
public class GetViewContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetViewContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/[id]/view.html"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                ContentDetail contentDetail = contentResource.viewContent(oi.id(),
                        ConnectedUserResource.instance.getUser(HttpRequestHelper.getSecurityToken(request)).id());
                //ContentDetail contentDetail = contentResource.getContentDetail(oi.id());
                params.put("content", contentDetail);

                String template;

                switch (contentDetail.header().type()) {
                    case TEXT: template = "content/view-text.ftl";
                        break;
                    case IMAGE: template = "content/view-image.ftl";
                        break;
                    case DOCUMENT: template = "content/view-document.ftl";
                        break;
                    case AUDIO: template = "content/view-audio.ftl";
                        break;
                    case VIDEO: template = "content/view-video.ftl";
                        break;
                    case EEG: template = "content/view-eeg.ftl";
                        break;
                    default: template = "content/view-text.ftl";
                }

                request.respondHtml().content(Templates.process(template, params));
            }
        };
    }
}
