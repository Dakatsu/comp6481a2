import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.FileInputStream;


public class BibCreator {

	static String author,journal,title,year,volume,number,pages,doi,ISSN,month;
	static int filecounter=1,countfile=1;
	
	public static void processFilesForValidation() {
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


		String author1 =	author.replaceAll(" and", ",");
		int andIndex = author.indexOf("and");
		String author2 = andIndex != -1 ? author.replaceAll(author.substring(andIndex,author.length()), "et al.") : author;
		String author3 = author.replaceAll("and", "&");

		pw1.println(author1+". \""+title+"\", "+journal+", vol. "+volume+", no. "+number+", p. "+pages+", "+month+" "+year+".");
		pw1.close();
		pw2.println("["+filecounter+"]"+author2+" "+title+". "+journal+". "+volume+", "+number+" (+"+year+"), "+pages+". DOI:https://doi.org/"+doi+".");
		pw2.close();
		pw3.println(author3+". "+title+". "+journal+". "+volume+", "+pages+"("+year+").");
		pw3.close();
		filecounter++;
	}
	
	public static void format(String s) {
		if (s.startsWith("@"))
			return;
		
		StringBuilder sb = new StringBuilder();
		char[] arr = s.toCharArray();
		int i=0; 
		String name="";
		if (Character.isDigit(arr[1]) || arr[0] == 'k')
			return;

		while(arr[i]!= '=') {
			name = name + arr[i];
			i++;
		}
		int l=name.length();

		switch(name) {
		case "author":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			author=sb.toString();
			break;

		case "journal":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			journal=sb.toString();
			break;
		case "title":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			title=sb.toString();
			break;
		case "year":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			year=sb.toString();
			break;
		case "volume":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			volume=sb.toString();
			break;
		case "number":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			number=sb.toString();
			break;
		case "pages":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			pages=sb.toString();
			break;
		case "doi":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			doi=sb.toString();
			break;
		case "ISSN":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			ISSN=sb.toString();
			break;
		case "month":  
			while(arr[l+2] != '}') {
				sb.append(arr[l+2]);

				l++;
			}
			month=sb.toString();
			break;
		default:
			System.out.println("No case matched");
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


			int count=0;
			String s;
			while(sc.hasNextLine()) {

				s=sc.nextLine();
				if(s.length()==0 || s.length()==1 ) 
					continue;
				else {
					format(s);
					count++;
					if(count == 13) {
						processFilesForValidation();
						System.out.println("hello1");
						filecounter=0;
						count=0;
					}
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
