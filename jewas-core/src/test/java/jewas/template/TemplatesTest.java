package jewas.template;

import jewas.configuration.JewasConfigurationForTest;
import jewas.util.file.Files;
import jewas.util.properties.ChainedProperties;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 19/07/11
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class TemplatesTest {

    @Test
    @Ignore("Ignored : doesn't pass when executed through gradle")
    public void shouldProcessATemplateWithGivenParameters() throws IOException {
        JewasConfigurationForTest.override(
                new ChainedProperties()
                        .chainProperties("jewas/configuration/jewasForTemplate.conf", "jewas-test-global", true)
                        .load()
        );

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", "JewasUser");

        String expected = Files.getStringFromStream(
                Files.getInputStreamFromPath("jewas/template/testTemplateWithParamsExpected.txt"));

        String result = Templates.process("testTemplateWithParams.ftl", params);

        Assert.assertNotNull(result);
        Assert.assertEquals(expected, result);

        JewasConfigurationForTest.clean();

    }
}
