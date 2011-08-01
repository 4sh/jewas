package fr.fsh.bbeeg.content.routes;

import com.google.gson.reflect.TypeToken;
import jewas.http.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fcamblor
 */
public class GetContentCriteriasRoute extends AbstractRoute {

    public GetContentCriteriasRoute(){
        super(HttpMethodMatcher.GET, new PatternUriPathMatcher("/content/criterias"));
    }

    public static class QueryObject{
        private Integer depth;
        private String parent;

        public QueryObject depth(Integer _depth){
            this.depth = _depth;
            return this;
        }

        public Integer depth(){
            return this.depth;
        }

        public QueryObject parent(String _parent){
            this.parent = _parent;
            return this;
        }

        public String parent(){
            return this.parent;
        }
    }

    public static class Option {
        public String label;
        public String value;
        public Option(String value, String label){ this.label = label; this.value = value; }
    }

    @Override
    protected RequestHandler onMatch(HttpRequest request, Parameters parameters) {
        final QueryObject qo = super.toQueryObject(parameters, QueryObject.class);
        return new RequestHandler() {
            @Override
            public void onRequest(HttpRequest request) {
                String prefix = "";
                for(int i=0; i<qo.depth(); i++){
                    prefix += "Ss-";
                }

                List<Option> options = new ArrayList<Option>();
                for(int i=0; i<5; i++){
                    String val = prefix+"Crit "+i;
                    options.add(new Option(val,"Lib "+val));
                }

                request.respondJson().object(options, new TypeToken<List<Option>>(){}.getType());
            }
        };
    }
}
