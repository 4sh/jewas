package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.content.pojos.ContentType;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.template.Templates;

import java.util.HashMap;
import java.util.Map;

/**
 * @author driccio
 */
public class GetCreateContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetCreateContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/text/create.html"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Map<String, Object> params = new HashMap<String, Object>();
                Long id = contentResource.createContent(ContentType.TEXT);
                System.out.println(id);
                params.put("contentId", id);
                request.respondHtml().content(Templates.process("content/create-text.ftl", params));
            }
        };
    }
}
