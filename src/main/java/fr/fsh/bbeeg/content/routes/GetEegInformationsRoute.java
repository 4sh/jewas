package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.resources.ObjectId;
import fr.fsh.bbeeg.content.resources.EegResource;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.impl.AbstractRequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author driccio
 */
public class GetEegInformationsRoute extends AbstractRoute {
    private EegResource eegResource;

    public GetEegInformationsRoute(EegResource _eegResource){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/eeg/informations/[id]"));
        eegResource = _eegResource;
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final ObjectId oi = toQueryObject(parameters, ObjectId.class);

        return  new AbstractRequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                StringBuilder content = new StringBuilder();

                // Proxy: get the result from Visio server.
                try {
                    URL url = new URL(BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/visio/content/" + oi.id() + "/informations");

                    URLConnection urlConnection = url.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                                    urlConnection.getInputStream()));

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }

                    in.close();
                } catch (MalformedURLException e) {
                    System.out.println(e.getMessage());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                request.respondHtml().content(content.toString());
            }
        };
    }
}
