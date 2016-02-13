package wyvern.tools.parsing.parselang.java;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

//Largely from http://www.ibm.com/developerworks/java/library/j-jcomp/index.html
public class StoringFileManager extends ForwardingJavaFileManager<JavaFileManager> {
	// the delegating class loader (passed to the constructor)
	private final StoringClassLoader classLoader;

	// Internal map of filename URIs to JavaFileObjects.
	private final Map<String, JavaFileObject> fileObjects = new HashMap<>();

	/**
	 * Construct a new FileManager which forwards to the <var>fileManager</var>
	 * for source and to the <var>classLoader</var> for classes
	 *
	 * @param fileManager
	 *           another FileManager that this instance delegates to for
	 *           additional source.
	 * @param classLoader
	 *           a ClassLoader which contains dependent classes that the compiled
	 *           classes will require when compiling them.
	 */
	public StoringFileManager(JavaFileManager fileManager, StoringClassLoader classLoader) {
		super(fileManager);
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	@Override
	public FileObject getFileForInput(Location location, String packageName,
									  String relativeName) throws IOException {
		FileObject o = fileObjects.get(packageName + relativeName);
		if (o != null)
			return o;
		return super.getFileForInput(location, packageName, relativeName);
	}

	public void putFileForInput(StandardLocation location, String packageName,
								String relativeName, JavaFileObject file) {
		fileObjects.put(packageName + relativeName, file);
	}

	/**
	 * Create a JavaFileImpl for an output class file and store it in the
	 * classloader.
	 *
	 * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location,
	 *      java.lang.String, javax.tools.JavaFileObject.Kind,
	 *      javax.tools.FileObject)
	 */
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName,
											   JavaFileObject.Kind kind, FileObject outputFile) throws IOException {
		JavaFileObject file = new CachingJavaFileObject(qualifiedName, kind);
		classLoader.add(qualifiedName, file);
		return file;
	}

	@Override
	public ClassLoader getClassLoader(JavaFileManager.Location location) {
		return classLoader;
	}

	@Override
	public String inferBinaryName(Location loc, JavaFileObject file) {
		String result;
		// For our JavaFileImpl instances, return the file's name, else
		// simply run the default implementation
		if (file instanceof CachingJavaFileObject)
			result = file.getName();
		else
			result = super.inferBinaryName(loc, file);
		return result;
	}

	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName,
										 Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
		Iterable<JavaFileObject> result = super.list(location, packageName, kinds,
				recurse);
		ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();
		if (location == StandardLocation.CLASS_PATH
				&& kinds.contains(JavaFileObject.Kind.CLASS)) {
			for (JavaFileObject file : fileObjects.values()) {
				if (file.getKind() == JavaFileObject.Kind.CLASS && file.getName().startsWith(packageName))
					files.add(file);
			}
			files.addAll(classLoader.files());
		} else if (location == StandardLocation.SOURCE_PATH
				&& kinds.contains(JavaFileObject.Kind.SOURCE)) {
			for (JavaFileObject file : fileObjects.values()) {
				if (file.getKind() == JavaFileObject.Kind.SOURCE && file.getName().startsWith(packageName))
					files.add(file);
			}
		}
		for (JavaFileObject file : result) {
			files.add(file);
		}
		return files;
	}

}
