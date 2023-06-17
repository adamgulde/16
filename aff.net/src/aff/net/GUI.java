package aff.net;
import java.util.ArrayList;
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors


public class GUI{ // Manages all global variables (number nodes, tags, etc) and instantiates network
	
	private String[][][] data = new String[4][][];
	
	
	private ArrayList<String[]> info = new ArrayList<String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();
	private ArrayList<String[]> tags = new ArrayList<String[]>();
	private ArrayList<String[]> notes = new ArrayList<String[]>();
			
	public GUI() 
	{	
		data[0] = extractContactInfo();
		data[1] = extractConnections();
		data[2] = extractTags();
		data[3] = extractNotes();
		new Network(data);
	}	
	private String[][] extractContactInfo() {
		String csvFileAddress = "" + System.getProperty("user.dir") + "\\src\\aff\\net\\ContactInfo.csv";// will need to be adjusted for deployment
		String line = "";  
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader(csvFileAddress)); 
			while ((line = br.readLine()) != null)     
			{  
				String[] contactData = line.split(",");    
				info.add(contactData);
			}  
			br.close(); 
		}   
		catch (IOException e)   
		{  
			e.printStackTrace();  
		}   
		return info.toArray(new String[info.size()][]);
	}
	private String[][] extractConnections() {
		String csvFileAddress = "" + System.getProperty("user.dir") + "\\src\\aff\\net\\Connections.csv";// will need to be adjusted for deployment
		String line = "";  
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader(csvFileAddress)); 
			while ((line = br.readLine()) != null)     
			{  
				String[] connectionData = line.split(",");    
				connections.add(connectionData);
			}  
			br.close();
		}   
		catch (IOException e)   
		{  
			e.printStackTrace();  
		}   
		return connections.toArray(new String[connections.size()][]);
	}
	private String[][] extractTags() {
		return new String[0][];
	}
	private String[][] extractNotes() {
		return new String[0][];
	}
	
//	
//	public static void writeContactInfo(String[] newContactInfo) { // update to writeInfo() - updates all CSV files AT PROGRAM CLOSE
//		try {
//			FileWriter myWriter = new FileWriter(csvFileAddress, true);
//			for(String line : newContactInfo) {
//				myWriter.write(line + ",");
//			}
//			myWriter.write("\n");
//			myWriter.close();
//		} catch (IOException e) {
//			System.out.println("An error occurred.");
//			e.printStackTrace();
//		}
//	}
	
	public static void main(String[] args) {
		new GUI();
	}

}
