package wyvern.target.oir;

import java.util.HashSet;

import wyvern.target.corewyvernIL.metadata.HasMetadata;
import wyvern.target.corewyvernIL.metadata.Metadata;

public abstract class OIRAST implements EmitLLVM, HasMetadata {

    private HashSet<Metadata> metadataSet;

    public OIRAST() {
        metadataSet = new HashSet<>();
    }

    public Metadata[] getMetadata() {
        return metadataSet.toArray(new Metadata[metadataSet.size()]);
    }

    public void addMetadata(Metadata metadata) {
        metadataSet.add(metadata);
    }

    public void copyMetadata(HasMetadata other) {
        Metadata[] metadata = other.getMetadata();
        for (Metadata m : metadata) {
            addMetadata(m);
        }
    }

}
