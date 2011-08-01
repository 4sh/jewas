package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.SearchInfo;
import fr.fsh.bbeeg.content.resources.ContentSearchResult;
import jewas.http.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fcamblor
 */
public class GetAdvancedSearchContent extends AbstractRoute {
    public GetAdvancedSearchContent() {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/advancedSearch"));
    }

    // FIXME : to externalize into resources package (when ContentQueryObject will have been renamed)
    public static class SearchQueryObject {
        private Date from;
        private Date to;
        private String[] searchTypes;
        private String criterias;
        private String[] authors;
        private Integer startingOffset = -1;
        private Integer numberOfContents = Integer.valueOf(10);

        public SearchQueryObject from(Date _from){
            this.from = _from;
            return this;
        }

        public Date from(){
            return this.from;
        }

        public SearchQueryObject to(Date _to){
            this.to = _to;
            return this;
        }

        public Date to(){
            return this.to;
        }

        public SearchQueryObject searchTypes(String[] _searchTypes){
            this.searchTypes = _searchTypes;
            return this;
        }

        public String[] searchTypes(){
            return this.searchTypes;
        }

        public SearchQueryObject criterias(String _criterias){
            this.criterias = _criterias;
            return this;
        }

        public String criterias(){
            return this.criterias;
        }

        public SearchQueryObject authors(String[] _authors){
            this.authors = _authors;
            return this;
        }

        public String[] authors(){
            return this.authors;
        }

        public SearchQueryObject startingOffset(Integer _startingOffset){
            this.startingOffset = _startingOffset;
            return this;
        }

        public Integer startingOffset(){
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

                if (query.authors != null) {
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
