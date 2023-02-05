package com.nyit.treeProject;

import java.util.Arrays;
import java.util.Comparator;

// This structure is based on https://www.programiz.com/dsa/b-plus-tree
// 2-4 child nodes per parent
public class BPlusTree {
	public static int maxPartsPerLeaf = 16;
	int order;
	InternalNode root;
	LeafNode firstLeaf;
	int leafSplits = 0;
	int internalSplits = 0;
	int leafMerge = 0;
	int internalMerge = 0;
	int depth = 0;
	 
	
	/**
	 * Constructor
	 * @param m: the order of the B+ tree
	 */
	public BPlusTree(int m) {
		this.order = m;
		this.root = null;
	}
	/**
	 * Traverse the leaf node of the tree
	 * @return String of partsList
	 */
	public String tranverseTree() {
		String resultData = "";
		LeafNode currentLeaf = this.firstLeaf;
		while(currentLeaf.rightSibling != null) {
			int currentLeafPartsNum = currentLeaf.numOfParts;
			for(int i = 0; i < currentLeafPartsNum; i++) {
				PartObject part = currentLeaf.partsList[i];
				resultData = resultData.concat(part.id + "        " + part.description + "\n");
			}
			currentLeaf = currentLeaf.rightSibling;
			if(currentLeaf.rightSibling == null) {
				currentLeafPartsNum = currentLeaf.numOfParts;
				for(int i = 0; i < currentLeafPartsNum; i++) {
					PartObject part = currentLeaf.partsList[i];
					resultData = resultData.concat(part.id + "        " + part.description + "\n");
				}
			}
		}
		return resultData;
	}
	/**
	 * Print the splits and merges
	 */
	public void printStats() {
		 System.out.println("split: " + this.leafSplits);
	     System.out.println("parent split: " + this.internalSplits);
	     System.out.println("merge: " + this.leafMerge);
	     System.out.println("parent merge: " + this.internalMerge);
	     System.out.println("depth: " + this.depth);
	}
	/**
	 * get how many parts in the partsList, can have null of elements
	 * @param partsList
	 * @return num of valid parts in the list
	 */
	static public int getValidPartsNum(PartObject[] partsList) {
		for (int i = 0; i <  partsList.length; i++) {
			if (partsList[i] == null) { return i; }
		}
		return -1;
	}
	/**
	 * same as function above but is for internalNode and leafNode
	 * @param pointers
	 * @return num of valid nodes
	 */
	static public int linearNullSearch(Node[] pointers) {
		for (int i = 0; i <  pointers.length; i++) {
			if (pointers[i] == null) { return i; }
		}
		return -1;
	}
	/**
	 * This method performs a standard binary search on a sorted
	 * PartObject[] and returns the index of the partsList pair
	 * with target key t if found. Otherwise, this method returns a negative
	 * value.
	 * @param numOfParts: how many parts need to be searched index wise
	 * @param partsList: list of partsList pairs sorted by key within leaf node
	 * @param id: target key value of partsList pair being searched for
	 * @return index of the target value if found, else a negative value
	 */
	public int binarySearch(PartObject[] partsList, int numOfParts, String id) {
		Comparator<PartObject> c = new Comparator<PartObject>() {
			@Override
			public int compare(PartObject o1, PartObject o2) {
				String a = o1.id;
				String b = o2.id;
				return a.compareTo(b);
			}
		};
		return Arrays.binarySearch(partsList, 0, numOfParts, new PartObject(id,""),c);
	}
	/**
	 * look for node index in nodes list
	 * @param pointers
	 * @param node
	 * @return index of the node in list
	 */
	public int findIndexOfPointer(Node[] pointers, LeafNode node) {
		int i;
		for (i = 0; i < pointers.length; i++) {
			if (pointers[i] == node) { break; }
		}
		return i;
	}
	/**
	 * pop pointers from left for certain amount
	 * @param pointers list of pointers to pop
	 * @param amount num of pointers need to pop in the list
	 */
	public void popPointers(Node[] pointers, int amount) {
		Node[] newPointers = new Node[this.order + 1];
		for (int i = amount; i < pointers.length; i++) {
			newPointers[i - amount] = pointers[i];
		}
		pointers = newPointers;
	}
	/**
	 * Java util lib sorting method
	 * @param partsList: a list of PartObject objects
	 */
	private void sortPartsList(PartObject[] partsList) {
		Arrays.sort(partsList, new Comparator<PartObject>() {
			@Override
			public int compare(PartObject o1, PartObject o2) {
				if (o1 == null && o2 == null) { return 0; }
				if (o1 == null) { return 1; }
				if (o2 == null) { return -1; }
				return o1.compareTo(o2);
			}
		});
	}

