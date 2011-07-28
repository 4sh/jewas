package fr.fsh.bbeeg.domain.resources;

import java.math.BigDecimal;

/**
 * @author driccio
 */
public class DomainSearchResult {
    private String text;
    private BigDecimal weight;
    private String url;

    public DomainSearchResult(String text, BigDecimal weight, String url) {
        this.text = text;
        this.url = url;
        this.weight = weight;
    }

    public DomainSearchResult text(String _text){
        this.text = _text;
        return this;
    }

    public String text(){
        return this.text;
    }

    public DomainSearchResult weight(BigDecimal _weight){
        this.weight = _weight;
        return this;
    }

    public BigDecimal weight(){
        return this.weight;
    }

    public DomainSearchResult url(String _url){
        this.url = _url;
        return this;
    }

    public String url(){
        return this.url;
    }
}
