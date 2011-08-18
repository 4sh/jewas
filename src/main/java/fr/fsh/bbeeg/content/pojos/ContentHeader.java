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
    private Boolean published;
    private String description;

    private ContentType type;
    private User author;
    private List<Domain> domains;

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

    public ContentHeader published(Boolean _published){
        this.published = _published;
        return this;
    }

    public Boolean published(){
        return this.published;
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
}
