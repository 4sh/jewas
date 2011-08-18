package fr.fsh.bbeeg.domain.pojos;

/**
 * @author driccio
 */
public class Domain {
    private Long id;
    private String label;

    public Domain id(Long _id){
        this.id = _id;
        return this;
    }

    public Long id(){
        return this.id;
    }

    public Domain label(String _label){
        this.label = _label;
        return this;
    }

    public String label(){
        return this.label;
    }
}
