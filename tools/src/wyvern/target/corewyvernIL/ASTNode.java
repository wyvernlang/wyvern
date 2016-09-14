package wyvern.target.corewyvernIL;

import java.io.IOException;
import java.util.HashSet;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public abstract class ASTNode implements HasLocation, IASTNode, HasMetadata {
    private FileLocation location;
    private HashSet<Metadata> metadataSet;

	/* TODO: eventually get rid of this constructor if we can,
	 * so that every ASTNode has a valid FileLocation */
	public ASTNode() {
		location = null;
    metadataSet = new HashSet<>();
	}
	
	public ASTNode(FileLocation location) {
		this.location = location;
    metadataSet = new HashSet<>();
	}
	
	public ASTNode(HasLocation hasLocation) {
		this(hasLocation.getLocation());
    metadataSet = new HashSet<>();
	}
	
	public final String prettyPrint() throws IOException {
		Appendable dest = new StringBuilder(); 
		doPrettyPrint(dest, "");
		return dest.toString();
	}
	public final String toString() {
		try {
			return prettyPrint();
		} catch (IOException e) {
			e.printStackTrace();
			return "ERROR_PRINTING";
		}
	}
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append("NOT_IMPLEMENTED(")
			.append(this.getClass().getName())
			.append(')');
		//throw new RuntimeException("not implemented");
	}
	
	@Override
	public FileLocation getLocation() {
		return location;
	}

    public Metadata[] getMetadata() {
        return metadataSet.toArray(new Metadata[metadataSet.size()]);
    }

    public void addMetadata(Metadata metadata) {
        metadataSet.add(metadata);
    }
}
