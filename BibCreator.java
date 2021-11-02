import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;

/**
 * BibCreator program for COMP 6481 Assignment 2.
 * This program opens files named "LatexN.bib", where 1 <= N <= 10. It parses the articles inside them
 * and prints them to ACM, IEEE, and NJ bibliography JSON files and allows the user to print one of the
 * files to their screen before exiting.
 * @author Parveen Kaur and Kyle Lange
 */
public class BibCreator {
	/**
	 * Array of scanners for reading Latex files.
	 */
	static Scanner[] inScanners;
	
	/**
	 * Array of arrays of three output files.
	 * These are used for deleting files.
	 */
	static File[][] outFiles;
	
	/**
	 * Array of writers for outputting formatted bibliographies.
	 */
	static PrintWriter[][] outWriters;

	/**
	 * The text fields to parse from the Latex files.
	 */
	static String author, journal, title, year, volume, number, pages, doi, ISSN, month;
	
	/**
	 * Validates each input file and outputs the formatted bibliographies to
	 * the appropriate files. An issue with reading a file (empty or missing field)
	 * will cause the output files to be deleted. All files must be opened before
	 * this function is called. This function handles closing the inScanners and
	 * outWriters once file reading/writing is not needed.
	 */
	public static void processFilesForValidation() {
		// Process each file one at a time.
		for (int i = 0; i < inScanners.length; i++) {
			Scanner sc = inScanners[i];
			int articleCount = 1;
			// Read the input file.
			try {
				while(sc.hasNextLine()) {
					String s = sc.nextLine();
					// If we come across an article, read its contents and add it to the file.
					if(s.startsWith("@ARTICLE{")) {
						author = journal = title = year = volume = number = pages = doi = ISSN = month = "";
						while (!s.equals("}")) {
							s = sc.nextLine();
							parseValue(s);
						}
						// Throw an exception if a field is missing.
						if (author.isEmpty() || journal.isEmpty() || title.isEmpty() || year.isEmpty() || volume.isEmpty() 
								|| number.isEmpty() || pages.isEmpty() || doi.isEmpty() || ISSN.isEmpty() || month.isEmpty()) {
							throw new FileInvalidException("One or more fields are missing.");
						}
						// Add the article to the file if valid.
						String author1 = author.replaceAll(" and", ",");
						int andIndex = author.indexOf("and");
						String author2 = andIndex != -1 ? author.replaceAll(author.substring(andIndex,author.length()), "et al.") : author;
						String author3 = author.replaceAll("and", "&");
				
						outWriters[i][0].println(author1+". \""+title+"\", "+journal+", vol. "+volume+", no. "+number+", p. "+pages+", "+month+" "+year+".");
						outWriters[i][1].println("["+articleCount+"]\t"+author2+" "+title+". "+journal+". "+volume+", "+number+" (+"+year+"), "+pages+". DOI:https://doi.org/"+doi+".");
						outWriters[i][2].println(author3+". "+title+". "+journal+". "+volume+", "+pages+"("+year+").");
						articleCount++;
					}	
				}
				// Close output writers once file has been read.
				for (int j = 0; j < 3; j++) {
					outWriters[i][j].close();
				}
			}
			// If file is invalid, close and delete the output files.
			catch (FileInvalidException e) {
				System.out.println("Error: Detected Empty Field!" 
						       + "\n============================"
			                   + "\nProblem detected with input file: Latex" + (i + 1) + ".bib"
						       + "\nFile is invalid: " + e.getMessage() + " Processing stopped at this point. "
						       + "Other empty/missing fields may be present as well.\n");
				for (int j = 0; j < 3; j++) {
					outWriters[i][j].close();
					outFiles[i][j].delete();
				}
			}
			// Close input file after reading.
			sc.close();
		}
	}
	
	/**
	 * Takes a key/value line from an article and writes the value into the appropriate key.
	 * Does nothing if there is no equals sign present.
	 * @param s The string from which to parse the value.
	 * @throws FileInvalidException If the field is empty.
	 */
	public static void parseValue(String s) throws FileInvalidException {
		if (s.indexOf('=') == -1) {
			return;
		}
		
		String key = s.substring(0, s.indexOf('='));
		
		// Handles empty field.
		if (s.indexOf('{') == -1 || s.indexOf('}') == -1 || s.indexOf('}') - s.indexOf('{') < 2) {
			throw new FileInvalidException("Field \"" + key + "\" is empty.");
		}

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
		}

	} 
	
	public static void main(String[] args) {
		System.out.println("Welcome to BibCreator!\n");
		int numFiles = 10;
		// Open every input file and store their scanners in an array.
		inScanners = new Scanner[numFiles];
		for (int fileID = 1; fileID <= numFiles; fileID++) {
			String fileName = "Latex" + fileID + ".bib"; // "C:\\Users\\Sony\\Documents\\Concordia Study\\COMP6481\\Assignments\\Comp6481_F21_Assg2_Files\\Latex" + fileID +".bib";
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
				String fileName = prefixes[pfxID] + fileID + ".json";
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
		// Give them two chances to enter a proper file name.
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
			System.out.println("Here are the contents of the successfully created File:\n");
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
		System.out.println("\nGoodbye! Hope you have enjoyed creating the needed files using BibCreator.");
	}
}

/**
 * This exception is thrown whenever there is an empty or missing field in a .bib file.
 */
class FileInvalidException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileInvalidException() {
		super("Error: Input file cannot be parsed due to missing information (i.e. month={}, title={}, etc.)");
	}
		
	public FileInvalidException(String message) {
		super(message);
	}
}