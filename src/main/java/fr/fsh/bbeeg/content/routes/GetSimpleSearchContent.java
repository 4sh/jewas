package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.common.resources.SearchInfo;
import fr.fsh.bbeeg.content.pojos.ContentHeader;
import fr.fsh.bbeeg.content.pojos.SearchMode;
import fr.fsh.bbeeg.content.pojos.SimpleSearchQueryObject;
import fr.fsh.bbeeg.content.resources.ContentResource;
import fr.fsh.bbeeg.security.resources.ConnectedUserResource;
import fr.fsh.bbeeg.security.resources.HttpRequestHelper;
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
public class GetSimpleSearchContent extends AbstractRoute {

    /**
     * Class logger.
     */
    private final static Logger logger = LoggerFactory.getLogger(GetSimpleSearchContent.class);

    private ContentResource contentResource;

    public GetSimpleSearchContent(ContentResource _contentResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/search"));
        contentResource = _contentResource;
    }

    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final SimpleSearchQueryObject query =
                toQueryObject(parameters, SimpleSearchQueryObject.class);
        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                logger.debug("Simple Query object: " + query);
                // For tests purposes only... will have to delete this..
                int offset = 1;
                if (query.startingOffset() != -1) {
                    offset = query.startingOffset();
                }

                if(SearchMode.ONLY_USER_CONTENTS.ordinal() == query.searchMode()) {
                    String[] authors =  {ConnectedUserResource.instance().getUser(HttpRequestHelper.getSecurityToken(request)).id() + ""};
                    query.authors(authors);
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
