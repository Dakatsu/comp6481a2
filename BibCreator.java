import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;


public class BibCreator {
	static Scanner[] inScanners;
	static File[][] outFiles;
	static PrintWriter[][] outWriters;

	static String author,journal,title,year,volume,number,pages,doi,ISSN,month;
	static int fileCount = 1;
	
	public static void processFilesForValidation() {
		// Process each file one at a time.
		for (int i = 0; i < inScanners.length; i++) {
			Scanner sc = inScanners[i];
			int articleCount = 1;
			// Read the input file.
			boolean fileIsValid = true;
			while(sc.hasNextLine()) {
				String s = sc.nextLine();
				// If we come across an article, read its contents and add it to the file if it's valid.
				if(s.startsWith("@ARTICLE{")) {
					while (!s.equals("}")) {
						s = sc.nextLine();
						try {
							format(s);
						}
						// Stop reading this article if it is invalid.
						catch (FileInvalidException e) {
							System.out.println("Error: Detected Empty Field! " + fileCount + "." + articleCount + ": " + e.getMessage());
							fileIsValid = false;
							break;
						}
					}
					if (fileIsValid) {
						System.out.println("File " + fileCount + "." + articleCount);
				
						String author1 = author.replaceAll(" and", ",");
						int andIndex = author.indexOf("and");
						String author2 = andIndex != -1 ? author.replaceAll(author.substring(andIndex,author.length()), "et al.") : author;
						String author3 = author.replaceAll("and", "&");
				
						outWriters[i][0].println(author1+". \""+title+"\", "+journal+", vol. "+volume+", no. "+number+", p. "+pages+", "+month+" "+year+".");
						outWriters[i][1].println("[" + articleCount + "]\t"+author2+" "+title+". "+journal+". "+volume+", "+number+" (+"+year+"), "+pages+". DOI:https://doi.org/"+doi+".");
						outWriters[i][2].println(author3+". "+title+". "+journal+". "+volume+", "+pages+"("+year+").");
						articleCount++;
					}
					// Stop reading this file if there is an invalid article.
					else {
						break;
					}
				}	
			}
			fileCount++;
			// Close the output writers. Delete the output files if input is invalid.
			for (int j = 0; j < 3; j++) {
				outWriters[i][j].close();
				if (!fileIsValid) {
					outFiles[i][j].delete();
				}
			}
			// Close input file after reading.
			sc.close();
		}
	}
	
	public static void format(String s) throws FileInvalidException {
		if (s.indexOf('=') == -1) {
			//System.out.println("FORMAT LINE: " + s);
			return;
		}
		
		String key = s.substring(0, s.indexOf('='));
		
		// Handles empty field.
		if (s.indexOf('{') == -1 || s.indexOf('}') == -1 || s.indexOf('}') - s.indexOf('{') < 2) {
			throw new FileInvalidException("Empty field when reading key: " + key);
		}

		//System.out.println(s.indexOf('{') + ", " + s.indexOf('}'));
		String value = s.substring(s.indexOf('{') + 1, s.indexOf('}'));

		switch(key) {
		case "author":  
			author = value;
			break;
		case "journal":  
			journal = value;
			break;
		case "title":  
			title = value;
			break;
		case "year":  
			year = value;
			break;
		case "volume":  
			volume = value;
			break;
		case "number":  
			number = value;
			break;
		case "pages":  
			pages = value;
			break;
		case "doi":  
			doi = value;
			break;
		case "ISSN":  
			ISSN = value;
			break;
		case "month":  
			month = value;
			break;
		default:
			// Do nothing.
			//System.out.println("No case matched");
		}

	} 
	
	public static void main(String[] args) {
		int numFiles = 10;
		// Open every input file and store their scanners in an array.
		inScanners = new Scanner[numFiles];
		for (int fileID = 1; fileID <= numFiles; fileID++) {
			String fileName = "C:\\Users\\Sony\\Documents\\Concordia Study\\COMP6481\\Assignments\\Comp6481_F21_Assg2_Files\\Latex" + fileID +".bib";
			// For Kyle's testing purposes.
			if (args.length > 0) {
				fileName = "Latex" + fileID + ".bib";
			}
			try {
				inScanners[fileID - 1] = new Scanner(new FileInputStream(fileName));
			}
			// If we cannot open a file, close all previously opened files and exit.
			catch(FileNotFoundException e) {
				System.out.println("Could not open input file " + fileName + " for reading." + 
						"\n\nPlease check if file exists! Program will terminate after closing any opened files.");	
				for (int i = 0; inScanners[i] != null; i++) {
					inScanners[i].close();
				}
				System.exit(0);			   
			}
		}
		
		// Open/create every output file.
		outFiles = new File[numFiles][3];
		outWriters = new PrintWriter[numFiles][3];
		for (int fileID = 1; fileID <= numFiles; fileID++) {
			String[] prefixes = {"IEEE", "ACM", "NJ"};
			for (int pfxID = 0; pfxID < prefixes.length; pfxID++) {
				String fileName = prefixes[pfxID] + fileID + ".txt";
				try {
					outFiles[fileID - 1][pfxID] = new File(fileName);
					outWriters[fileID - 1][pfxID] = new PrintWriter(new FileOutputStream(outFiles[fileID - 1][pfxID]));
				}
				catch (Exception e) {
					System.out.println("Could not create output file " + fileName + "\nAll created files will be deleted before exit.");
					// Close and delete output files before exit.
					// Put in a try/catch to swallow any possible null pointers.
					try {
						for (int i = 0; i <= fileID; i++) {
							for (int j = 0; j < prefixes.length; j++) {
								outWriters[i][j].close();
								outFiles[i][j].delete();
							}
						}
					}
					catch (Exception exception) {
						// Do nothing.
					}
					// Close input files before exit.
					for (Scanner inFile : inScanners) {
						inFile.close();
					}
					System.exit(0);
				}
			}
		}
		
		// Process all the files.
		processFilesForValidation();
		
		// Allow the user to open and display one file.
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Please enter the name of one of the files you need to review:");
		BufferedReader inFile = null;
		try {
			inFile = new BufferedReader(new FileReader(keyboard.nextLine()));
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not open input file. File does not exist; possibly it could not be created!"
					+ "\n\nHowever, you will be allowed another chance to enter another file name."
					+ "\nPlease enter the name of one of the files that you need to review:");
			try {
				inFile = new BufferedReader(new FileReader(keyboard.nextLine()));
			}
			catch (FileNotFoundException e2) {
				System.out.println("Sorry! I am unable to display your desired files! Program will exit!");
				keyboard.close();
				System.exit(0);
			}
		}
		keyboard.close();
		
		// Display the file if successfully opened.
		if (inFile != null) {
			System.out.println("Here are the contents of the successfully created File:");
			try {
				String line = inFile.readLine();
				while (line != null) {
					System.out.println(line);
					line = inFile.readLine();
				}
			} 
			catch (IOException e) {
				System.out.println("Exception occured when attempting to read file: " + e.getMessage());
			}
		}
		System.out.println("Goodbye! Hope you have enjoyed creating the needed files using BibCreator.");
	}

}
