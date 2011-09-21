package fr.fsh.bbeeg.content.pojos;

import jewas.collection.TypedArrayList;
import jewas.collection.TypedList;

import java.util.Date;

/**
 * @author driccio
 */
public class AdvancedSearchQueryObject extends SimpleSearchQueryObject {
    private Date from;
    private Date to;
    private TypedList<String> searchTypes = new TypedArrayList<String>(String.class);;
    //private String criterias;
    private TypedList<String> domains = new TypedArrayList<String>(String.class);
    private TypedList<String> authors = new TypedArrayList<String>(String.class);

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

    public AdvancedSearchQueryObject searchTypes(TypedList<String> _searchTypes){
        this.searchTypes = _searchTypes;
        return this;
    }

    public TypedList<String> searchTypes(){
        return this.searchTypes;
    }

   /* public AdvancedSearchQueryObject criterias(String _criterias){
        this.criterias = _criterias;
        return this;
    }

    public String criterias(){
        return this.criterias;
    } */

    public AdvancedSearchQueryObject domains(TypedList<String> _domains){
        this.domains = _domains;
        return this;
    }

    public TypedList<String> domains(){
        return this.domains;
    }

    public AdvancedSearchQueryObject authors(TypedList<String> _authors){
        this.authors = _authors;
        return this;
    }

    public TypedList<String> authors(){
        return this.authors;
    }
}
