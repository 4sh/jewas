package jewas.http;

import java.util.HashMap;
import java.util.Map;

public enum ContentType {

    APP_JS("application/javascript", "js"),
    APP_OGG("application/ogg", "ogg"),
    APP_PDF("application/pdf", "pdf"),
    APP_JSON("application/json", "json"),
    APP_XML("application/xml", "xml"),
    APP_ZIP("application/zip", "zip"),

    AUD_MPEG("audio/mpeg", "mp3"),
    AUD_WMA("audio/x-ms-wma", "wma"),
    AUD_WAV("audio/x-wav", "wav"),

    IMG_GIF("image/gif", "gif"),
    IMG_JPG("image/jpeg", "jpg", "jpeg"),
    IMG_PNG("image/png", "png"),
    IMG_TIFF("image/tiff", "tiff"),
    IMG_ICO("image/vnd.microsoft.icon", "ico"),
    IMG_SVG("image/svg+xml", "svg"),

    TXT_CSS("text/css", "css"),
    TXT_CSV("text/csv", "csv"),
    TXT_HTML("text/html", "html"),
    TXT_PLAIN("text/plain", "txt"),

    VID_MPG("video/mpeg", "mpg", "mpeg"),
    VID_MP4("video/mp4", "mp4"),
    VID_MOV("video/quicktime", "mov"),
    VID_WMV("video/x-ms-mwv", "wmv"),
    VID_FLV("video/x-flv", "flv");

    private static final Map<String, ContentType> CONTENT_TYPES_BY_EXTENSION = new HashMap<String, ContentType>();
    static {
        for(ContentType ct : ContentType.values()){
            for(String ext : ct.possiblePathExtensions){
                CONTENT_TYPES_BY_EXTENSION.put(ext, ct);
            }
        }
    }


	private String contentTypeValue;
    private String[] possiblePathExtensions;

	ContentType(String contentTypeValue, String... possiblePathExtensions) {
        this.contentTypeValue = contentTypeValue;
        this.possiblePathExtensions = possiblePathExtensions;
	}

    public String value(){
        return contentTypeValue;
    }

    public static ContentType guessContentTypeByUri(String uri){
        String extension = uri.substring(uri.lastIndexOf(".")+1);
        if(CONTENT_TYPES_BY_EXTENSION.containsKey(extension)){
            return CONTENT_TYPES_BY_EXTENSION.get(extension);
        } else {
            return null;
        }
    }
	
	@Override
	public String toString() {
		return contentTypeValue;
	}
}
