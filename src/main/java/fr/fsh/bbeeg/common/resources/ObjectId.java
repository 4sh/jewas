package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class ObjectId {
    private Integer id;

    public ObjectId id(Integer _id){
        this.id = _id;
        return this;
    }

    public Integer id(){
        return this.id;
    }
}
