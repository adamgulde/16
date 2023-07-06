package aff.net;
import java.util.ArrayList;
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.FileWriter;
import java.io.IOException;


public class GUI{ // Manages all global variables (number nodes, tags, etc) and instantiates network

	// MAX NUMBER OF TAGS IS 10, BUT TWO TAGS ARE X AND Y COORDS. 
	// DEPLOY WITH https://www.webswing.org/en AHHHHHH ITS SO GOOD??
	
	public GUI() 
	{	
		ArrayList<ArrayList<String[]>> data = new ArrayList<ArrayList<String[]>>();
		data.add(extractContactInfo());
		data.add(extractConnections());
		data.add(extractTags());
		data.add(extractNotes());
		new Network(data);
	}	
	public static void restartNetwork(ArrayList<ArrayList<String[]>> data) {
		new Network(data);
	}
	public static void writeData(ArrayList<ArrayList<String[]>> data) {
		FileWriter fr;
		try {
			fr = new FileWriter("ContactInfo.csv");
			for(String[] line : data.get(0)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.close();
			fr = new FileWriter("Connections.csv");
			for(String[] line : data.get(1)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.close(); 
			fr = new FileWriter("Tags.csv");
			for(String[] line : data.get(2)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.close();
			fr = new FileWriter("Notes.csv");
			for(String[] line : data.get(3)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.close(); 	
		} catch (IOException e) { e.printStackTrace(); }  
		
	}
	private ArrayList<String[]> extractContactInfo() {
		ArrayList<String[]> info = new ArrayList<String[]>();
		String line = "";  
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader("ContactInfo.csv")); 
			while ((line = br.readLine()) != null)     
			{  
				String[] contactData = line.split(",");    
				info.add(contactData);
			}  
			br.close(); 
		}   
		catch (IOException e) { e.printStackTrace(); }    
		return info;
	}
	private ArrayList<String[]> extractConnections() {
		ArrayList<String[]> connections = new ArrayList<String[]>();
		String line = "";  
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader("Connections.csv")); 
			while ((line = br.readLine()) != null)     
			{  
				String[] connectionData = line.split(",");    
				connections.add(connectionData);
			}  
			br.close();
		}   
		catch (IOException e) { e.printStackTrace(); }    
		return connections;
	}
	private ArrayList<String[]> extractTags() {
		ArrayList<String[]> tags = new ArrayList<String[]>();
		String line = "";  
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader("Tags.csv")); 
			while ((line = br.readLine()) != null)     
			{  
				String[] tagData = line.split(",");    
				tags.add(tagData);
			}  
			br.close();
		}   
		catch (IOException e) { e.printStackTrace(); }  
		return tags;
	}
	private ArrayList<String[]> extractNotes() {
		ArrayList<String[]> notes = new ArrayList<String[]>();
		return notes;
	}
	
	public static void main(String[] args) {
		new GUI();
	}

}
