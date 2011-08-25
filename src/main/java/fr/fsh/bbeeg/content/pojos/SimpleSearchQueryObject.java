package fr.fsh.bbeeg.content.pojos;

import java.util.Date;

public class SimpleSearchQueryObject {
    private String query;
    private Date serverTimestamp;
    private Integer startingOffset = -1;
    private Integer numberOfContents = Integer.valueOf(10);
    private Integer searchMode;

    public SimpleSearchQueryObject query(String _query) {
        this.query = _query;
        return this;
    }

    public String query() {
        return this.query;
    }

    public SimpleSearchQueryObject startingOffset(Integer _startingOffset) {
        this.startingOffset = _startingOffset;
        return this;
    }

    public Integer startingOffset() {
        if (startingOffset < 1) {
            return 1;
        }
        
        return this.startingOffset;
    }

    public SimpleSearchQueryObject numberOfContents(Integer _numberOfContents){
        this.numberOfContents = _numberOfContents;
        return this;
    }

    public Integer numberOfContents(){
        return this.numberOfContents;
    }

   public SimpleSearchQueryObject serverTimestamp(Date _serverTimestamp){
       this.serverTimestamp = _serverTimestamp;
       return this;
   }

   public Date serverTimestamp(){
       return this.serverTimestamp;
   }

    public SimpleSearchQueryObject searchMode(Integer _searchMode){
        this.searchMode = _searchMode;
        return this;
    }

    public Integer searchMode(){
        if (searchMode == null || searchMode < 0) {
            return 0;
        }
        return this.searchMode;
    }
}