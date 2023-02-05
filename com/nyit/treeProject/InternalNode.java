package com.nyit.treeProject;
// This class extends Node class which is a class indicating the parentNode, it has a property parent
public class InternalNode extends Node{
	int maxDegree;
    int minDegree;
    int degree;
	InternalNode leftSibling;
    InternalNode rightSibling;
    String[] keys;
    Node[] childPointers;
    /**
     * constructor for internal node without child pointers parameter
     * @param m
     * @param keys
     */
    public InternalNode(int m, String[] keys) {
        this.maxDegree = m;
        this.minDegree = (int) Math.ceil(m / 2.0);
        this.degree = 0;
        this.keys = keys;
        this.childPointers = new Node[this.maxDegree + 1];
    }
    /**
     * full constructor
     * @param m
     * @param keys
     * @param pointers
     */
    public InternalNode(int m, String[] keys, Node[] pointers) {
        this.maxDegree = m;
        this.minDegree = (int) Math.ceil(m / 2.0);
        this.degree = BPlusTree.linearNullSearch(pointers);
        this.keys = keys;
        this.childPointers = pointers;
    }
    /**
     * insert at the end index of child pointers
     * @param pointer
     */
    public void appendChildPointer(Node pointer) {
        this.childPointers[degree] = pointer;
        this.degree++;
    }
    /**
     * find the index of pointer based on parameter pointer
     * @param pointer
     * @return
     */
    public int findIndexOfPointer(Node pointer) {
        for (int i = 0; i < childPointers.length; i++) {
          if (childPointers[i] == pointer) {
            return i;
          }
        }
        return -1;
    }
    /**
     * insert the pointer to specific index
     * @param pointer
     * @param index
     */
    public void insertChildPointer(Node pointer, int index) {
        for (int i = degree - 1; i >= index; i--) {
          childPointers[i + 1] = childPointers[i];
        }
        this.childPointers[index] = pointer;
        this.degree++;
    }
    /**
     * remove key at that index
     * @param index
     */
    public void removeKey(int index) {
        this.keys[index] = null;
    }
    /**
     * remove pointer at specific index of childPointers
     * @param index
     */
    public void removePointer(int index) {
        this.childPointers[index] = null;
        this.degree--;
    }
    /**
     * remove specific pointer in childPointers
     * @param pointer
     */
    public void removePointer(Node pointer) {
        for (int i = 0; i < childPointers.length; i++) {
          if (childPointers[i] == pointer) {
            this.childPointers[i] = null;
          }
        }
        this.degree--;
    }
    /**
     * whether the degree is less than minDegree
     * @return
     */
    public boolean isDeficient() {
       return this.degree < this.minDegree;
    }
    /**
     * whether nodes can lend to other nodes
     * @return
     */
    public boolean isLendable() {
       return this.degree > this.minDegree;
     }
    /**
     * whether can merge other nodes
     * @return
     */
    public boolean isMergeable() {
       return this.degree == this.minDegree;
    }
    /**
     * whether the node is overfull
     * @return
     */
    public boolean isOverfull() {
       return this.degree == maxDegree + 1;
    }

}
