package jewas.template;


import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jewas.configuration.JewasConfiguration;
import jewas.util.file.Files;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class Templates {
    /**
     * The FreeMarker configuration.
     */
    private static Configuration cfg;

    /**
     * The template folder path.
     */
    protected static String templatesPath;

    /**
     * Init the configuration of FreeMarker.
     */
    private static void initConfiguration() {
        if (templatesPath == null) {
            templatesPath = JewasConfiguration.getTemplatesPath();
        }
        
        cfg = new Configuration();

        try {
            cfg.setDirectoryForTemplateLoading(Files.getFileFromPath(templatesPath));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        cfg.setObjectWrapper(new DefaultObjectWrapper());
    }

    /**
     * Process the template with the given parameters.
     * @param templateName the name of the template to use
     * @param params the {@link Map} of objects to use as parameters
     * @return
     */
    public static String process(String templateName, Map<String, Object> params) {
        if (cfg == null) {
            initConfiguration();
        }

        Template temp = null;

        try {
            temp = cfg.getTemplate(templateName);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        if (temp == null) {
            return "";
        }

        Writer writer = new StringWriter();

        try {
            temp.process(params, writer);
        } catch (TemplateException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

        return writer.toString();
    }
}
