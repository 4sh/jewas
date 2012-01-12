package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import jewas.http.AbstractRoute;
import jewas.http.HttpMethodMatcher;
import jewas.http.HttpRequest;
import jewas.http.Parameters;
import jewas.http.PatternUriPathMatcher;
import jewas.http.RequestHandler;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author driccio
 */
public class SaveEegRoute extends AbstractRoute {

    /**
     * Class logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SaveEegRoute.class);


    public SaveEegRoute(){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/eeg/[tmpEegId]/[eegId]"));
    }

    public static class QueryObject {
        private Integer tmpEegId;
        private Integer eegId;

        public QueryObject tmpEegId(Integer _tmpEegId){
            this.tmpEegId = _tmpEegId;
            return this;
        }

        public Integer tmpEegId(){
            return this.tmpEegId;
        }

        public QueryObject eegId(Integer _eegId){
            this.eegId = _eegId;
            return this;
        }

        public Integer eegId(){
            return this.eegId;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryObject queryObject = toQueryObject(parameters, QueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                StringBuilder content = new StringBuilder();

                // Proxy: get the result from BBEEG server.
                try {
                    URL url = new URL(BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() + "/eeg/" + queryObject.tmpEegId() + "/" + queryObject.eegId());

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    try (BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())))
                    {
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            content.append(inputLine);
                        }
                        in.close();
                    }
                } catch (MalformedURLException e) {
                    logger.error("Cannot open connection", e);
                } catch (IOException e) {
                    logger.error("Cannot open connection", e);
                }
                request.respondHtml().content(content.toString());
            }
        };
    }
}