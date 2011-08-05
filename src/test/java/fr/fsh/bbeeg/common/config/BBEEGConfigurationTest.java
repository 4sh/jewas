package fr.fsh.bbeeg.common.config;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author fcamblor
 */
public class BBEEGConfigurationTest {
    @Test
    public void shouldAppVersionBeExtractedFromManifest(){
        assertThat(BBEEGConfiguration.INSTANCE.appVersion(), is(equalTo("version-dev")));
    }
}
