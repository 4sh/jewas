package fr.fsh.bbeeg.common;

import com.beust.jcommander.Parameter;

public class CliOptions {
    @Parameter(names = "-httpPort", description = "Http port used by Netty")
    private int httpPort = 8086;

    @Parameter(names = "-visioRootUrl", description = "Root URL of the EEG visualizer", required = true)
    private String visioRootUrl;

    public int httpPort() {
        return this.httpPort;
    }

    public String visioRootUrl(){
        return this.visioRootUrl;
    }
}