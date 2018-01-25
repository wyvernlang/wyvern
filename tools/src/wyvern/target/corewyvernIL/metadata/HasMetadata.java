package wyvern.target.corewyvernIL.metadata;

import java.util.Set;

public interface HasMetadata {
    Set<Metadata> getMetadata();
    void addMetadata(Metadata metadata);
    void copyMetadata(HasMetadata other);
}
