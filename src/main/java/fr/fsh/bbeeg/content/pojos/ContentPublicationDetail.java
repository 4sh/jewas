package fr.fsh.bbeeg.content.pojos;

import java.util.Date;

/**
 * @author carmarolli
 */
public class ContentPublicationDetail {

    /**
     * The publication start date.
     */
    private Date start;

    /**
     * The publication end date.
     */
    private Date end;

    /**
     * The publication comments
     */
    private String comments;

    /**
     * Constructor for {@ContentPublicationDetail}.
     *
     * @param start    the publication start date
     * @param end      the publication end date
     * @param comments the publication comments
     */
    public ContentPublicationDetail(Date start, Date end, String comments) {
        this.start = start;
        this.end = end;
        this.comments = comments;
    }

    public ContentPublicationDetail start(Date _start) {
        this.start = _start;
        return this;
    }

    public Date start() {
        return this.start;
    }

    public ContentPublicationDetail end(Date _end) {
        this.end = _end;
        return this;
    }

    public Date end() {
        return this.end;
    }

    public ContentPublicationDetail comments(String _comments) {
        this.comments = _comments;
        return this;
    }

    public String comments() {
        return this.comments;
    }
}
