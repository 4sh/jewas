package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.pojos.ContentDetail;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;
import jewas.template.Templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author carmarolli
 */
public class GetEditContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetEditContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/[id]/edit.html"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                ContentDetail contentDetail = contentResource.getContentDetail(oi.id());
                params.put("content", contentDetail);

                String template;

                switch (contentDetail.header().type()) {
                    case TEXT: template = "content/create-text.ftl";
                        break;
                    case IMAGE: template = "content/create-image.ftl";
                        break;
                    case DOCUMENT: template = "content/create-document.ftl";
                        break;
                    case AUDIO: template = "content/edit-audio.ftl";
                        break;
                    case VIDEO: template = "content/edit-video.ftl";
                        break;
                    case EEG: template = "content/edit-eeg.ftl";
                        break;
                    default: template = "content/create-text.ftl";
                }

                request.respondHtml().content(Templates.process(template, params));
            }
        };
    }
}