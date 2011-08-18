package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class ContentDetail extends ContentHeader {
    private ContentHeader header;
    private String url;

    public ContentDetail header(ContentHeader _header){
        this.header = _header;
        return this;
    }

    public ContentHeader header(){
        return this.header;
    }

    public ContentDetail url(String _url){
        this.url = _url;
        return this;
    }
     
    public String url(){
        return this.url;
    }
}
