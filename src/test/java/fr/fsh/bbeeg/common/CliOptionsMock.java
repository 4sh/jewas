package fr.fsh.bbeeg.common;

public class CliOptionsMock extends CliOptions {

    private String contentFileRepository;

    private String tmpContentFileRepository;

    public CliOptionsMock contentFileRepository(String _contentFileRepository){
        this.contentFileRepository = _contentFileRepository;
        return this;
    }

    public String contentFileRepository(){
        return this.contentFileRepository;
    }

    public CliOptionsMock tmpContentFileRepository(String _tmpContentFileRepository){
        this.tmpContentFileRepository = _tmpContentFileRepository;
        return this;
    }

    public String tmpContentFileRepository(){
        return this.tmpContentFileRepository;
    }

}
