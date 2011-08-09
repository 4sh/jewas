package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class ObjectId {
    private Long id;

    public ObjectId id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }
}
