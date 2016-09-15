package wyvern.target.corewyvernIL;

import java.io.IOException;
import java.util.HashSet;

import wyvern.target.corewyvernIL.metadata.HasMetadata;
import wyvern.target.corewyvernIL.metadata.Metadata;

import wyvern.target.corewyvernIL.metadata.IsTailCall;

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
        if (metadata instanceof IsTailCall) {
            System.out.println("Found a tail call:");
            StringBuilder builder = new StringBuilder();
            try {this.doPrettyPrint(builder, "");}
            catch (Exception e) {}
            System.out.println(builder.toString());
        }
        metadataSet.add(metadata);
    }

    public void copyMetadata(HasMetadata other) {
        Metadata[] metadata = other.getMetadata();
        for (Metadata m : metadata) {
            addMetadata(m);
        }
    }
}
