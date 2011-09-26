package fr.fsh.bbeeg.content.pojos;

import java.util.Date;

/**
 * @author driccio
 */
public class AdvancedSearchQueryObject extends SimpleSearchQueryObject {
    private Date from;
    private Date to;
    private String[] searchTypes;
    //private String criterias;
    private String[] domains;
    private String[] authors;

    public AdvancedSearchQueryObject from(Date _from){
        this.from = _from;
        return this;
    }

    public Date from(){
        return this.from;
    }

    public AdvancedSearchQueryObject to(Date _to){
        this.to = _to;
        return this;
    }

    public Date to(){
        return this.to;
    }

    public AdvancedSearchQueryObject searchTypes(String[] _searchTypes){
        this.searchTypes = _searchTypes;
        return this;
    }

    public String[] searchTypes(){
        return this.searchTypes;
    }

   /* public AdvancedSearchQueryObject criterias(String _criterias){
        this.criterias = _criterias;
        return this;
    }

    public String criterias(){
        return this.criterias;
    } */

    public AdvancedSearchQueryObject domains(String[] _domains){
        this.domains = _domains;
        return this;
    }

    public String[] domains(){
        return this.domains;
    }

    public AdvancedSearchQueryObject authors(String[] _authors){
        this.authors = _authors;
        return this;
    }

    public String[] authors(){
        return this.authors;
    }
}
