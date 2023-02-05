package com.nyit.treeProject;

import java.io.File; // Import the File class
import java.io.FileNotFoundException; // Import this class to handle errors
import java.io.FileWriter;
import java.util.Scanner;
import java.io.IOException;
import java.net.URL;

public class ExecutionClass {
	// partID: AAA-123 description: ...
	// partID: AAA-234 description: ...

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int PartsDisplay = 10;
		BPlusTree bp = null;
		bp = new BPlusTree(4); // here is 4 based on the requirement
		System.out.println("Testing");
		Scanner sc = new Scanner(System.in);
//		String fileName = args[0];

		try {
			URL path = ExecutionClass.class.getResource("partfile.txt");
			File myFile = new File(path.getFile());
			Scanner myReader = new Scanner(myFile);
			System.out.println("Reading the file...");
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
//	          System.out.println(data);
				PartObject part = PartReader.readPart(data);
				bp.insert(part.id, part.description);

			}
			myReader.close();
			bp.printStats();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		while (true) {
			System.out.println("\nOperations:");
			System.out.println("1.Insert");
			System.out.println("2.Delete");
			System.out.println("3.Search");
			System.out.println("4.Modify Description");
			System.out.println("5.Display");
			System.out.println("6.Exit");
			System.out.println("Please enter the option: ");
			String n = sc.nextLine();
			while (n.compareTo("1") != 0 && n.compareTo("2") != 0 && n.compareTo("3") != 0 && n.compareTo("4") != 0
					&& n.compareTo("5") != 0 && n.compareTo("6") != 0) {
				System.out.println("Invalid operation. Enter another one.");
				n = sc.nextLine();
			}
			int m = Integer.parseInt(n);

//		String key, desc;
			switch (Integer.parseInt(n)) {
			case 1:
				System.out.println("Enter Part ID");
				String key = sc.nextLine();
				System.out.println("Enter Description");
				String desc = sc.nextLine();
				bp.insert(key, desc);
				System.out.println("Insert Successful");
				bp.printStats();
				break;

			case 2:
				System.out.println("Enter the key to delete:");
				key = sc.nextLine();
				bp.delete(key);
				bp.printStats();
				break;

			case 3:
				System.out.println("Enter the key to search");
				key = sc.nextLine();
				SearchResult result = bp.search(key);
				if (result != null) {
					PartObject[] resultPartsList = result.getNextParts(PartsDisplay);
					System.out.println("Key found: " + resultPartsList[0].id + "    " + resultPartsList[0].description +"\n" );
					System.out.println("The next "+ PartsDisplay + " entries are:");
					for(int i = 0; i< resultPartsList.length; i++) {
						if(resultPartsList[i] != null) {
							System.out.println(resultPartsList[i].id + "    " + resultPartsList[i].description);
						}else {
							System.out.println("End of tree");
							break;
						}
					}
				}else
				System.err.println("\n Key not found in B+ tree. \n");
				break;
				
			case 4:
				System.out.println("Enter key to modify");
				key = sc.nextLine();
				System.out.println("Enter new description");
				desc = sc.nextLine();
				bp.modifyDescription(key, desc);
				System.out.println("Modification successful");
				break;
			case 5:
				System.out.print(bp.tranverseTree());
				break;
			case 6:
				System.out.println("Before exit, save the changes? (y/n)");
				String answer = sc.nextLine().toLowerCase();
				if (answer.compareTo("y") == 0) {
					try {
						// TODO: change this to same name as partfile.txt;
						URL path = ExecutionClass.class.getResource("partfile.txt");
						FileWriter fWriter = new FileWriter(path.getFile());
						fWriter.write(bp.tranverseTree());
						fWriter.close();
						System.out.println("Save success.");
						System.exit(0);
					} catch (IOException e) {
						System.out.print("Error in writing");
					}
				}
				if (answer.compareTo("n") == 0) {
					System.out.println("Exited.");
					System.exit(0);
				}
				break;
			}
		}

	}
}
