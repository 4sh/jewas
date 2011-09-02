package fr.fsh.bbeeg.common.resources;

import jewas.http.data.FileUpload;

/**
 * @author driccio
 */
public class FileQueryObject {
    private FileUpload file;
    private String extension;

    public FileQueryObject file(FileUpload _file){
        this.file = _file;
        return this;
    }

    public FileUpload file(){
        return this.file;
    }
    
    public FileQueryObject extension(String _extension){
        this.extension = _extension;
        return this;
    }
     
    public String extension(){
        return this.extension;
    }
}
