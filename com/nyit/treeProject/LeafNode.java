package com.nyit.treeProject;

import java.util.Arrays;

// This class extends Node class which is a class indicating the parentNode, it has a property parent
public class LeafNode extends Node{
    int maxNumParts;
    int minNumParts;
    int numOfParts;
    LeafNode leftSibling;
    LeafNode rightSibling;
    // instead of dictionary pair, we have parts here
    PartObject[] partsList; // capacity of 16 parts
    
    /**
     * first leaf node constructor
     * @param maxNumParts
     * @param part
     */
    public LeafNode(int maxNumParts, PartObject part) {
    	this.maxNumParts = maxNumParts;
    	this.minNumParts = 1;
    	this.partsList = new PartObject[maxNumParts + 1];
    	this.numOfParts = 0;
    	this.insert(part);
    }
    /**
     * LeafNode with partsList belongs to parent with maxNumParts
     * @param maxNumParts
     * @param partsList
     * @param parent
     */
    public LeafNode(int maxNumParts, PartObject[] partsList, InternalNode parentNode) {
    	this.maxNumParts = maxNumParts;
		this.minNumParts = 1;
		this.partsList = partsList;
		this.numOfParts = BPlusTree.getValidPartsNum(partsList);
		this.parentNode = parentNode;
    }
    /**
     * Whether the current leafNode numOfParts satisfy minNumParts
     * @return true or false
     */
    public boolean isDeficient() { return numOfParts < minNumParts; }
    /**
     * Whether the numOfParts are full
     * @return true or false
     */
    public boolean isFull() { return numOfParts == maxNumParts; }
    /**
     * Whether numOfParts can give at least one part to other leafNode
     * @return
     */
    public boolean isLendable() { return numOfParts > minNumParts; }
    /**
     * Whether this leafNode can merge with other leafNode
     * @return true or false
     */
    public boolean isMergeable() {
		return numOfParts == minNumParts;
	}
    /**
     * insert a part to the partList and return true if success else false
     * @param partObject
     * @return true or false
     */
    public boolean insert(PartObject partObject) {
    	if(this.isFull()) {
    		return false;
    	}else {
    		this.partsList[numOfParts] = partObject;
    		numOfParts++;
    		Arrays.sort(this.partsList, 0, numOfParts);
    		return true;
    	}
    }
    /**
     * delete the part in the partObject with index index
     * @param index
     */
    public void delete(int index) {
    	this.partsList[index] = null;
    	numOfParts--;
    }
}
