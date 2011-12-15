package fr.fsh.bbeeg.content.pojos;

import java.util.Date;

public class ContentStatusQueryObject {
    private String status;
    private String comment;
    private Date startPublicationDate;
    private Date endPublicationDate;

    public ContentStatusQueryObject status(String _status) {
        this.status = _status;
        return this;
    }

    public String status() {
        return this.status;
    }

    public ContentStatusQueryObject comment(String _comment) {
        this.comment = _comment;
        return this;
    }

    public String comment() {
        return this.comment;
    }

    public ContentStatusQueryObject startPublicationDate(Date _startPublicationDate) {
        this.startPublicationDate = _startPublicationDate;
        return this;
    }

    public Date startPublicationDate() {
        return this.startPublicationDate;
    }

    public ContentStatusQueryObject endPublicationDate(Date _endPublicationDate) {
        this.endPublicationDate = _endPublicationDate;
        return this;
    }

    public Date endPublicationDate() {
        return this.endPublicationDate;
    }
}