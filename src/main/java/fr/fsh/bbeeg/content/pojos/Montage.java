package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class Montage {
    private String[] signalsToDisplay;
    private MontageOperation[] operations;

    public Montage signalsToDisplay(String[] _signalsToDisplay){
        this.signalsToDisplay = _signalsToDisplay;
        return this;
    }

    public String[] signalsToDisplay(){
        return this.signalsToDisplay;
    }

    public Montage operations(MontageOperation[] _operations){
        this.operations = _operations;
        return this;
    }

    public MontageOperation[] operations(){
        return this.operations;
    }
}
