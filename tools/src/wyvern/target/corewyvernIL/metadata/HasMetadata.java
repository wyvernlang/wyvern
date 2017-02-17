package wyvern.target.corewyvernIL.metadata;

import java.util.Set;

public interface HasMetadata {
    public Set<Metadata> getMetadata();
    public void addMetadata(Metadata metadata);
    public void copyMetadata(HasMetadata other);
}
