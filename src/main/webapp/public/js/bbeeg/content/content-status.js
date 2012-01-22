var ContentStatus = (function() {

    // The content is being created by a teacher.
    var DRAFT = "DRAFT";

    // The teacher has made a request for publication concerning the content.
    var TO_BE_VALIDATED = "TO_BE_VALIDATED";

    // The moderator accepted the request for publication.
    var VALIDATED = "VALIDATED";

    // The moderator rejected the request for publication.
    var REJECTED = "REJECTED";

    // The teacher owner of the content has made a request for deletion.
    var TO_BE_DELETED = "TO_BE_DELETED";

    // The moderator has accepted the request for deletion
    var DELETED = "DELETED";

    // A new version of the content has been validated.
    var ARCHIVED = "ARCHIVED";

    return {
        DRAFT: DRAFT,
        TO_BE_VALIDATED: TO_BE_VALIDATED,
        VALIDATED: VALIDATED,
        REJECTED: REJECTED,
        TO_BE_DELETED: TO_BE_DELETED,
        DELETED: DELETED,
        ARCHIVED: ARCHIVED
    };
})();