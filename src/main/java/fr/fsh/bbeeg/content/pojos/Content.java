package fr.fsh.bbeeg.content.pojos;

/**
 * @author fcamblor
 */
public class Content {

    private Long id;
    private String title;
    // TODO: refactor this with a User entity ???
    private String author;

    public Content title(String _title){
        this.title = _title;
        return this;
    }

    public String title(){
        return this.title;
    }

    public Content author(String _author){
        this.author = _author;
        return this;
    }

    public String author(){
        return this.author;
    }
    
    public Content id(Long _id){
        this.id = _id;
        return this;
    }
    
    public Long id(){
        return this.id;
    }
}
