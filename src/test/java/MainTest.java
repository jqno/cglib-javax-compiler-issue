import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class MainTest {
    private static String CODE =
            "public class Container {" +
            "    public String s;" +
            "}";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void raw() throws Exception {
        Main main = new Main();
        File tempFileLocation = folder.newFolder();
        try (Compiler compiler = new Compiler(tempFileLocation)) {
            Class type = compiler.compile("Container", CODE);
            Class<?> subtype = main.createDynamicSubclass(type);
            assertNotNull(subtype);
        }
    }
}
