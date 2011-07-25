package jewas.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternUriPathMatcher implements UriPathMatcher {
	private final static Pattern GROUP_PATTERN = Pattern.compile("/\\[([^\\]]+)\\]");
	private final Pattern pattern;
	private final List<String> groupNames = new ArrayList<String>();
	
	public PatternUriPathMatcher(String pattern) {
		StringBuffer sb = new StringBuffer();
		Matcher m = GROUP_PATTERN.matcher(pattern);
        int counter = 1;

		while (m.find()) {
            if (m.groupCount() == counter) {
                m.appendReplacement(sb, "(?:/([\\\\w\\\\.\\\\_\\\\-\\\\d/]+))?");
            } else {
                m.appendReplacement(sb, "(?:/([\\\\w\\\\.\\\\_\\\\-\\\\d]+))?");
            }
            groupNames.add(m.group(1));
		}
		m.appendTail(sb);
		
		this.pattern = Pattern.compile(sb.toString());
	}
    
    @Override
    public Parameters match(String path) {
        Matcher m = pattern.matcher(path);
        if (m.matches()) {
            Map<String, List<String>> p = new LinkedHashMap<String, List<String>>();
            for (int i = 1; i <= m.groupCount(); i++) {
                /* A 'groupNames' item is not always linked to a corresponding value in the URI */
                if (m.group(i) != null) {
                    p.put(groupNames.get(i - 1), Arrays.asList(m.group(i)));
                }
            }
            return new Parameters(p);
        }
        return null;
    }

}
