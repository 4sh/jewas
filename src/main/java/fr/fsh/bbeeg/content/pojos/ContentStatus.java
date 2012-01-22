package fr.fsh.bbeeg.content.pojos;

/**
 * @author carmarolli
 */
public enum ContentStatus {

    /**
     * The content is being created by a teacher
     */
    DRAFT,
    /**
     * The teacher has made a request for publication concerning the content
     */
    TO_BE_VALIDATED,
    /**
     * The moderator accepted the request for publication
     */
    VALIDATED,
    /**
     * The moderator rejected the request for publication
     */
    REJECTED,
    /**
     * The teacher owner of the content has made a request for deletion
     */
    TO_BE_DELETED,
    /**
     * The moderator has accepted the request for deletion
     */
    DELETED,
    /**
     * A new version of the content has been validated
     */
    ARCHIVED,
}
