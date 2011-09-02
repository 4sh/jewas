package fr.fsh.bbeeg.common.resources;

/**
 * @author driccio
 */
public class SuccessObject {
    private Boolean success;

    public SuccessObject success(Boolean _success){
        this.success = _success;
        return this;
    }

    public Boolean success(){
        return this.success;
    }
}
