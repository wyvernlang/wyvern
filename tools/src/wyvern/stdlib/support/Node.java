package wyvern.stdlib.support;

public class Node<T> {
    private T data;
    private Node<T> right, left;

    public Node(T newData)  {
        data = newData;
        right = null;
        left = null;
    }

    public void setData(T newData) {
        data = newData;
    }

    public T getData()  {
        return data;
    }

    public Node<T> getLeft()  {
        return left;
    }

    public Node<T> setLeft(Node<T> l) {
        left = l;
        return left;
    }

    public Node<T> getRight()  {
        return right;
    }

    public Node<T> setRight(Node<T> r) {
        right = r;
        return right;
    }
}