	/**
	 * Doing merges or height changes based on deficient node as param
	 * @param in
	 */
	public void handleDeficiency(InternalNode in) {

		InternalNode sibling;
		InternalNode parent = in.parentNode;

		// root node is deficient, remove this and use the next child as root
		if (this.root == in) {
			for (int i = 0; i < in.childPointers.length; i++) {
				if (in.childPointers[i] != null) {
					if (in.childPointers[i] instanceof InternalNode) {
						this.depth -= 1;
						this.root = (InternalNode)in.childPointers[i];
						this.root.parentNode = null;
					} else if (in.childPointers[i] instanceof LeafNode) {
						this.depth -= 1;
						this.root = null;
					}
				}
			}
		}

		// Borrow:
		else if (in.rightSibling != null && in.rightSibling.isLendable()) {
			sibling = in.rightSibling;
			this.internalMerge += 1;
			String borrowedKey = sibling.keys[0];
			Node pointer = sibling.childPointers[0];

			in.keys[in.degree - 1] = parent.keys[0];
			in.childPointers[in.degree] = pointer;

			// Copy borrowedKey into root
			parent.keys[0] = borrowedKey;

			// Delete key and pointer from sibling
			sibling.removePointer(0);
			Arrays.sort(sibling.keys);
			sibling.removePointer(0);
			popPointers(in.childPointers, 1);
		}

		// Merge:
		else if (in.rightSibling != null && in.rightSibling.isMergeable()) {
			sibling = in.rightSibling;
			this.internalMerge += 1;
			// Copy rightmost key in parent to beginning of sibling's keys &
			// delete key from parent
			sibling.keys[sibling.degree - 1] = parent.keys[parent.degree - 2];
			Arrays.sort(sibling.keys, 0, sibling.degree);
			parent.keys[parent.degree - 2] = null;

			// Copy in's child pointer over to sibling's list of child pointers
			for (int i = 0; i < in.childPointers.length; i++) {
				if (in.childPointers[i] != null) {
					sibling.insertChildPointer(in.childPointers[i],0);
					in.childPointers[i].parentNode = sibling;
					in.removePointer(i);
				}
			}

			// Delete child pointer from grandparent to deficient node
			parent.removePointer(in);

			// Remove left sibling
			sibling.leftSibling = in.leftSibling;
		}

		// Handle deficiency a level up if it exists
		if (parent != null && parent.isDeficient()) {
			handleDeficiency(parent);
		}
	}

