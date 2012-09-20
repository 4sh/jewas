package jewas.http;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PatternUriPathMatcher implements UriPathMatcher {
    private final static Pattern GROUP_PATTERN = Pattern.compile("/\\[([^\\]]+)\\]");
    private final Pattern pattern;
    private final List<String> groupNames = new ArrayList<String>();

    public PatternUriPathMatcher(String pattern) {
        StringBuffer sb = new StringBuffer();
        Matcher m = GROUP_PATTERN.matcher(pattern);

        while (m.find()) {
            if (m.end() == pattern.length()) {
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
