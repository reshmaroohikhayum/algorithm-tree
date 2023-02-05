package com.nyit.treeProject;

public class PartObject implements Comparable<PartObject>{
	String id;
	String description;
	
	public PartObject(String id, String description){
		this.id = id;
		this.description = description;
	}
	public PartObject modifyDescription(String newDescription) {
		this.description = newDescription;
		return this;
	}
	@Override
	public int compareTo(PartObject o) {
		return this.id.compareTo(o.id);
	}
	public String getDescription() {
		return this.description;
	}
}