	/**
	 * if the firstLeaf is null, the BPlusTree is empty
	 * @return true if empty else false
	 */
	public boolean isEmpty() {
		return firstLeaf == null;
	}
	/**
	 * find the leaf node based on param key
	 * @param key
	 * @return the leaf node found
	 */
	public LeafNode findLeafNode(String key) {

		// Initialize keys and index variable
		String[] keys = this.root.keys;
		int i;

		// Find next node on path to appropriate leaf node
		for (i = 0; i < this.root.degree - 1; i++) {
			if (key.compareTo(keys[i]) < 0) { break; }
		}

		/* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
		Node child = this.root.childPointers[i];
		if (child instanceof LeafNode) {
			return (LeafNode)child;
		} else {
			return findLeafNode((InternalNode)child, key);
		}
	}
	/**
	 * find the leaf node based on param key and node
	 * @param key
	 * @return the leaf node found
	 */
	public LeafNode findLeafNode(InternalNode node, String key) {

		// Initialize keys and index variable
		String[] keys = node.keys;
		int i;

		// Find next node on path to appropriate leaf node
		for (i = 0; i < node.degree - 1; i++) {
			if (key.compareTo(keys[i]) < 0) { break; }
		}

		/* Return node if it is a LeafNode object,
		   otherwise repeat the search function a level down */
		Node childNode = node.childPointers[i];
		if (childNode instanceof LeafNode) {
			return (LeafNode)childNode;
		} else {
			return findLeafNode((InternalNode)node.childPointers[i], key);
		}
	}
	/**
	 * get the midpoint of leaf using for the key generation
	 * @return index of the midpoint
	 */
	public int getLeafMidpoint() {
		return (int)Math.ceil((maxPartsPerLeaf + 1) / 2.0) - 1;
	}
	/**
	 * get the midpoint of node using for the key generation
	 * @return index of the midpoint
	 */
	public int getMidpoint() {
		return (int)Math.ceil((this.order + 1) / 2.0) - 1;
	}
	/**
	 * Java util lib for sorting
	 * @param partsList
	 */
	public void sortpartsList(PartObject[] partsList) {
		Arrays.sort(partsList, new Comparator<PartObject>() {
			@Override
			public int compare(PartObject o1, PartObject o2) {
				if (o1 == null && o2 == null) { return 0; }
				if (o1 == null) { return 1; }
				if (o2 == null) { return -1; }
				return o1.compareTo(o2);
			}
		});
	}
	/**
	 * This method modifies a list of Integer-typed objects that represent keys
	 * by removing half of the keys and returning them in a separate Integer[].
	 * This method is used when splitting an InternalNode object.
	 * @param keys: a list of Integer objects
	 * @param split: the index where the split is to occur
	 * @return Integer[] of removed keys
	 */
	public String[] splitKeys(String[] keys, int split) {

		String[] halfKeys = new String[this.order];

		// Remove split-indexed value from keys
		keys[split] = null;

		// Copy half of the values into halfKeys while updating original keys
		for (int i = split + 1; i < keys.length; i++) {
			halfKeys[i - split - 1] = keys[i];
			keys[i] = null;
		}

		return halfKeys;
	}
	private Node[] splitChildPointers(InternalNode in, int split) {

		Node[] pointers = in.childPointers;
		Node[] halfPointers = new Node[this.order + 1];

		// Copy half of the values into halfPointers while updating original keys
		for (int i = split + 1; i < pointers.length; i++) {
			halfPointers[i - split - 1] = pointers[i];
			in.removePointer(i);
		}

		return halfPointers;
	}

	/**
	 * When an insertion into the B+ tree causes an overfull node, this method
	 * is called to remedy the issue, i.e. to split the overfull node. This method
	 * calls the sub-methods of splitKeys() and splitChildPointers() in order to
	 * split the overfull node.
	 * @param in: an overfull InternalNode that is to be split
	 */
	public void splitInternalNode(InternalNode in) {
		this.internalSplits += 1;

		// Acquire parent
		InternalNode parent = in.parentNode;

		// Split keys and pointers in half
		int midpoint = getMidpoint();
		String newParentKey = in.keys[midpoint];
		String[] halfKeys = splitKeys(in.keys, midpoint);
		Node[] halfPointers = splitChildPointers(in, midpoint);

		// Change degree of original InternalNode in
		in.degree = linearNullSearch(in.childPointers);

		// Create new sibling internal node and add half of keys and pointers
		InternalNode sibling = new InternalNode(this.order, halfKeys, halfPointers);
		for (Node pointer : halfPointers) {
			if (pointer != null) { pointer.parentNode = sibling; }
		}

		// Make internal nodes siblings of one another
		sibling.rightSibling = in.rightSibling;
		if (sibling.rightSibling != null) {
			sibling.rightSibling.leftSibling = sibling;
		}
		in.rightSibling = sibling;
		sibling.leftSibling = in;

		if (parent == null) {
			this.depth += 1;
			// Create new root node and add midpoint key and pointers
			String[] keys = new String[this.order];
			keys[0] = newParentKey;
			InternalNode newRoot = new InternalNode(this.order, keys);
			newRoot.appendChildPointer(in);
			newRoot.appendChildPointer(sibling);
			this.root = newRoot;

			// Add pointers from children to parent
			in.parentNode = newRoot;
			sibling.parentNode = newRoot;

		} else {

			// Add key to parent
			parent.keys[parent.degree - 1] = newParentKey;
			Arrays.sort(parent.keys, 0, parent.degree);

			// Set up pointer to new sibling
			int pointerIndex = parent.findIndexOfPointer(in) + 1;
			parent.insertChildPointer(sibling, pointerIndex);
			sibling.parentNode = parent;
		}
	}
	public PartObject[] splitPartsList(LeafNode ln, int split) {
		this.leafSplits += 1;
		PartObject[] partsList = ln.partsList;

		/* Initialize two dictionaries that each hold half of the original
		   partsList values */
		PartObject[] halfPartsList = new PartObject[maxPartsPerLeaf + 1];

		// Copy half of the values into halfPartsList
		for (int i = split; i < partsList.length; i++) {
			halfPartsList[i - split] = partsList[i];
			ln.delete(i);
		}

		return halfPartsList;
	}
	/**
	 * search a part id will return the description of the part
	 * @param key
	 * @return
	 */
	public SearchResult search(String key) {

		// If B+ tree is completely empty, simply return null
		if (isEmpty()) { return null; }

		// Find leaf node that holds the dictionary key
		LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);

		// Perform binary search to find index of key within dictionary
		PartObject[] partsList = ln.partsList;
		int index = binarySearch(partsList, ln.numOfParts, key);
		
		// If index negative, the key doesn't exist in B+ tree
		if (index < 0) {
			return null;
		} else {
			SearchResult result = new SearchResult(ln,index);
			return result;
		}
	}
	/**
	 * search a part id will return the description of the part
	 * @param key
	 * @return
	 */
	public String modifyDescription(String key, String newDescription) {

		// If B+ tree is completely empty, simply return null
		if (isEmpty()) { return null; }

		// Find leaf node that holds the dictionary key
		LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);

