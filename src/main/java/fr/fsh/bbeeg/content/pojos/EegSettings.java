package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class EegSettings {
    private Integer eegStart;
    private Integer eegStop;
    private Integer zoom;
    private Integer frameDuration;
    private Montage[] montages;

    public EegSettings eegStart(Integer _eegStart){
        this.eegStart = _eegStart;
        return this;
    }

    public Integer eegStart(){
        return this.eegStart;
    }

    public EegSettings eegStop(Integer _eegStop){
        this.eegStop = _eegStop;
        return this;
    }

    public Integer eegStop(){
        return this.eegStop;
    }

    public EegSettings zoom(Integer _zoom){
        this.zoom = _zoom;
        return this;
    }

    public Integer zoom(){
        return this.zoom;
    }

    public EegSettings frameDuration(Integer _frameDuration){
        this.frameDuration = _frameDuration;
        return this;
    }

    public Integer frameDuration(){
        return this.frameDuration;
    }


    public EegSettings montages(Montage[] _montages){
        this.montages = _montages;
        return this;
    }

    public Montage[] montages(){
        return this.montages;
    }
}
