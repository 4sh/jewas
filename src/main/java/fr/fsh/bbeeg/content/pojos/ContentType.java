package fr.fsh.bbeeg.content.pojos;

/**
 * @author carmarolli
 */
public enum ContentType {

    /** The 'text' content type **/
    TEXT("contentType.text"),
    /** The 'image' content type for JPG files **/
    IMAGE("contentType.image"),
    /** The 'video' content type for MP4 files **/
    VIDEO("contentType.video"),
    /** The 'audio' content type for the MP3 files **/
    AUDIO("contentType.audio"),
    /** The 'document' content type for the PDF files **/
    DOCUMENT("contentType.document"),
    /** The 'eeg' content type for edf signals files **/
    EEG("contentType.eeg");

    /**
     * The i18n key which, combined with the locale,
     * creates a unique identifier of the content type name translation.
     */
    private final String i18nKey;

    /**
     * Default constructor for Content Type enum object.
     *
     * @param _i18nKey the internationalization key
     */
    private ContentType(String _i18nKey) {
        this.i18nKey = _i18nKey;
    }

    /**
     * @return the i18nKey
     */
    public String i18nKey() {
        return i18nKey;
    }

}
