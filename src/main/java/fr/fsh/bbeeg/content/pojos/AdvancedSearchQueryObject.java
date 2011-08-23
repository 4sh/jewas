package fr.fsh.bbeeg.content.pojos;

import java.util.Date;

/**
 * @author driccio
 */
public class AdvancedSearchQueryObject {
    private Date from;
    private Date to;
    private String[] searchTypes;
    private String criterias;
    private String[] authors;
    private Integer startingOffset = -1;
    private Integer numberOfContents = Integer.valueOf(10);

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

    public AdvancedSearchQueryObject criterias(String _criterias){
        this.criterias = _criterias;
        return this;
    }

    public String criterias(){
        return this.criterias;
    }

    public AdvancedSearchQueryObject authors(String[] _authors){
        this.authors = _authors;
        return this;
    }

    public String[] authors(){
        return this.authors;
    }

    public AdvancedSearchQueryObject startingOffset(Integer _startingOffset){
        this.startingOffset = _startingOffset;
        return this;
    }

    public Integer startingOffset(){
        if (startingOffset < 1) {
            return 1;
        }

        return this.startingOffset;
    }

    public AdvancedSearchQueryObject numberOfContents(Integer _numberOfContents){
        this.numberOfContents = _numberOfContents;
        return this;
    }

    public Integer numberOfContents(){
        return this.numberOfContents;
    }
}
