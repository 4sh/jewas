package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class MontageOperation {
    private String s1;
    private String operator;
    private String s2;

    public MontageOperation s1(String _s1){
        this.s1 = _s1;
        return this;
    }
     
    public String s1(){
        return this.s1;
    }

    public MontageOperation operator(String _operator){
        this.operator = _operator;
        return this;
    }

    public String operator(){
        return this.operator;
    }

    public MontageOperation s2(String _s2){
        this.s2 = _s2;
        return this;
    }

    public String s2(){
        return this.s2;
    }
}
