package wyvern.stdlib.support;

public class BinarySearchTree<T extends Comparable<T>> {
    private Node<T> root;

    BinarySearchTree() {
        root = null;
    }

    public T getRoot()  {
        return root.getData();
    }

    public void add(T data)  {
        root = addRec(data, root);
    }

    public boolean isEmpty()  {
        return root == null;
    }

    public T getMin() {
        return getMinRec(root);
    }

    private T getMinRec(Node<T> curr) {
        while (curr.getLeft() != null) {
            curr = curr.getLeft();
        }
        return curr.getData();
    }

    public T getMax() {
      Node<T> curr = root;
      while (curr.getRight() != null) {
          curr = curr.getRight();
      }
      return curr.getData();
    }

    public boolean find(T key)  {
        Node<T> curr = root;
        while (curr != null)  {
          int c = key.compareTo(curr.getData());
          if (c < 0)  {
              curr = curr.getLeft();
          } else if (c > 0) {
              curr = curr.getRight();
          } else {
              return true;
          }
        }
        return false;
    }

    private Node<T> addRec(T data, Node<T> curr) {
        if (curr == null) {
            curr = new Node<>(data);
        }
        int c = data.compareTo(curr.getData());
        if (c > 0) {
            curr.setRight(addRec(data, curr.getRight()));
        } else if (c < 0)  {
            curr.setLeft(addRec(data, curr.getLeft()));
        }
        return curr;
    }

    public void remove(T key) {
        removeRec(key, root);
    }

    private Node<T> removeRec(T key, Node<T> curr) {
        if (curr == null) {
            return root;
        }
        if (key.compareTo(curr.getData()) < 0)  {
            root.setLeft(removeRec(key, root.getLeft()));
        } else if (key.compareTo(root.getData()) > 0) {
            root.setRight(removeRec(key, root.getRight()));
        } else {
            if (root.getLeft() == null && root.getRight() == null) {
                return null;
            } else if (root.getLeft() == null)  {
                return root.getRight();
            } else if (root.getRight() == null) {
                return root.getLeft();
            } else {
                T min = getMinRec(curr.getRight());
                curr.setData(min);
                curr.setRight(removeRec(min, curr.getRight()));
            }
        }
        return curr;
    }
}
