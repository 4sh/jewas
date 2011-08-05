package fr.fsh.bbeeg.common.config;

import fr.fsh.bbeeg.common.CliOptions;
import jewas.util.file.Files;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author fcamblor
 */
public class BBEEGConfiguration {
    public static final BBEEGConfiguration INSTANCE = new BBEEGConfiguration();

    private Manifest manifest = null;
    private CliOptions cliOptions = null;


    private BBEEGConfiguration(){
        try {
            manifest = new Manifest(Files.getInputStreamFromPath(BBEEGConfiguration.class, "META-INF/MANIFEST.MF"));
        } catch (IOException e) {
            throw new IllegalStateException("Error while reading BBEEG Manifest file : "+e.getMessage(), e);
        }
    }

    public BBEEGConfiguration cliOptions(CliOptions _cliOptions){
        if(this.cliOptions() != null){
            throw new IllegalStateException("Can't affect cliOptions twice to BBEEGConfiguration !");
        }
        this.cliOptions = _cliOptions;
        return this;
    }

    public CliOptions cliOptions(){
        return this.cliOptions;
    }

    public String appVersion(){
        return manifest.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION);
    }
}
