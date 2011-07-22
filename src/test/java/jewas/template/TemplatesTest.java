package jewas.template;

import configuration.JewasConfigurationForTest;
import jewas.util.file.Files;
import junit.framework.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
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
    public void shouldProcessATemplateWithGivenParameters() throws FileNotFoundException {
        JewasConfigurationForTest.override("jewas/configuration/jewasForTemplate.conf");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user", "JewasUser");

        String expected = Files.getStringFromFile(
                Files.getFileFromPath("jewas/template/testTemplateWithParamsExpected.txt"));

        String result = Templates.process("testTemplateWithParams.ftl", params);

        Assert.assertNotNull(result);
        Assert.assertEquals(expected, result);

        JewasConfigurationForTest.clean();

    }
}
