package wyvern.target.corewyvernIL;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.metadata.HasMetadata;
import wyvern.target.corewyvernIL.metadata.Metadata;
import wyvern.tools.errors.FileLocation;
import wyvern.tools.errors.HasLocation;

public abstract class ASTNode implements HasLocation, IASTNode, HasMetadata {
    private FileLocation location;
    private HashSet<Metadata> metadataSet;

    /* TODO: eventually get rid of this constructor if we can,
     * so that every ASTNode has a valid FileLocation */
    public ASTNode() {
        this((FileLocation) null);
    }

    public ASTNode(FileLocation location) {
        this.location = location;
        metadataSet = new HashSet<>();
    }

    public ASTNode(HasLocation hasLocation) {
        this(hasLocation.getLocation());
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
    }

    @Override
    public FileLocation getLocation() {
        return location;
    }

    public Set<Metadata> getMetadata() {
        return Collections.unmodifiableSet(metadataSet);
    }

    public void addMetadata(Metadata metadata) {
        metadataSet.add(metadata);
    }

    public void copyMetadata(HasMetadata other) {
        Set<Metadata> metadata = other.getMetadata();
        for (Metadata m : metadata) {
            addMetadata(m);
        }
    }
}
