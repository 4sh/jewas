package fr.fsh.bbeeg.common.config;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class BBEEGConfigurationTest {
    @Test
    public void shouldAppVersionBeExtractedFromManifest(){
        assertThat(BBEEGConfiguration.INSTANCE.appVersion(), is(equalTo("[version-dev]")));
    }
}
