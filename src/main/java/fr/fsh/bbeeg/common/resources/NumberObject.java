package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class NumberObject {
    private Integer number;

    public NumberObject number(Integer _number){
        this.number = _number;
        return this;
    }

    public Integer number(){
        return this.number;
    }
}
