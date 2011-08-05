package fr.fsh.bbeeg.common;

import com.beust.jcommander.Parameter;

public class CliOptions {
    @Parameter(names = "-httpPort", description = "Http port used by Netty")
    private int httpPort = 8086;

    @Parameter(names = "-visioUrl", description = "URL allowing to access EEG visualizer", required = true)
    private String visioUrl;

    public int httpPort() {
        return this.httpPort;
    }

    public String visioUrl(){
        return this.visioUrl;
    }
}