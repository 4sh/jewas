package fr.fsh.bbeeg.content.resources;

/**
 * @author driccio
 */
public class ContentType {
    private Long id;
    private String title;

    public ContentType id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }

    public ContentType title(String _title){
        this.title = _title;
        return this;
    }

    public String title(){
        return this.title;
    }
}