		// Perform binary search to find index of key within dictionary
		PartObject[] partsList = ln.partsList;
		int index = binarySearch(partsList, ln.numOfParts, key);

		// If index negative, the key doesn't exist in B+ tree
		if (index < 0) {
			return null;
		} else {
			partsList[index].description = newDescription;
			return partsList[index].description;
		}
	}
	public void delete(String key) {
		if (isEmpty()) {

			/* Flow of execution goes here when B+ tree has no partsList pairs */

			System.err.println("Invalid Delete: The B+ tree is currently empty.");

		} else {

			// Get leaf node and attempt to find index of key to delete
			LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);
			int partIndex = binarySearch(ln.partsList, ln.numOfParts, key);


			if (partIndex < 0) {

				/* Flow of execution goes here when key is absent in B+ tree */

				System.err.println("Invalid Delete: Key unable to be found.");

			} else {

				// Successfully delete the partsList pair
				ln.delete(partIndex);
				System.out.println("Key deleted Successfully!");

				// Check for deficiencies
				if (ln.isDeficient()) {

					LeafNode sibling;
					InternalNode parentNode = ln.parentNode;

					// Borrow: First, check the left sibling, then the right sibling
					if (ln.leftSibling != null &&
						ln.leftSibling.parentNode == ln.parentNode &&
						ln.leftSibling.isLendable()) {

						this.leafMerge += 1;
						sibling = ln.leftSibling;
						PartObject borrowedPartObject = sibling.partsList[sibling.numOfParts - 1];

						/* Insert borrowed partsList pair, sort partsList,
						   and delete partsList pair from sibling */
						ln.insert(borrowedPartObject);
						sortPartsList(ln.partsList);
						sibling.delete(sibling.numOfParts - 1);

						// Update key in parentNode if necessary
						int pointerIndex = findIndexOfPointer(parentNode.childPointers, ln);
						if (!(borrowedPartObject.id.compareTo(parentNode.keys[pointerIndex - 1]) >= 0)) {
							parentNode.keys[pointerIndex - 1] = ln.partsList[0].id;
						}

					} else if (ln.rightSibling != null &&
							   ln.rightSibling.parentNode == ln.parentNode &&
							   ln.rightSibling.isLendable()) {
						this.leafMerge += 1;
						sibling = ln.rightSibling;
						PartObject borrowedPartObject = sibling.partsList[0];

						/* Insert borrowed partsList pair, sort partsList,
					       and delete partsList pair from sibling */
						ln.insert(borrowedPartObject);
						sibling.delete(0);
						sortPartsList(sibling.partsList);

						// Update key in parentNode if necessary
						int pointerIndex = findIndexOfPointer(parentNode.childPointers, ln);
						if (!(borrowedPartObject.id.compareTo(parentNode.keys[pointerIndex]) < 0)) {
							parentNode.keys[pointerIndex] = sibling.partsList[0].id;
						}

					}

					// Merge: First, check the left sibling, then the right sibling
					else if (ln.leftSibling != null &&
							 ln.leftSibling.parentNode == ln.parentNode &&
							 ln.leftSibling.isMergeable()) {
						sibling = ln.leftSibling;
						int pointerIndex = findIndexOfPointer(parentNode.childPointers, ln);

						// Remove key and child pointer from parentNode
						parentNode.removeKey(pointerIndex - 1);
						parentNode.removePointer(ln);

						// Update sibling pointer
						sibling.rightSibling = ln.rightSibling;

						// Check for deficiencies in parentNode
						if (parentNode.isDeficient()) {
							handleDeficiency(parentNode);
						}

					} else if (ln.rightSibling != null &&
							   ln.rightSibling.parentNode == ln.parentNode &&
							   ln.rightSibling.isMergeable()) {
						sibling = ln.rightSibling;
						int pointerIndex = findIndexOfPointer(parentNode.childPointers, ln);

						// Remove key and child pointer from parentNode
						parentNode.removeKey(pointerIndex);
						parentNode.removePointer(pointerIndex);

						// Update sibling pointer
						sibling.leftSibling = ln.leftSibling;
						if (sibling.leftSibling == null) {
							firstLeaf = sibling;
						}

						if (parentNode.isDeficient()) {
							handleDeficiency(parentNode);
						}
					}

				} else if (this.root == null && this.firstLeaf.numOfParts == 0) {

					/* Flow of execution goes here when the deleted partsList
					   pair was the only pair within the tree */

					// Set first leaf as null to indicate B+ tree is empty
					this.firstLeaf = null;

				} else {

					/* The partsList of the LeafNode object may need to be
					   sorted after a successful delete */
					sortPartsList(ln.partsList);

				}
			}
		}
	}
	/**
	 * insertion the part id and description
	 * @param key
	 * @param value
	 */
	public void insert(String key, String value){
		if(isEmpty()) {
			// initial insert
			LeafNode ln = new LeafNode(maxPartsPerLeaf, new PartObject(key, value));
			this.firstLeaf = ln;
		} else {
			// Find leaf node to insert to
			LeafNode ln = (this.root == null) ? this.firstLeaf : findLeafNode(key);
			
			// The insert failed because the LeafNode is full
			if( !ln.insert(new PartObject(key, value))) {
				// Sort all the partsList with the included pair to be inserted
				ln.partsList[ln.numOfParts] = new PartObject(key, value);
				ln.numOfParts++;
				sortPartsList(ln.partsList);
				
				// Split the sorted parts into two arrays
				int midpoint = getLeafMidpoint();
				PartObject[] halfPartsList = splitPartsList(ln, midpoint);
				
				if (ln.parentNode == null) {

					// No internal node yet, just leaf

					// Create internal node to serve as parent, use partsList midpoint key
					String[] parent_keys = new String[this.order];
					parent_keys[0] = halfPartsList[0].id;
					InternalNode parent = new InternalNode(this.order, parent_keys);
					ln.parentNode = parent;
					parent.appendChildPointer(ln);

				} else {

					// There is internal node

					// Add new key to parent for proper indexing
					String newParentKey = halfPartsList[0].id;
					ln.parentNode.keys[ln.parentNode.degree - 1] = newParentKey;
					Arrays.sort(ln.parentNode.keys, 0, ln.parentNode.degree);
				}
				// Create new LeafNode that holds the other half
				LeafNode newLeafNode = new LeafNode(maxPartsPerLeaf, halfPartsList, ln.parentNode);

				// Update child pointers of parent node
				int pointerIndex = ln.parentNode.findIndexOfPointer(ln) + 1;
				ln.parentNode.insertChildPointer(newLeafNode, pointerIndex);

				// Make leaf nodes siblings of one another
				newLeafNode.rightSibling = ln.rightSibling;
				if (newLeafNode.rightSibling != null) {
					newLeafNode.rightSibling.leftSibling = newLeafNode;
				}
				ln.rightSibling = newLeafNode;
				newLeafNode.leftSibling = ln;
			}
			
			if (this.root == null) {
				this.depth += 1;
				// Set the root of B+ tree to be the parent
				this.root = ln.parentNode;

			} else {

				/* If parent is exceeds the maximum it should hold, repeat the process up the tree,
		   		   until no deficiencies are found */
				InternalNode in = ln.parentNode;
				while (in != null) {
					if (in.isOverfull()) {
						splitInternalNode(in);
					} else {
						break;
					}
					in = in.parentNode;
				}
			}
		}
	}
}
