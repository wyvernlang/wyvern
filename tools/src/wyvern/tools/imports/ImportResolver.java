package wyvern.tools.imports;

import java.net.URI;

public interface ImportResolver {
	ImportBinder resolveImport(URI uri);
}
