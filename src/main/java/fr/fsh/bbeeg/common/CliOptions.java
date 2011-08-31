package fr.fsh.bbeeg.common;

import com.beust.jcommander.Parameter;

public class CliOptions {
    @Parameter(names = "-httpPort", description = "Http port used by Netty")
    private int httpPort = 8086;

    @Parameter(names = "-visioRootUrl", description = "Root URL of the EEG visualizer", required = true)
    private String visioRootUrl;

    @Parameter(names = "-contentFileRepository", description = "Repository of content files", required = true)
    private String contentFileRepository;

    @Parameter(names = "-h2ServerPort", description = "Port of the h2 server", required = true)
    private String h2ServerPort;

    @Parameter(names = "-elasticSearchAdress", description = "Ip adress of the elasticSearch server", required = true)
    private String elasticSearchAdress;

    @Parameter(names = "-elasticSearchPort", description = "Port of the elasticSearch server", required = true)
    private int elasticSearchPort;

    public int httpPort() {
        return this.httpPort;
    }

    public String visioRootUrl(){
        return this.visioRootUrl;
    }

    public String contentFileRepository() {
        return this.contentFileRepository;
    }

    public String h2ServerPort() {
        return this.h2ServerPort;
    }

    public String elasticSearchAdress() {
        return this.elasticSearchAdress;
    }
    
    public int elasticSearchPort() {
        return this.elasticSearchPort;
    }
}