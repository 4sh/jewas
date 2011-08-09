package fr.fsh.bbeeg.content.pojos;

/**
 * @author fcamblor
 */
public class TextContent extends Content {
    private String text;

    public TextContent text(String _text){
        this.text = _text;
        return this;
    }

    public String text(){
        return this.text;
    }
}
