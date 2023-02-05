package com.nyit.treeProject;

public class SearchResult {
	private LeafNode leaf;
	private int index;
	public SearchResult(LeafNode leaf, int index) {
		this.leaf = leaf;
		this.index = index;
	}
	// get the part of that index for the leaf
	public PartObject getIndexPart() {
		return this.leaf.partsList[this.index];
	}
	// takes the num of partsNeed to print next amount of parts of that leaf,
	// if no parts left, go to the next sibling until amount satisfied or ended
	public PartObject[] getNextParts(int partsNeeded) {
		PartObject[] resultPartsList = new PartObject[partsNeeded];
		LeafNode currentLeaf = this.leaf;
		int currentLeafNumOfParts = currentLeaf.numOfParts;
		int currentIndex = this.index;
		for(int i = 0; i < partsNeeded; i++) {
			resultPartsList[i] = currentLeaf.partsList[currentIndex];
			if(currentIndex == currentLeafNumOfParts - 1) {
				if(currentLeaf.rightSibling == null) {
					return resultPartsList;
				}
				currentLeaf = currentLeaf.rightSibling;
				currentLeafNumOfParts = currentLeaf.numOfParts;
				currentIndex = 0;
			}else {
				currentIndex++;
			}
		}
		return resultPartsList;
	}
}
