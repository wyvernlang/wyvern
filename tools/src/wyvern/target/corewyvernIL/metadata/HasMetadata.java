package wyvern.target.corewyvernIL.metadata;

import java.util.Iterator;

public interface HasMetadata {
    public Metadata[] getMetadata();
    public void addMetadata(Metadata metadata);
    public void copyMetadata(HasMetadata other);
}
