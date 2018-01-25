package wyvern.target.oir;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import wyvern.target.corewyvernIL.metadata.HasMetadata;
import wyvern.target.corewyvernIL.metadata.Metadata;

public abstract class OIRAST implements EmitLLVM, HasMetadata {
    private HashSet<Metadata> metadataSet;

    public OIRAST() {
        metadataSet = new HashSet<>();
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
