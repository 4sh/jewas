package jewas.template;

import freemarker.cache.URLTemplateLoader;

import java.net.URL;

/**
 * @author fcamblor
 * TemplateLoader which will take a classloader to load templates from
 * Resources will be searched into current classloader
 */
public class ClassLoaderTemplateLoader extends URLTemplateLoader {

    private ClassLoader classloader;
    private String path;

    /**
     * @param classloader Can be null (if so, ClassLoader.getSystemResource() will be called
     * instead of classloader.getResource()
     */
    public ClassLoaderTemplateLoader(ClassLoader classloader, String path){
        this.classloader = classloader;
        this.path = canonicalizePrefix(path);
    }

    @Override
    protected URL getURL(String name) {
        if(classloader == null){
            return ClassLoader.getSystemResource(path+name);
        } else {
            return classloader.getResource(path + name);
        }
    }
}
