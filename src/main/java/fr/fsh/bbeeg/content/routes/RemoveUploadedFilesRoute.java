package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.persistence.TempFiles;
import fr.fsh.bbeeg.common.resources.SuccessObject;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;
import jewas.json.Json;

/**
 * @author driccio
 */
public class RemoveUploadedFilesRoute extends AbstractRoute {
    public RemoveUploadedFilesRoute(){
        super(HttpMethodMatcher.DELETE, new PatternUriPathMatcher("/upload"));
    }

    public static class FilesQueryObject {
        private String fileNames;

        public FilesQueryObject fileNames(String _fileNames){
            this.fileNames = _fileNames;
            return this;
        }

        public String fileNames(){
            return this.fileNames;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                FilesQueryObject fqo = toContentObject(bodyParameters, FilesQueryObject.class);

                String[] fileNames = (String[]) Json.instance().fromJsonString(fqo.fileNames(), String[].class);

                TempFiles.removeFiles(fileNames);

                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}
