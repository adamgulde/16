import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

public class Network {

	private final int MAP_WIDTH = 1920;
	private final int MAP_HEIGHT = 1080;
	
	private ArrayList<String[]> info = new ArrayList<String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();
	private ArrayList<String[]> tags = new ArrayList<String[]>();
	private ArrayList<String[]> notes = new ArrayList<String[]>();
	private ArrayList<Point> coords = new ArrayList<Point>();
	
	private boolean[] flags; // true is clicked / deleted
	
	private ArrayList<JButton> nodes = new ArrayList<JButton>();
	private ArrayList<JFrame> windows = new ArrayList<JFrame>();
	private ArrayList<ConnectionLine> conLines = new ArrayList<ConnectionLine>();
	private ArrayList<int[]> connectionIDs = new ArrayList<int[]>();
	
	private int numNodes = 1;
	private Random RNG = new Random();
	private NodePanel truePanel;
	
	private String[] prefixes = {}; // { "Name", "Email 1", "Email 2", "Email 3", "Cell Number", "Work Number", "LinkedIn",
//			"Instagram 1", "Instagram 2", "Snapchat", "Discord", "Facebook Link", "Twitter Link", "Reddit", "Other 1",
//			"Other 2" };
	ArrayList<String[]> prefixList;
	public Network(ArrayList<ArrayList<String[]>> data) {
		
		prefixes = data.get(0).get(0);
		info = data.get(1);
		connections = data.get(2);
		tags = data.get(3);
		notes = data.get(4);
		numNodes = info.size();
		setCoordsList(tags);
		flags = new boolean[numNodes]; // true is clicked / deleted
		prefixList = new ArrayList<>(); prefixList.add(prefixes); // dumb because of the structure I chose
		
		JFrame frame = new JFrame("Network");
		windows.add(frame);
		createMenuBar(frame);
		NodePanel panel = new NodePanel(this);
		truePanel = panel;
		panel.setLayout(null);
		for (int i = 0; i < numNodes; i++) {
			nodes.add(createNode(info.get(i)[0], info.get(i), connections.get(i), notes.get(i), coords.get(i).x, coords.get(i).y));
			panel.add(nodes.get(i));
		}
		relocateNodes();
		frame.add(panel);
		
		frame.setSize(new Dimension(MAP_WIDTH/2, MAP_HEIGHT/2));
		frame.setVisible(true);
		frame.setLocation(200,200);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	confirmExit();
		        closeUpperWindows();
		    }
		});
	}

	@SuppressWarnings("serial")
	private void createMenuBar(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(new Color(0, 165, 127));
		menuBar.setPreferredSize(new Dimension(MAP_WIDTH, 20));

		JButton addNodeButton = new JButton(new AbstractAction("Add Node") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Add Node Menu");
				windows.add(popup);
				popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

				JTextField[] infoArray = new JTextField[prefixes.length];

				for (int i = 0; i < infoArray.length; i++) {
					infoArray[i] = new JTextField(prefixes[i]);
					panel.add(infoArray[i]);
				}

				JButton submitInfo = new JButton(new AbstractAction("Submit Info") {
					@Override
					public void actionPerformed(ActionEvent e) {
						info.add(new String[infoArray.length]);
						tags.add(new String[10]); // SETS TAG LIMIT
						notes.add(new String[]{"This is a note. Add relevant information about your contact here."});
						for (int i = 0; i < infoArray.length; i++) {
							info.get(info.size()-1)[i] = infoArray[i].getText();
						}
						tags.get(info.size()-1)[0] = RNG.nextInt(1200) + "";
						tags.get(info.size()-1)[1] = RNG.nextInt(600) + "";
						popup.dispose();
						popupNewConnections(info.size()-1);
					}
				});
				panel.add(submitInfo);

				popup.add(panel);
				defaultPopupBehavior(popup);
			}
		});

		JButton removeNodeButton = new JButton(new AbstractAction("Remove Node") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Remove Node");
				windows.add(popup);
				popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

				JLabel label = new JLabel("Choose node to remove:");
				panel.add(label);
				
				JButton[] buttons = new JButton[numNodes];
				for (int i = 0; i < numNodes; i++) {
					final int n = i;
					
					buttons[i] = new JButton(new AbstractAction(info.get(i)[0] + existingText(i)) {
						@Override
						public void actionPerformed(ActionEvent e) {
							flags[n] = !flags[n];
							buttons[n].setText(info.get(n)[0] + existingText(n));
						}
					});
					panel.add(buttons[i]);
				}
				
				JScrollPane areaScrollPane = new JScrollPane(panel);
				areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				areaScrollPane.setPreferredSize(new Dimension(200, 300));

				popup.add(areaScrollPane);
				defaultPopupBehavior(popup);

			}
		});
		
		JButton editPrefixes = new JButton(new AbstractAction("Set Default Contact Information") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Edit Contact Defaults");
				windows.add(popup);
				popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				
				JLabel text = new JLabel("Enable or disable contact information defaults.");
				JLabel text2 = new JLabel("Caution: Disabling defaults will delete contact data stored.");
				panel.add(text);
				panel.add(text2);
				
				JCheckBox[] checkboxes = new JCheckBox[prefixes.length];
				for (int i = 0; i < prefixes.length; i++) {
					checkboxes[i] = new JCheckBox();
					checkboxes[i].setText(prefixes[i]);
					checkboxes[i].setSelected(true);
					panel.add(checkboxes[i]);
				}
				ArrayList<JTextField> prefixTFs = new ArrayList<JTextField>();
				JButton addPrefix = new JButton(new AbstractAction("Add default") {
					@Override
					public void actionPerformed(ActionEvent e) {
						JTextField prefixTextField = new JTextField();
						prefixTFs.add(prefixTextField);
						panel.add(prefixTextField);
						popup.pack();
					}
				});
				JButton saveAndClose = new JButton(new AbstractAction("Save and Close") {
					@Override
					public void actionPerformed(ActionEvent e) {
						ArrayList<Integer> pflags = new ArrayList<Integer>();
						ArrayList<String> newPrefixes = new ArrayList<String>();
						for(int i=0;i<checkboxes.length;i++) {
							if(checkboxes[i].isSelected()) {
								newPrefixes.add(prefixes[i]);
							} 
							else pflags.add(i);
						}
						for(JTextField tf : prefixTFs) {
							if(!tf.getText().equals("") && !tf.getText().isEmpty() && !tf.getText().isBlank())
								newPrefixes.add(tf.getText());
						}
						prefixes = newPrefixes.toArray(new String[newPrefixes.size()]);
						for(int i = 0; i < info.size(); i++) {
							ArrayList<String> tempArr = new ArrayList<String>(); 
							tempArr.addAll(Arrays.asList(info.get(i)));
							for(int j = tempArr.size()-1; j>0; j--) {
								if(pflags.contains(j)) {
									tempArr.remove(j);
								}
								
							}
							tempArr.toArray(info.get(i));
						}
						prefixList.set(0, prefixes);
						popup.dispose();
					}
				});	
				addComponentsToPanel(panel, new ArrayList<JComponent>(Arrays.asList(addPrefix, saveAndClose)));
				popup.add(panel);
				defaultPopupBehavior(popup);
			}
		});
		
		JButton refreshNetwork = new JButton(new AbstractAction("Refresh Network") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(JFrame f : windows) {
					f.dispose();
				}
				ArrayList<Boolean> flags_ = new ArrayList<Boolean>();
				for(boolean f : flags) {
					flags_.add(f);
				}
				for(int j = 0; j < numNodes; j++) {
					if(flags_.get(j)) {
						info.remove(j);
						connections.remove(j);
						tags.remove(j);
						notes.remove(j);
//						coords.remove(j); Not necessary because this is extracted from the tags arraylist from CURRENT network instance
						flags_.remove(j);
						j--;
						if(!checkFlags(flags_)) { // I don't like this workaround
							break;
						}
					}
				}
				GUI.restartNetwork(new ArrayList<>(Arrays.asList(prefixList, info, connections, tags, notes)));
			}
		});
		JButton saveAndClose = new JButton(new AbstractAction("Save and Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(JFrame f : windows) {
					f.dispose();
				}
				GUI.writeData(new ArrayList<>(Arrays.asList(prefixList, info, connections, tags, notes)));
			}
		});			
		menuBar.add(addNodeButton);
		menuBar.add(removeNodeButton);
		menuBar.add(editPrefixes);
		menuBar.add(refreshNetwork);
		menuBar.add(saveAndClose);
		frame.setJMenuBar(menuBar);
	}
			
	private void relocateNodes() {
		conLines.clear();
		for(int i=0;i<numNodes;i++) {
			for(int ID : connectionIDs.get(i)) {
				conLines.add(new ConnectionLine(coords.get(i), coords.get(ID)));
			}
			nodes.get(i).setLocation(coords.get(i).x - nodes.get(i).getPreferredSize().width / 2, 
					coords.get(i).y - nodes.get(i).getPreferredSize().height / 2);
		}
		truePanel.retrieveLinesList(conLines);
	}
	public void relocateNodes(int dx, int dy) {
		conLines.clear();
		for(int i=0;i<numNodes;i++) {
			coords.get(i).x += dx;
			coords.get(i).y += dy;
			for(int ID : connectionIDs.get(i)) {
				conLines.add(new ConnectionLine(coords.get(i), coords.get(ID)));
			}
			nodes.get(i).setLocation(coords.get(i).x - nodes.get(i).getPreferredSize().width / 2, 
					coords.get(i).y - nodes.get(i).getPreferredSize().height / 2);
		}
		truePanel.retrieveLinesList(conLines);
	}
	private JButton createNode(String name, String[] nodeContactInfo, String[] connections, String[] notes, int x, int y) {
		int myID = nameToID(name);
		JButton button = new JButton(new AbstractAction(name) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame(name);
				windows.add(popup);
				int nodeID = info.indexOf(nodeContactInfo);
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(0, 1));
				
				JButton contactInfo = new JButton(new AbstractAction("View Contact Info") {
					@Override
					public void actionPerformed(ActionEvent e) {
						popupContactInfo(name, nodeContactInfo);
					}
				});
				JButton editNode = new JButton(new AbstractAction("Edit Contact Info") {
					@Override
					public void actionPerformed(ActionEvent e) {
						popupEditNode(nodeContactInfo);
					}
				});
				JButton editConnections = new JButton(new AbstractAction("Edit Connections") {
					@Override
					public void actionPerformed(ActionEvent e) {
						popupEditConnections(nodeID);
					}
				});
				JButton viewNotes = new JButton(new AbstractAction("View Notes") {
					@Override
					public void actionPerformed(ActionEvent e) {
						popupNotes(myID);
					}
				});
				
				addComponentsToPanel(panel, new ArrayList<JComponent>(Arrays.asList(contactInfo, editNode, editConnections, viewNotes)));
				popup.add(panel);
				defaultPopupBehavior(popup);
			}
		});
		connectionIDs.add(namesToIDs(connections));
		for(int ID : connectionIDs.get(myID)) {
			conLines.add(new ConnectionLine(coords.get(myID), coords.get(ID)));
		}
		button.setLocation(x - button.getPreferredSize().width / 2, y - button.getPreferredSize().height / 2);
		button.setSize(button.getPreferredSize());
		button.setOpaque(true);
		button.setBackground(new Color(0, 213, 222));

		return button;
	} 
	private void popupContactInfo(String title, String[] nodeContactInfo) {
		JFrame popup = new JFrame(title);
		windows.add(popup);
		popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();

		JTextArea info = new JTextArea();
		info.setEditable(false);
		info.append("Contact Points");

		if(nodeContactInfo.length < prefixes.length) {
			for (int i = 0; i < nodeContactInfo.length; i++) {
				if(!nodeContactInfo[i].equals(prefixes[i]) && !nodeContactInfo[i].equals("") && !nodeContactInfo[i].equals(" ")) {
					info.append("\n" + prefixes[i] + ": " + nodeContactInfo[i]);
				}
			}
		} else {
			for (int i = 0; i < prefixes.length; i++) {
				if(!nodeContactInfo[i].equals(prefixes[i]) && !nodeContactInfo[i].equals("") && !nodeContactInfo[i].equals(" ")) {
					info.append("\n" + prefixes[i] + ": " + nodeContactInfo[i]);
				}
			}
		}
		
		JScrollPane areaScrollPane = new JScrollPane(info);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(300, 300));

		panel.add(areaScrollPane);

		popup.add(panel);
		defaultPopupBehavior(popup);
	}
	private void popupEditNode(String[] nodeContactInfo) {
		JFrame popup = new JFrame("Edit Node Menu");
		windows.add(popup);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JTextField[] infoArray = new JTextField[prefixes.length];
		if(nodeContactInfo.length < prefixes.length) {
			for (int i = 0; i < nodeContactInfo.length; i++) {
				if(nodeContactInfo[i] == " " || nodeContactInfo[i] == "") {
					infoArray[i] = new JTextField(prefixes[i]);
				}
				else infoArray[i] = new JTextField(nodeContactInfo[i]);
				panel.add(infoArray[i]);
			}
		} else {
			for (int i = 0; i < prefixes.length; i++) {
				if(nodeContactInfo[i] == " " || nodeContactInfo[i] == "") {
					infoArray[i] = new JTextField(prefixes[i]);
				}
				else infoArray[i] = new JTextField(nodeContactInfo[i]);
				panel.add(infoArray[i]);
			}
		}
		
		for(int i = nodeContactInfo.length; i<prefixes.length; i++) {
			infoArray[i] = new JTextField(prefixes[i]);
			panel.add(infoArray[i]);
		}
		
		JButton submitInfo = new JButton(new AbstractAction("Submit Info") {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] temp = new String[prefixes.length];
				for (int i = 0; i < infoArray.length; i++) {
					temp[i] = infoArray[i].getText();
				}
				info.set(info.indexOf(nodeContactInfo), temp);
				popup.dispose();
			}
		});
		panel.add(submitInfo);

		popup.add(panel);
		defaultPopupBehavior(popup);
	}
	private void popupNotes(int nodeID) {
		JFrame popup = new JFrame("Notes");
		windows.add(popup);
		JPanel panel = new JPanel();
		JTextArea noteArea;
		if(notes.get(nodeID)[0]!=null) { // Crashes anyway if empty- initialize new nodes with placeholder data
			noteArea = new JTextArea("", 9, 16);
			for(String line : notes.get(nodeID)) {
				noteArea.append(line + "\n");
			}
		} else {
			noteArea = new JTextArea("This is a note. Add relevant information about your contact here.", 9, 16);
		}
		noteArea.setLineWrap(true);
		noteArea.setWrapStyleWord(true);
		panel.add(noteArea);
		JScrollPane areaScrollPane = new JScrollPane(panel);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		popup.add(areaScrollPane);
		defaultPopupBehavior(popup);
		popup.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	notes.set(nodeID, noteArea.getText().split("\n"));
				popup.dispose();
		    }
		});  
	}
	private void popupEditConnections(int nodeID) {
		JFrame popup = new JFrame("Edit Connections");
		windows.add(popup);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
	
		ArrayList<JCheckBox> myConnections = new ArrayList<JCheckBox>();
		JLabel text = new JLabel("Select connections");
		panel.add(text);
		for (int i = 0; i < info.size(); i++) { 
			// I can see this bit getting inefficient 
			// but I didnt want to declare a global var
			if(!info.get(i)[0].equals(info.get(nodeID)[0])) {
				myConnections.add(new JCheckBox(info.get(i)[0]));
				panel.add(myConnections.get(i));
			}
			else {
				myConnections.add(new JCheckBox(info.get(i)[0]));
				myConnections.get(i).setEnabled(false);
				panel.add(myConnections.get(i));
			}
		}
		for(String connText : connections.get(nodeID)) {
			myConnections.forEach( (element) -> { // gross but works
				if(element.getText().equals(connText)) element.setSelected(true);
			}); // after looking at it for 
				// a minute I realize this is just 
				// the double for loop I was 
				// trying to avoid using..
		}
		JButton submitChanges = new JButton(new AbstractAction("Save Connections") {
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> newConnections = new ArrayList<String>();
				myConnections.forEach( (element) -> { 
					if(element.isSelected()) {
						newConnections.add(element.getText());
					}
				});  
				connections.set(nodeID, newConnections.toArray(new String[newConnections.size()])); 
				popup.dispose();
			}
		});
		panel.add(submitChanges);
		JScrollPane areaScrollPane = new JScrollPane(panel);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		popup.add(areaScrollPane);
		defaultPopupBehavior(popup);
	}
	private void popupNewConnections(int nodeID) {
		JFrame popup = new JFrame("Edit Connections");
		windows.add(popup);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
//		boolean hasAtLeastOneConnection = false;
		ArrayList<JCheckBox> myConnections = new ArrayList<JCheckBox>();
		JLabel text = new JLabel("Select connections");
		panel.add(text);
		for (int i = 0; i < info.size(); i++) { 
			if(!info.get(i)[0].equals(info.get(nodeID)[0])) {
				myConnections.add(new JCheckBox(info.get(i)[0]));
				panel.add(myConnections.get(i));
			}
			else {
				myConnections.add(new JCheckBox(info.get(i)[0]));
				myConnections.get(i).setEnabled(false);
				panel.add(myConnections.get(i));
			}
		}
		JButton submitChanges = new JButton(new AbstractAction("Save Connections") {
//			boolean f = hasAtLeastOneConnection;
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> newConnections = new ArrayList<String>();
				myConnections.forEach( (element) -> { 
					if(element.isSelected()) {
						newConnections.add(element.getText());
//						f = true;
					}
				});  
				connections.add(nodeID, newConnections.toArray(new String[newConnections.size()])); 
				popup.dispose();
//				if(f) {
//					connections.add(nodeID, newConnections.toArray(new String[newConnections.size()])); 
//					popup.dispose();
//				} else {
//					panel.add(new JLabel("Please add at least one connection"));
//					popup.pack();
//				}

			}
		});
		panel.add(submitChanges);
		JScrollPane areaScrollPane = new JScrollPane(panel);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		popup.add(areaScrollPane);
		defaultPopupBehavior(popup);
	}
	private void defaultPopupBehavior(JFrame popup_) { // helper function to clean code
		popup_.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		popup_.pack();
		popup_.setVisible(true);
		popup_.setAlwaysOnTop(true);
		popup_.setLocation(MouseInfo.getPointerInfo().getLocation());
	}
	private void defaultPopupBehavior(JFrame popup_, int num) { 
		popup_.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		        closeUpperWindows(windows.indexOf(popup_)+1);
		    }
		});
		popup_.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		popup_.pack();
		popup_.setVisible(true);
		popup_.setAlwaysOnTop(true);
		popup_.setLocation(MouseInfo.getPointerInfo().getLocation());
	}
	

	private void addComponentsToPanel(JPanel panel_, ArrayList<JComponent> comps) { // helper function to clean code
		for(JComponent comp : comps) {
			panel_.add(comp);
		}
	}
	private String existingText(int id) {
		if(flags[id]) {
			return " (deleted)";
		}
		else return " (not deleted).";
	}
	private void setCoordsList(ArrayList<String[]> tagsList) {
		for(int i = 0; i < tagsList.size(); i++) {
			coords.add(new Point());
			coords.get(i).y = Integer.parseInt(tagsList.get(i)[1]);
			coords.get(i).x = Integer.parseInt(tagsList.get(i)[0]);
		}		
	}
	private Point getHighestCoords() {
		Point highest = new Point(0,0);
		for(Point arr : coords) {
			if(arr.x>highest.x) highest.x = arr.x+60;
			if(arr.y>highest.y) highest.y = arr.y+60;
		}
		return highest;
	}
	private boolean checkFlags(ArrayList<Boolean> f) {
		for(boolean val : f) {
			if(val) {
				return true;
			}
		}
		return false;
	}
	private boolean checkNeighboringNodes(int nodeID) {
		return true;
	}
	private int nameToID(String name) { 
		for(int i = 0; i < info.size(); i++) {
			System.out.println(info.get(i)[0]);
			if(info.get(i)[0].equals(name)) return i;
		}
		System.out.println("Name not found...");
		return -1;
	}
	private String IDtoName(int id) {
		try {
			return info.get(id)[0];
		} catch (IndexOutOfBoundsException e) {
			System.out.println("ID not found...");
			return "N/A";
		}
	}
	
	/* Instead of using the looping "nameToID" function throughout
	 * execution, this function will be run ONCE per node and will
	 * return a usable list of IDs for each person in node's 
	 * connection list. 
	 */
	private int[] namesToIDs(String[] names) { 
		int[] translatedIDs = new int[names.length];
		for(int i=0;i<names.length;i++) {
			translatedIDs[i] = nameToID(names[i]);
			/*
			 * TODO Current issue: When no connections are selected (edit connection -> remove all connections) 
			 * Program will crash due to it returning -1 as the empty name, and -1 has no ID associated. 
			 * 8/26/23
			 */
		}
		return translatedIDs;
	}
	public ArrayList<Point> getCoordsList() {
		return coords;
	}
	public Point getNodeLocation(int ID) {
		return new Point(coords.get(ID).x, coords.get(ID).y);
	}
	public Point getNodeLocation(String name) {
		return new Point(coords.get(nameToID(name)).x, coords.get(nameToID(name)).y);
	}
	/* Deletes excess windows that have appeared 
	 * and need to be removed.
	 */
	public void closeUpperWindows() {
		for(int i = 1; i < windows.size(); i++) {
			windows.get(i).dispose();
			windows.remove(i);
			i--;
		}
	}
	public void closeUpperWindows(int start) {
		for(int i = start; i < windows.size(); i++) {
			windows.get(i).dispose();
			windows.remove(i);
			i--;
		}
	}
	private void closeUpperWindows(int start, int winID) {
		for(int i = start; i == winID; i++) {
			windows.get(i).dispose();
			windows.remove(i);
			i--;
		}
	}
	private void confirmExit() {
		System.out.println("TO BE IMPLEMENTED: Are you sure you want to exit?");
	}
}