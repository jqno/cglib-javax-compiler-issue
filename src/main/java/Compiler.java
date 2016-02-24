import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

public class Compiler implements Closeable {
	private final File tempFolder;
	private final URLClassLoader classLoader;
	
	public Compiler(File tempFolder) {
		this.tempFolder = tempFolder;
		this.classLoader = createClassLoader(tempFolder);
	}
	
	private static URLClassLoader createClassLoader(File tempFolder) {
		try {
			URL[] urls = { tempFolder.toURI().toURL() };
			return new URLClassLoader(urls);
		}
		catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	public Class<?> compile(String className, String code) {
		try {
			JavaFileObject sourceFile = new StringJavaFileObject(className, code);
			compileClass(sourceFile);
			return classLoader.loadClass(className);
		}
		catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	private void compileClass(JavaFileObject sourceFile) throws IOException {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(collector, Locale.ROOT, null)) {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(tempFolder));
			CompilationTask task = compiler.getTask(null, fileManager, collector, null, null, Arrays.asList(sourceFile));
			task.call();
		}
	}

	@Override
	public void close() {
		try {
			classLoader.close();
		}
		catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	private static class StringJavaFileObject extends SimpleJavaFileObject {
		private final String code;
		
		protected StringJavaFileObject(String className, String code) {
			super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}
		
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
			return code;
		}
	}
}
