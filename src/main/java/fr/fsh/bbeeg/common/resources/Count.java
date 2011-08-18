package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class Count {
    private Integer count;

    public Count count(Integer _count){
        this.count = _count;
        return this;
    }

    public Integer count(){
        return this.count;
    }
}
