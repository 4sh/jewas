package fr.fsh.bbeeg.domain.routes;

import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.domain.resources.DomainResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

import java.util.*;

/**
 * Route called by the site knowledge overview page. Stream the domains hierarchy in JSON.
 */
public class GetDomainsHierarchyRoute extends AbstractRoute {

    /**
     * Domain resource.
     */
    private DomainResource domainResource;

    /**
     * Constructor of the route.
     *
     * @param domainResource the domain resource.
     */
    public GetDomainsHierarchyRoute(DomainResource domainResource) {
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/domain/hierarchy"));
        this.domainResource = domainResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {

        return new AbstractRequestHandler() {
        
            private Set<Domain> visitedNodes = new HashSet<>();
            @Override
            public void onRequest(HttpRequest request) {
                List<Domain> results = new ArrayList<Domain>();
                domainResource.fetchAllDomainHierarchy(results);
                request.respondJson().object(serializeToJson(results));
            }

            /**
             * JSON serializer of a domain object to match the following format:
             * <code>{"id":"", "name": "", "children":[]}</code>
             *
             * @param domains the collection of domains to serialize
             * @return a JSON string
             */
            private String serializeToJson(Collection<Domain> domains) {
                if (domains == null || domains.isEmpty()) {
                    return "";
                }
                StringBuilder json = new StringBuilder("");
                Iterator<Domain> it = domains.iterator();
                while (it.hasNext()) {
                    Domain domainTreeNode = it.next();
                    if (visitedNodes.contains(domainTreeNode)) {
                        continue;
                    }
                    visitedNodes.add(domainTreeNode);

                    json.append("{\"id\":\"")
                            .append(domainTreeNode.id())
                            .append("\",")
                            .append("\"name\":\"")
                            .append(domainTreeNode.label())
                            .append("\",")
                            .append("\"children\":[")
                            .append(serializeToJson(domainTreeNode.children()))
                            .append("]")
                            .append("}");
                }
                return json.toString().replace("}{", "},{");
            }
        };
    }
}
