package fr.fsh.bbeeg.content.routes;

import fr.fsh.bbeeg.common.config.BBEEGConfiguration;
import fr.fsh.bbeeg.common.resources.SuccessObject;
import fr.fsh.bbeeg.content.resources.EegResource;
import jewas.http.*;
import jewas.http.data.BodyParameters;
import jewas.http.impl.AbstractRequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author driccio
 */
public class CreateEegSettingsRoute extends AbstractRoute {

    private EegResource eegResource;

    public CreateEegSettingsRoute(EegResource _eegResource){
        super(HttpMethodMatcher.POST_OR_PUT, new PatternUriPathMatcher("/content/eeg/settings/[id]/[mode]"));
        this.eegResource = _eegResource;
    }

    public static class QueryObject {
        private Long id;
        private String mode;

        public QueryObject id(Long _id){
            this.id = _id;
            return this;
        }

        public Long id(){
            return this.id;
        }

        public QueryObject mode(String _mode){
            this.mode = _mode;
            return this;
        }

        public String mode(){
            return this.mode;
        }
    }

    public static class TextQueryObject {
        private String text;

        public TextQueryObject text(String _text){
            this.text = _text;
            return this;
        }

        public String text(){
            return this.text;
        }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryObject qo = toQueryObject(parameters, QueryObject.class);
        final TextQueryObject tqo = toQueryObject(parameters, TextQueryObject.class);

        return new AbstractRequestHandler() {
            @Override
            public void onReady(HttpRequest request, BodyParameters bodyParameters) {
                super.onReady(request, bodyParameters);

                // Used as proxy for Visio SaveEegSettingsFileRoute: /content/eeg/settings/[id]/[mode]
                try {
                    URL url = new URL(BBEEGConfiguration.INSTANCE.cliOptions().visioEegInternalUrl() +
                            "/content/eeg/settings/" + qo.id() + "/" + qo.mode() + "?text=" + URLEncoder.encode(tqo.text(), "UTF-8"));



                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setDoOutput(true);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null) {
                        System.out.println(line);

                    }
                    rd.close();

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                eegResource.updateEegDocumentUri(qo.id(), "");
                request.respondJson().object(new SuccessObject().success(true));
            }
        };
    }
}