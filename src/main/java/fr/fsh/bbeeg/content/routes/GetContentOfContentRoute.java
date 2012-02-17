package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

import java.nio.file.Path;
import java.util.Locale;

/**
 * @author driccio
 */
public class GetContentOfContentRoute extends AbstractRoute {
    private ContentResource contentResource;

    public GetContentOfContentRoute(ContentResource _contentResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/content/[id]"));
        contentResource = _contentResource;
    }

    public static class ObjectId {
        private String id;

        public ObjectId id(String _id){
            this.id = _id;
            return this;
        }

        public String id(){
            return this.id;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {

                Path content = null;
                String extension;

                if (oi.id().startsWith("tmp_")) {
                    content = TempFiles.getPath(oi.id());

                    extension = oi.id().split("\\.")[1];
                } else {
                    content = contentResource.getContentOfContent(Long.parseLong(oi.id()));
                    extension = contentResource.getContentOfContentExtension(Long.parseLong(oi.id()));
                }

                ContentType contentType;

                switch (extension.toLowerCase(Locale.getDefault())) {
                    case "pdf":
                        contentType = ContentType.APP_PDF;
                        break;
                    case "jpg":
                        contentType = ContentType.IMG_JPG;
                        break;
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
                    default:
                        contentType = ContentType.TXT_PLAIN;
                }

                request.respondFile()
                        .contentType(contentType)
                        .file(content);
            }
        };
    }
}
