package jewas.http;

import java.util.*;

import jewas.lang.Strings;

public class Parameters {
	private final /* @Immutable */ Map<String, /* @Immutable */ List<String>> params;

	public Parameters(Map<String, List<String>> params) {
		this.params = Collections.unmodifiableMap(params);
	}

	public Map<String, List<String>> asMap() {
		return params;
	}
	
	public Parameters union(Parameters others) {
		Map<String, List<String>> p = new LinkedHashMap<String, List<String>>(params);
		p.putAll(others.asMap());
		return new Parameters(p);
	}

    public boolean nullVal(String name){
        return val(name) == Strings.NULL;
    }

	public String val(String name) {
		List<String> vals = params.get(name);
		if (vals == null || vals.isEmpty()) {
			return Strings.NULL;
		} else {
			return vals.get(0);
		}
	}
	
	public List<String> vals(String name) {
		List<String> vals = params.get(name);
		if (vals == null) {
			return Collections.emptyList();
		} else {
			return vals;
		}
	}

    public Set<String> names(){
        return params.keySet();
    }
}
