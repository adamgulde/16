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
		new Network(extractAllData());
	}	
	public static void restartNetwork(ArrayList<ArrayList<String[]>> data) {
		new Network(data);
	}
	public static void writeData(ArrayList<ArrayList<String[]>> data) {
		FileWriter fr;
		try {
			fr = new FileWriter("AllData.csv");
			for(String[] line : data.get(0)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.write("###BREAK###\n");
			for(String[] line : data.get(1)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.write("###BREAK###\n");
			for(String[] line : data.get(2)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.write("###BREAK###\n");
			for(String[] line : data.get(3)) {
				for(String text : line) {
					fr.write(text + ",");
				}
				fr.write("\n");
			}
			fr.close(); 	
		} catch (IOException e) { e.printStackTrace(); }  
		
	}
	
	private ArrayList<ArrayList<String[]>> extractAllData() {
		ArrayList<ArrayList<String[]>> allData = new ArrayList<ArrayList<String[]>>();
		String line = "";  
		try   
		{  
			BufferedReader br = new BufferedReader(new FileReader("AllData.csv")); 
			for(int i=0; i<4;i++) {
				allData.add(new ArrayList<String[]>());
				while ((line = br.readLine()) != null)  
				{  
					if(line.startsWith("###BREAK###")) break;
					else {
						String[] lineData = line.split(",");    
						allData.get(i).add(lineData);
					}
				}
			}
			br.close(); 
		}   
		catch (IOException e) { e.printStackTrace(); }    
		return allData;
	}
	
	public static void main(String[] args) {
		new GUI();
	}

}
