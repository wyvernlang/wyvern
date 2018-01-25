package wyvern.target.oir;

public class FinalPICNode {
    private PICEntry picEntry;
    private long fieldAddress;

    public PICEntry getPicEntry() {
        return picEntry;
    }
    public long getFieldAddress() {
        return fieldAddress;
    }
    public FinalPICNode(long fieldAddress, PICEntry picEntry) {
        super();
        this.picEntry = picEntry;
        this.fieldAddress = fieldAddress;
    }
}
