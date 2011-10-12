package fr.fsh.bbeeg.security.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.security.pojos.Security;
import fr.fsh.bbeeg.security.resources.ConnectionInformation;
import fr.fsh.bbeeg.security.resources.ConnectionResultObject;
import fr.fsh.bbeeg.security.resources.SecurityResource;
import jewas.http.*;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class PostConnectionRoute extends AbstractRoute {

    private SecurityResource securityResource;

    public PostConnectionRoute(SecurityResource _securityResource){
        // TODO: use POST
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/connection/[login]/[password]"));
        this.securityResource = _securityResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ConnectionInformation infos = toQueryObject(parameters, ConnectionInformation.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Security security = securityResource.connectUser(infos);

                if (security != null) {
                    ConnectionResultObject<ConnectionResultObject.SuccessObject> resultObject =
                            new ConnectionResultObject<ConnectionResultObject.SuccessObject>();

                    resultObject.status(ConnectionResultObject.ConnectionStatus.SUCCESS)
                            .object(new ConnectionResultObject.SuccessObject().url("/dashboard/dashboard.html"));

                    JsonResponse jsonResponse = request.respondJson();
                    jsonResponse.addHeader(HttpHeaders.SET_COOKIE, "login=" + security.login() + ":expires=Wed, 12 Oct 2011 20:00");
                    jsonResponse.object(resultObject,
                            new TypeToken<ConnectionResultObject<ConnectionResultObject.SuccessObject>>() {
                            }.getType());

                } else {
                    ConnectionResultObject<ConnectionResultObject.FailureObject> resultObject =
                            new ConnectionResultObject<ConnectionResultObject.FailureObject>();

                    resultObject.status(ConnectionResultObject.ConnectionStatus.FAILURE)
                            .object(new ConnectionResultObject.FailureObject().msg("Erreur d'authentification. Vérifier votre identifiant ou votre mot de passe."));

                    request.respondJson().object(resultObject,
                            new TypeToken<ConnectionResultObject<ConnectionResultObject.FailureObject>>(){}.getType());
                }
                //request.respondHtml().content(Templates.process("dashboard/dashboard.ftl", null));
//                request.redirect().location(page);
            }
        };
    }


}
