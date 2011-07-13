package jewas.http;

public enum HttpMethodMatcher {
	ALL {
		@Override
		public boolean match(HttpMethod method) {
			return true;
		}
	},
	GET {
		@Override
		public boolean match(HttpMethod method) {
			return method == HttpMethod.GET;
		}
	},
	POST {
		@Override
		public boolean match(HttpMethod method) {
			return method == HttpMethod.POST;
		}
	},
	PUT {
		@Override
		public boolean match(HttpMethod method) {
			return method == HttpMethod.PUT;
		}
	},
	DELETE {
		@Override
		public boolean match(HttpMethod method) {
			return method == HttpMethod.DELETE;
		}
	}
	;

	public abstract boolean match(HttpMethod method);

}
