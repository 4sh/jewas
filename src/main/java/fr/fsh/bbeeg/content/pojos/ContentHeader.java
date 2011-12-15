package fr.fsh.bbeeg.content.pojos;

import fr.fsh.bbeeg.domain.pojos.Domain;
import fr.fsh.bbeeg.user.pojos.User;

import java.util.Date;
import java.util.List;

/**
 * @author fcamblor
 */
public class ContentHeader {
    private Long id;
    private String title;
    private Date creationDate;
    private Date lastModificationDate;
    private Date startPublicationDate;
    private Date endPublicationDate;
    private ContentStatus status;
    private String description;
    private Long popularity;
    //private Integer version;
    private Long ancestorId;

    private ContentType type;
    private User author;
    private List<Domain> domains;
    private List<String> tags;

    // TODO: add index criteria

    public ContentHeader title(String _title){
        this.title = _title;
        return this;
    }

    public String title(){
        return this.title;
    }

    public ContentHeader author(User _author){
        this.author = _author;
        return this;
    }

    public User author(){
        return this.author;
    }
    
    public ContentHeader id(Long _id){
        this.id = _id;
        return this;
    }
    
    public Long id(){
        return this.id;
    }

    public ContentHeader creationDate(Date _creationDate){
        this.creationDate = _creationDate;
        return this;
    }

    public Date creationDate(){
        return this.creationDate;
    }

    public ContentHeader lastModificationDate(Date _lastModificationDate){
        this.lastModificationDate = _lastModificationDate;
        return this;
    }

    public Date lastModificationDate(){
        return this.lastModificationDate;
    }

    public ContentHeader startPublicationDate(Date _startPublicationDate) {
        this.startPublicationDate = _startPublicationDate;
        return this;
    }

    public Date startPublicationDate() {
        return this.startPublicationDate;
    }

    public ContentHeader endPublicationDate(Date _endPublicationDate) {
        this.endPublicationDate = _endPublicationDate;
        return this;
    }

    public Date endPublicationDate() {
        return this.endPublicationDate;
    }

    public ContentHeader status(ContentStatus _status){
        this.status = _status;
        return this;
    }

    public ContentStatus status(){
        return this.status;
    }

    public ContentHeader description(String _description){
        this.description = _description;
        return this;
    }

    public String description(){
        return this.description;
    }


    public ContentHeader type(ContentType _type){
        this.type = _type;
        return this;
    }

    public ContentType type(){
        return this.type;
    }

    public ContentHeader domains(List<Domain> _domains){
        this.domains = _domains;
        return this;
    }

    public List<Domain> domains(){
        return this.domains;
    }

    public ContentHeader tags(List<String> _tags){
        this.tags = _tags;
        return this;
    }

    public List<String> tags(){
        return this.tags;
    }
//    public ContentHeader version(Integer _version){
//        this.version = _version;
//        return this;
//    }
//
//    public Integer version(){
//        return this.version;
//    }

     public ContentHeader popularity(Long _popularity){
        this.popularity = _popularity;
        return this;
    }

    public Long popularity(){
        return this.popularity;
    }

    public ContentHeader ancestorId(Long _ancestorId){
        this.ancestorId = _ancestorId;
        return this;
    }

    public Long ancestorId(){
        return this.ancestorId;
    }
}
