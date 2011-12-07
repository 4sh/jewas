package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.SearchInfo;
import fr.fsh.bbeeg.content.pojos.AdvancedSearchQueryObject;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.resources.ContentResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fcamblor
 */
public class GetAdvancedSearchContent extends AbstractRoute {

    /**
     * Class logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(GetAdvancedSearchContent.class);

    private ContentResource contentResource;

    public GetAdvancedSearchContent(ContentResource _contentResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/advancedSearch"));
        contentResource = _contentResource;
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final AdvancedSearchQueryObject query =
                toQueryObject(parameters, AdvancedSearchQueryObject.class);
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                logger.debug("Advanced Query object: " + query);

                // For tests purposes only... will have to delete this..
                int offset = 1;
                if (query.startingOffset() != -1) {
                    offset = query.startingOffset();
                }

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
