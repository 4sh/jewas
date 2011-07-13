package jewas.json;

import jewas.lang.Objects;

public class Json {
	public static String toJsonString(Object jsonRepresentation) {
		if (Objects.isNull(jsonRepresentation)) {
			return "{}";
		} else {
			// TODO: handle this properly
			return jsonRepresentation.toString();
			
//			Properties<?> props = Properties.properties(jsonRepresentation.getClass());
//			for (Property p : props.asList()) {
//				
//			}
		}
	}
}
