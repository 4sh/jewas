package fr.fsh.bbeeg.content.pojos;

import java.util.Date;

public class ContentStatusQueryObject {
    private String newStatus;
    private String comments;
    private Date startPublicationDate;
    private Date endPublicationDate;

    public ContentStatusQueryObject newStatus(String _newStatus) {
        this.newStatus = _newStatus;
        return this;
    }

    public String newStatus() {
        return this.newStatus;
    }

    public ContentStatusQueryObject comments(String _comments) {
        this.comments = _comments;
        return this;
    }

    public String comments() {
        return this.comments;
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