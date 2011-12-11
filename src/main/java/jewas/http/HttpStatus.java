package jewas.http;

public enum HttpStatus {
	OK(200), PARTIAL_CONTENT(206), NOT_FOUND(404), SEE_OTHER(303), REQUESTED_RANGE_NOT_SATISFIABLE(416);
	
	private int code;

	private HttpStatus(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
}
