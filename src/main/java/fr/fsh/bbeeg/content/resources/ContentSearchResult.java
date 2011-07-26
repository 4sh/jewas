package fr.fsh.bbeeg.content.resources;

import java.util.Date;

/**
 * @author fcamblor
 */
public class ContentSearchResult {

    private String id;
    private String title;
    // TODO: refactor this with a User entity ???
    private String author;
    public String domain;
    public String concept;
    private String levelCriteria;
    private Date creationDate;
    private Date lastModificationDate;
    // TODO: replace this with an enum type ???
    private String mediaType;

    public ContentSearchResult title(String _title) {
        this.title = _title;
        return this;
    }

    public String title() {
        return this.title;
    }

    public ContentSearchResult author(String _author) {
        this.author = _author;
        return this;
    }

    public String author() {
        return this.author;
    }

    public ContentSearchResult levelCriteria(String _levelCriteria) {
        this.levelCriteria = _levelCriteria;
        return this;
    }

    public String levelCriteria() {
        return this.levelCriteria;
    }

    public ContentSearchResult creationDate(Date _creationDate) {
        this.creationDate = _creationDate;
        return this;
    }

    public Date creationDate() {
        return this.creationDate;
    }

    public ContentSearchResult lastModificationDate(Date _lastModificationDate) {
        this.lastModificationDate = _lastModificationDate;
        return this;
    }

    public Date lastModificationDate() {
        return this.lastModificationDate;
    }

    public ContentSearchResult mediaType(String _mediaType) {
        this.mediaType = _mediaType;
        return this;
    }

    public String mediaType() {
        return this.mediaType;
    }

    public ContentSearchResult domain(String _domain) {
        this.domain = _domain;
        return this;
    }

    public String domain() {
        return this.domain;
    }

    public ContentSearchResult concept(String _concept) {
        this.concept = _concept;
        return this;
    }

    public String concept() {
        return this.concept;
    }

    public ContentSearchResult id(String _id) {
        this.id = _id;
        return this;
    }

    public String id() {
        return this.id;
    }
}
