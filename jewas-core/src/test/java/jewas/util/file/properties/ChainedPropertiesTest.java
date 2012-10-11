package jewas.util.file.properties;

import jewas.util.properties.ChainedProperties;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author fcamblor
 */
public class ChainedPropertiesTest {

    @Test
    public void shouldPropertiesOverloadingWorksCorrectly() {
        prepareEnvProperties("user.name", "fcamblor", "env", "dev");

        ChainedProperties chainedProps = new ChainedProperties()
                .chainProperties("jewas/util/properties/app.properties", "global", true)
                .chainProperties("jewas/util/properties/envspecific/app-${env}.properties", "env", true)
                .chainProperties("jewas/util/properties/devspecific/app-${user.name}.properties", "dev", true);

        Properties props = chainedProps.load();
        assertThat(props.getProperty("foo"), is(equalTo("barUser")));
    }

    @Test
    public void shouldPropertiesWithDollarBeHandledCorrectly() {
        prepareEnvProperties("user.name", "blah$", "env", "dev");

        ChainedProperties chainedProps = new ChainedProperties()
                .chainProperties("jewas/util/properties/app.properties", "global", true)
                .chainProperties("jewas/util/properties/envspecific/app-${env}.properties", "env", true)
                .chainProperties("jewas/util/properties/devspecific/app-${user.name}.properties", "dev", true);

        Properties props = chainedProps.load();
        assertThat(props.getProperty("foo"), is(equalTo("barBlah$")));
    }

    private static void prepareEnvProperties(String... keyValues) {
        for (int i = 0; i < keyValues.length; i += 2) {
            System.setProperty(keyValues[i], keyValues[i + 1]);
        }
    }
}
