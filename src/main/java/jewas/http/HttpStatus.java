package jewas.http;

public enum HttpStatus {
	OK(200), NOT_FOUND(404), SEE_OTHER(303);
	
	private int code;

	private HttpStatus(int code) {
		this.code = code;
	}
	
	public int code() {
		return code;
	}
}
