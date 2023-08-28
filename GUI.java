import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.*;
import java.io.File;
import java.awt.GridLayout;
import java.awt.MouseInfo;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.FileWriter;
import java.io.IOException;


public class GUI extends JFrame implements ActionListener{ // Manages all global variables (number nodes, tags, etc) and instantiates network

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// MAX NUMBER OF TAGS IS 10, BUT TWO TAGS ARE X AND Y COORDS. 
	// DEPLOY WITH https://www.webswing.org/en AHHHHHH ITS SO GOOD??
	
	
	JButton loadFromCD;
	JButton loadFromExt;
	JButton newNetwork;
    static String filePath;
	
	public GUI() 
	{	
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new GridLayout(0, 1));
        loadFromCD = new JButton("Load Network Data File from Current Directory");
        loadFromExt = new JButton("Select Network Data File");
        newNetwork = new JButton("Create New Network");
        loadFromCD.addActionListener(this);
        loadFromExt.addActionListener(this);
        newNetwork.addActionListener(this);

        add(loadFromCD);
        add(loadFromExt);
        add(newNetwork);
        pack();
        setVisible(true);
        setTitle("Affinity Network");
        setLocation(200,200);
        addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		        dispose();
		    }
		});
	}	

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loadFromExt) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(".")); //sets current directory
            FileNameExtensionFilter csvFilter = new FileNameExtensionFilter(
            	     "Comma Seperated Values files (*.csv)", "csv");
            fileChooser.setFileFilter(csvFilter);
            int response = fileChooser.showOpenDialog(null); //select file to open
            if (response == JFileChooser.APPROVE_OPTION) {
                filePath = fileChooser.getSelectedFile().getAbsolutePath();
                dispose();
                new Network(extractAllData());
            }
        }
        if (e.getSource() == loadFromCD) {
            filePath = "AllData.csv";
            dispose();
            new Network(extractAllData());
        }
        if (e.getSource() == newNetwork) {
        	JFrame popup = new JFrame("");
        	JPanel panel = new JPanel();
        	JLabel text = new JLabel("Enter new Network file name: ");
        	JTextField fileNameField = new JTextField(10);
            
            String[] invalids = {"/","\\",":","*","?","\"","<",">","|","\'"};
            
            // TODO: Implement removal of invalid characters
            JButton submit = new JButton(new AbstractAction("Create") {
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				filePath = fileNameField.getText().strip() + ".csv";
    				
    				File f = new File(filePath);
    				if(f.exists()) new Network(extractAllData());
    				else {
    					try {
							f.createNewFile();
							FileWriter fw = new FileWriter(f);
							fw.write("Name"
									+"\n###BREAK###"
									+"\n###BREAK###"
									+"\n###BREAK###"
									+"\n###BREAK###");
							fw.close();
							new Network(extractAllData());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}    					
    				}
    				popup.dispose();
    			}
    		});	
            panel.add(text);
        	panel.add(fileNameField);
            panel.add(submit);
            popup.add(panel);
    		popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    		popup.pack();
    		popup.setVisible(true);
    		popup.setLocation(MouseInfo.getPointerInfo().getLocation());
            dispose();
            
        }
    }
	
	public static void restartNetwork(ArrayList<ArrayList<String[]>> data) {
		System.out.println("\nNetwork refreshed!");
		new Network(data);
	}
	public static void writeData(ArrayList<ArrayList<String[]>> data) {
		FileWriter fr;
		try {
			fr = new FileWriter(filePath);
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
			fr.write("###BREAK###\n");
			for(String[] line : data.get(4)) {
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
			BufferedReader br = new BufferedReader(new FileReader(filePath)); 
			for(int i=0; i<5;i++) {
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
		System.out.println("\nNetwork refreshed!");
		new GUI();
	}

}