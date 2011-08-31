package fr.fsh.bbeeg.security.routes;

import com.google.gson.reflect.TypeToken;
import fr.fsh.bbeeg.security.resources.ConnectionInformation;
import fr.fsh.bbeeg.security.resources.ConnectionResultObject;
import fr.fsh.bbeeg.security.resources.SecurityResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;

/**
 * @author driccio
 */
public class PostConnectionRoute extends AbstractRoute {
    public PostConnectionRoute(){
        // TODO: use POST
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/connection/[login]/[password]"));
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ConnectionInformation infos = toQueryObject(parameters, ConnectionInformation.class);

        return new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                Boolean connected = SecurityResource.connectUser(infos);

                if (connected) {
                    ConnectionResultObject<ConnectionResultObject.SuccessObject> resultObject =
                            new ConnectionResultObject<ConnectionResultObject.SuccessObject>();

                    resultObject.status(ConnectionResultObject.ConnectionStatus.SUCCESS)
                            .object(new ConnectionResultObject.SuccessObject().url("/dashboard/dashboard.html"));

                    request.respondJson().object(resultObject,
                            new TypeToken<ConnectionResultObject<ConnectionResultObject.SuccessObject>>(){}.getType());
                } else {
                    ConnectionResultObject<ConnectionResultObject.FailureObject> resultObject =
                            new ConnectionResultObject<ConnectionResultObject.FailureObject>();

                    resultObject.status(ConnectionResultObject.ConnectionStatus.FAILURE)
                            .object(new ConnectionResultObject.FailureObject().msg("Erreur d'authentification. Vérifier votre identifiant ou votre mot de passe."));

                    request.respondJson().object(resultObject,
                            new TypeToken<ConnectionResultObject<ConnectionResultObject.FailureObject>>(){}.getType());
                }

                //request.respondHtml().content(Templates.process(page, null));
                //request.redirect().location(page);
            }
        };
    }
}
