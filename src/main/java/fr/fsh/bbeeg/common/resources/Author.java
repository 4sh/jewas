package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class Author {
    private Long id;
    private String name;

    public Author id(Long _id){
        this.id = _id;
        return this;
    }
     
    public Long id(){
        return this.id;
    }
    
    public Author name(String _name){
        this.name = _name;
        return this;
    }

    public String name(){
        return this.name;
    }
}
