package fr.fsh.bbeeg.content.pojos;

/**
 * @author driccio
 */
public class Video {
    private String fileName;
    private String start;
    private String stop;

    public Video fileName(String _fileName){
        this.fileName = _fileName;
        return this;
    }

    public String fileName(){
        return this.fileName;
    }

    public Video start(String _start){
        this.start = _start;
        return this;
    }

    public String start(){
        return this.start;
    }

    public Video stop(String _stop){
        this.stop = _stop;
        return this;
    }

    public String stop(){
        return this.stop;
    }
}
