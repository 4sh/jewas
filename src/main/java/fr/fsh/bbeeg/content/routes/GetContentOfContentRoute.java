package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.ContentType;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;

import java.io.InputStream;

/**
 * @author driccio
 */
public class GetContentOfContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetContentOfContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/content/[id]"));
        contentResource = _contentResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {

                InputStream content = contentResource.getContentOfContent(oi.id());
                ContentType contentType;

                switch (contentResource.getContentOfContentExtension(oi.id())) {
                    case "pdf":
                        contentType = ContentType.APP_PDF;
                       break;
                    case "jpg":
                    case "jpeg":
                        contentType = ContentType.IMG_JPG;
                         break;
                    case "png":
                        contentType = ContentType.IMG_PNG;
                         break;
                    case "gif":
                        contentType = ContentType.IMG_GIF;
                        break;
                    case "mp4":
                        contentType = ContentType.VID_MP4;
                        break;
                    case "mp3":
                        contentType = ContentType.AUD_MPEG;
                        break;
                    case "txt":
                    default :
                        contentType = ContentType.TXT_PLAIN;
                }

                request.respondFile()
                        .contentType(contentType)
                        .file(content);
            }
        };
    }
}
