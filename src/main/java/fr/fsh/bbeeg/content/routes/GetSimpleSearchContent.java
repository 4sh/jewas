package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.SearchInfo;
import fr.fsh.bbeeg.content.resources.ContentResource;
import fr.fsh.bbeeg.content.resources.ContentSearchResult;
import jewas.http.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fcamblor
 */
public class GetSimpleSearchContent extends AbstractRoute {
    private ContentResource contentResource;

    public GetSimpleSearchContent(ContentResource _contentResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/search"));
        contentResource = _contentResource;
    }

    // FIXME : to externalize into resources package (when ContentQueryObject will have been renamed)
    public static class SearchQueryObject {
        private String query;
        private Integer startingOffset = -1;
        private Integer numberOfContents = Integer.valueOf(10);

        public SearchQueryObject query(String _query) {
            this.query = _query;
            return this;
        }

        public String query() {
            return this.query;
        }

        public SearchQueryObject startingOffset(Integer _startingOffset) {
            this.startingOffset = _startingOffset;
            return this;
        }

        public Integer startingOffset() {
            return this.startingOffset;
        }

        public SearchQueryObject numberOfContents(Integer _numberOfContents){
            this.numberOfContents = _numberOfContents;
            return this;
        }

        public Integer numberOfContents(){
            return this.numberOfContents;
        }
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final SearchQueryObject query = toQueryObject(parameters, SearchQueryObject.class);
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                List<ContentSearchResult> results = new ArrayList<ContentSearchResult>();

                // For tests purposes only... will have to delete this..
                int offset = 1;
                if (query.startingOffset() != -1) {
                    offset = query.startingOffset();
                }

                for(int i=0; i<query.numberOfContents(); i++){
                    results.add(new ContentSearchResult().id(String.valueOf(offset))
                            .author("fcamblor")
                            .title("Contenu " + offset)
                            .creationDate(new Date())
                            .mediaType("audio")
                            .description("blablabla"));
                    offset++;
                }

                if (query.query().contains("easterEgg")) {
                    results.add(new ContentSearchResult().id(String.valueOf(offset))
                            .author("fcamblor")
                            .title("Contenu (caché) " + offset)
                            .creationDate(new Date())
                            .mediaType("audio")
                            .description("blablabla"));
                    offset++;
                }

                SearchInfo<ContentSearchResult> infos = new SearchInfo<ContentSearchResult>().results(results).endingOffset(offset - 1);

                request.respondJson().object(infos, new TypeToken<SearchInfo<ContentSearchResult>>(){}.getType());
            }
        };
    }
}
