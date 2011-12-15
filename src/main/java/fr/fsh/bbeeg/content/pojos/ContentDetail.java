package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class ContentDetail {
    private ContentHeader header;

    private String rejectionComments;
    private String publicationComments;

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

   public ContentDetail publicationComments(String _publicationComments){
       this.publicationComments = _publicationComments;
       return this;
   }

   public String publicationComments(){
       return this.publicationComments;
   }

   public ContentDetail rejectionComments(String _rejectionComments){
       this.rejectionComments = _rejectionComments;
       return this;
   }

   public String rejectionComments(){
       return this.rejectionComments;
   }
}
