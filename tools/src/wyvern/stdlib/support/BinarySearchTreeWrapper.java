package wyvern.stdlib.support;

public class BinarySearchTreeWrapper  {
    public static final BinarySearchTreeWrapper binarySearchTreeWrapper = new BinarySearchTreeWrapper();

    public BinarySearchTreeWrapper()  {  }

    @SuppressWarnings("rawtypes")
    public BinarySearchTree makeBinarySearchTree()  {
        return new BinarySearchTree<>();
    }
}
