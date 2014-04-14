package wyvern.tools.imports.extensions;

import wyvern.tools.imports.ImportBinder;
import wyvern.tools.imports.ImportResolver;
import wyvern.tools.typedAST.core.declarations.ClassDeclaration;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.typedAST.JavaClassDecl;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;

import java.net.URI;
import java.util.HashMap;
import java.util.Optional;

public class JavaResolver implements ImportResolver {
	private static ImportResolver instance = new JavaResolver();
	private JavaResolver() {}
	public static ImportResolver getInstance() {
		return instance;
	}

	private static class JavaBinder implements ImportBinder {

		private final JavaClassDecl resolved;
		private final String classAlias;

		public JavaBinder(JavaClassDecl resolved, String classAlias) {
			this.resolved = resolved;
			this.classAlias = classAlias;
		}

		@Override
		public Environment extendTypes(Environment in) {
			return resolved.extendType(in);
		}

		@Override
		public Environment extendNames(Environment in) {
			return resolved.extendName(in, in);
		}

		@Override
		public Environment extend(Environment in) {
			return resolved.extend(in);
		}

		@Override
		public Type typecheck(Environment env) {
			return resolved.typecheck(env, Optional.<Type>empty());
		}

		@Override
		public Environment extendVal(Environment env) {
			return resolved.extendWithValue(env);
		}

		@Override
		public Environment bindVal(Environment env) {
			return resolved.bindDecl(env);
		}
	}

	private HashMap<String, JavaClassDecl> binderHashMap = new HashMap<>();

	@Override
	public ImportBinder resolveImport(URI uri) {
		String className = uri.getSchemeSpecificPart();
		Class resolved;
		try {
			resolved = this.getClass().getClassLoader().loadClass(className);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		String classAlias = uri.getFragment();
		if (binderHashMap.containsKey(className))
			return new JavaBinder(binderHashMap.get(className), classAlias);
		else {
			JavaClassDecl cd = Util.javaToWyvDecl(resolved);
			binderHashMap.put(className, cd);
			return new JavaBinder(cd, classAlias);
		}
	}
}
