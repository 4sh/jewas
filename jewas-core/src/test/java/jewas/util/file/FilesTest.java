package jewas.util.file;

import org.junit.Test;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class FilesTest {

    @Test(expected = IllegalArgumentException.class)
    public void getFileExtensionWithNullParameter() {
        Files.getFileExtension(null);
    }

    @Test
    public void getFileExtension() {
        String res1 = Files.getFileExtension("withoutDot");
        assertThat(res1, is(equalTo("")));

        String res2 = Files.getFileExtension("filename.ext");
        assertThat(res2, is(equalTo("ext")));

        String res3 = Files.getFileExtension("multiple.dots.which.one.would.you.choose.?");
        assertThat(res3, is(equalTo("?")));

        String res4 = Files.getFileExtension(".ext");
        assertThat(res4, is(equalTo("ext")));
    }


}
