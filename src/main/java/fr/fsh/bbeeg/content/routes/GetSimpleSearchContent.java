package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.SearchInfo;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.SimpleSearchQueryObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;

import java.util.ArrayList;
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

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final SimpleSearchQueryObject query =
                toQueryObject(parameters, SimpleSearchQueryObject.class);
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                // For tests purposes only... will have to delete this..
                int offset = 1;
                if (query.startingOffset() != -1) {
                    offset = query.startingOffset();
                }

//                for(int i=0; i<query.numberOfContents(); i++){
//                    results.add(new ContentSearchResult().id(String.valueOf(offset))
//                            .author("fcamblor")
//                            .title("Contenu " + offset)
//                            .creationDate(new Date())
//                            .mediaType("audio")
//                            .description("blablabla"));
//                    offset++;
//                }
//
//                if (query.query().contains("easterEgg")) {
//                    results.add(new ContentSearchResult().id(String.valueOf(offset))
//                            .author("fcamblor")
//                            .title("Contenu (caché) " + offset)
//                            .creationDate(new Date())
//                            .mediaType("audio")
//                            .description("blablabla"));
//                    offset++;
//                }

                List<ContentHeader> contentHeaders = new ArrayList<ContentHeader>();
                contentResource.fetchSearch(contentHeaders, query);
                SearchInfo<ContentHeader> infos =
                        new SearchInfo<ContentHeader>()
                                .results(contentHeaders)
                                .endingOffset(offset + contentHeaders.size() - 1);

                request.respondJson().object(infos,
                        new TypeToken<SearchInfo<ContentHeader>>(){}.getType());
            }
        };
    }
}
