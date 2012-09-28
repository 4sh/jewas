package jewas.configuration;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: driccio
 * Date: 21/07/11
 * Time: 20:05
 * <p/>
 * The implementation of {@link JewasConfiguration} to use for test.
 */
public class JewasConfigurationForTest extends JewasConfiguration {
    /**
     * The special instance of delegate to use.
     */
    private static final JewasConfigurationDelegateForTest DELEGATE_FOR_TEST_INSTANCE =
            new JewasConfigurationDelegateForTest();

    /**
     * Init the delegate of {@link JewasConfiguration} with DELEGATE_FOR_TEST_INSTANCE if needed.
     */
    private static void initDelegateIfNeeded() {
        if (delegate != DELEGATE_FOR_TEST_INSTANCE) {
            // Here we throw an exception which is directly catched.
            // It allows us to get the called hierarchy (via the StackTrace) which can be useful
            // in debug to know when the delegate have been initialized.
            try {
                throw new GetStackTraceException();
            } catch (GetStackTraceException e) {
                DELEGATE_FOR_TEST_INSTANCE.stacktrace = e.getStackTrace();
            }

            // The delegate of {@link JewasConfiguration} is set with the special instance
            // DELEGATE_FOR_TEST_INSTANCE.
            delegate = DELEGATE_FOR_TEST_INSTANCE;
        }
    }

    public static void override(Properties props) {
        initDelegateIfNeeded();

        DELEGATE_FOR_TEST_INSTANCE.override(props);
    }

    public static void clean() {
        initDelegateIfNeeded();

        DELEGATE_FOR_TEST_INSTANCE.clean();
    }
}
