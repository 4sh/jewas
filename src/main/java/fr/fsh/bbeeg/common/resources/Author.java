package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class Author {
    private String name;

    public Author name(String _name){
        this.name = _name;
        return this;
    }

    public String name(){
        return this.name;
    }
}
