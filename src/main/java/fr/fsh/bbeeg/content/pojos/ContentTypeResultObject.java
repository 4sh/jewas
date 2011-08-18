package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class ContentTypeResultObject {
    private Long id;
    private String title;

    public ContentTypeResultObject id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }

    public ContentTypeResultObject title(String _title){
        this.title = _title;
        return this;
    }

    public String title(){
        return this.title;
    }
}
