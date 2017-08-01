/*
 *  Java Program to Implement Trie
 */

import java.io.Serializable;
import java.util.*;

class MyLinkedList<E> {
    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.data = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private int size;
    private Node<E> root;
    private Node<E> last;

    public void addElement(E e) {
        Node<E> l = last;
        Node<E> newNode = new Node<E>(l, e, null);
        last = newNode;
        if (l == null)
            root = newNode;
        else
            l.next = newNode;
        size++;
    }

    public void removeElement(Object o) {
        for (Node<E> x = root; x != null; x = x.next) {
            if (o.equals(x.data)) {
                E element = x.data;
                Node<E> next = x.next;
                Node<E> prev = x.prev;
                if (prev == null) {
                    root = next;
                } else {
                    prev.next = next;
                    x.prev = null;
                }

                if (next == null) {
                    last = prev;
                } else {
                    next.prev = prev;
                    x.next = null;
                }
                x.data = null;
                size--;
                return;
            }
        }
    }

}

class TrieNode implements Serializable {

    char data;
    boolean finished;
    int count;
    ArrayList<TrieNode> childList;
    int occurence;

    public TrieNode(char c) {
        childList = new ArrayList<TrieNode>();
        finished = false;
        data = c;
        count = 0;
        occurence = 0;
    }

    public void printAll() {

    }

    public TrieNode subNode(char c) {
        if (childList != null)
            for (TrieNode child : childList)
                if (child.data == c)
                    return child;
        return null;
    }
}

class Trie implements Serializable {

    private TrieNode root;

    public Trie() {
        root = new TrieNode(' ');
    }

    public void clear() {
        root = new TrieNode(' ');
    }

    public void insert(String str) {
        if (search(str) == true)
            return;
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            TrieNode child = current.subNode(ch);
            if (child != null)
                current = child;
            else {
                current.childList.add(new TrieNode(ch));
                current = current.subNode(ch);
            }
            current.count++;
        }
        current.finished = true;
    }

    public boolean search(String str) {
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            if (current.subNode(ch) == null)
                return false;
            else
                current = current.subNode(ch);
        }
        if (current.finished == true)
            return true;

        return false;
    }

    public void remove(String str) {
        if (search(str) == false) {
            return;
        }
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            TrieNode child = current.subNode(ch);
            if (child.count == 1) {
                current.childList.remove(child);
                return;
            } else {
                child.count--;
                current = child;
            }
        }
        current.finished = false;
    }

    public int getOccurences(String str) {
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            if (current.subNode(ch) == null) {

                return -1;
            } else
                current = current.subNode(ch);
        }
        if (current.finished == true) {
            if (current.occurence >= 0) {
                return current.occurence;
            }
        }
        return -1;
    }

    public boolean increment(String str) {
        TrieNode current = root;
        for (char ch : str.toCharArray()) {
            if (current.subNode(ch) == null) {
                return false;
            } else
                current = current.subNode(ch);
        }
        if (current.finished == true) {
            if (current.occurence >= 0) {
                current.occurence++;
                return true;
            }
        }
        return false;
    }


}
