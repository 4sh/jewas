package fr.fsh.bbeeg.domain.pojos;

import java.util.Collection;

/**
 * @author driccio
 */
public class Domain {
    private Long id;
    private Long parentRef;
    private int level;
    private String label;
    private Collection<Domain> children;
    
    public Domain id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }

    public Domain parentRef(Long _parentRef){
        this.parentRef = _parentRef;
        return this;
    }

    public Long parentRef(){
        return this.parentRef;
    }

    public Domain level(int _level){
        this.level = _level;
        return this;
    }

    public int level(){
        return this.level;
    }

    public Domain label(String _label){
        this.label = _label;
        return this;
    }

    public String label(){
        return this.label;
    }

    public Domain children(Collection<Domain> _children){
        this.children = _children;
        return this;
    }

    public Collection<Domain> children(){
        return this.children;
    }
}
