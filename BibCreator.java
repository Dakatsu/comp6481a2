import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileInputStream;


public class BibCreator {

	static String author,journal,title,year,volume,number,pages,doi,ISSN,month;
	static int filecounter=1,countfile=1;
	
	public static void processFilesForValidation() {
		System.out.println("File " + filecounter);
		PrintWriter pw1 = null, pw2 = null, pw3 = null;	
		try
		{
			pw1 = new PrintWriter(new FileOutputStream("IEEE" + countfile + ".txt", true));
			pw2 = new PrintWriter(new FileOutputStream("ACM" + countfile + ".txt", true));
			pw3 = new PrintWriter(new FileOutputStream("NJ" + countfile + ".txt", true));
		}
		catch(FileNotFoundException e) 			// Since we are attempting to write to the file
		{							   	// exception is automatically thrown if file cannot be created. 
			System.out.println("Could not create a file to write to. "
					+ " Please check for problems such as directory permission"
					+ " or no available memory.");	
			System.out.print("Program will terminate.");
			System.exit(0);			   		   
		}

		String author1 = author.replaceAll(" and", ",");
		int andIndex = author.indexOf("and");
		String author2 = andIndex != -1 ? author.replaceAll(author.substring(andIndex,author.length()), "et al.") : author;
		String author3 = author.replaceAll("and", "&");

		pw1.println(author1+". \""+title+"\", "+journal+", vol. "+volume+", no. "+number+", p. "+pages+", "+month+" "+year+".");
		pw1.close();
		pw2.println("[" + filecounter + "] "+author2+" "+title+". "+journal+". "+volume+", "+number+" (+"+year+"), "+pages+". DOI:https://doi.org/"+doi+".");
		pw2.close();
		pw3.println(author3+". "+title+". "+journal+". "+volume+", "+pages+"("+year+").");
		pw3.close();
		filecounter++;
	}
	
	public static void format(String s) throws FileInvalidException {
		if (s.indexOf('=') == -1) {
			//System.out.println("FORMAT LINE: " + s);
			return;
		}
		
		// Handles empty field.
		if (s.indexOf('{') == -1 || s.indexOf('}') == -1 || s.indexOf('}') - s.indexOf('{') < 2) {
			throw new FileInvalidException();
		}

		String key = s.substring(0, s.indexOf('='));
		System.out.println(s.indexOf('{') + ", " + s.indexOf('}'));
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
		Scanner sc = null;		// A scanner object ,
		while(countfile<=10) {
			try
			{
				String filename = "C:\\Users\\Sony\\Documents\\Concordia Study\\COMP6481\\Assignments\\Comp6481_F21_Assg2_Files\\Latex"+countfile+".bib";
				// For Kyle's testing purposes.
				if (args.length > 0) {
					filename = "Latex" + countfile + ".bib";
				}
				System.out.println(filename);
				sc = new Scanner(new FileInputStream(filename));				     
			}
			catch(FileNotFoundException e) 
			{							   
				System.out.println("Could not open input file for reading."+ " Please check if file exists.");	
				System.out.print("Program will terminate.");
				System.exit(0);			   
			}

			String s;
			filecounter = 1;
			while(sc.hasNextLine()) {
				s = sc.nextLine();
				if(s.startsWith("@ARTICLE{")) {
					while (!s.equals("}")) {
						s = sc.nextLine();
						try {
							format(s);
						}
						catch (FileInvalidException e) {
							System.out.println("TODO EXCEPTION");
							System.exit(0);
						}
					}
					processFilesForValidation();
				}	
			}
			System.out.println("hello2");
			countfile++;
			System.out.println("hello3");
		}
		sc.close();
		System.out.println("All files processed!");
	}

}
