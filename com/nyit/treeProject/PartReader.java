package com.nyit.treeProject;

public class PartReader {
	/**
	 * takes in string from scanner and scanned as part object
	 * @param fileContext
	 * @return PartObject
	 */
	public static PartObject readPart(String fileContext) {
		try {
			String partsID = fileContext.substring(0,7);
			String partsDescription = fileContext.substring(15, fileContext.length());
			return new PartObject(partsID, partsDescription);
		}catch(Exception e) {
			System.out.println("Error on reading part data, check the string format. Error String is " + fileContext);
			return null;
		}
	}
}
