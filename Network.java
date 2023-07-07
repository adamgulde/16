import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.*;

public class Network {

	private final int MAP_WIDTH = 1280;
	private final int MAP_HEIGHT = 720;
	
	private ArrayList<String[]> info = new ArrayList<String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();
	private ArrayList<String[]> tags = new ArrayList<String[]>();
	private ArrayList<String[]> notes = new ArrayList<String[]>();
	private ArrayList<int[]> coords = new ArrayList<int[]>();
	
	boolean[] flags; // true is clicked / deleted
	
	private ArrayList<JFrame> windows = new ArrayList<JFrame>();
	int numNodes = 1;
	Random RNG = new Random();
	
	String[] prefixes = { "Name", "Email 1", "Email 2", "Email 3", "Cell Number", "Work Number", "LinkedIn",
			"Instagram 1", "Instagram 2", "Snapchat", "Discord", "Facebook Link", "Twitter Link", "Reddit", "Other 1",
			"Other 2" };
	

	public Network(ArrayList<ArrayList<String[]>> data) {
		
		info = data.get(0);
		connections = data.get(1);
		tags = data.get(2);
		notes = data.get(3);
		numNodes = info.size();
		setCoordsList(tags);
		flags = new boolean[numNodes]; // true is clicked / deleted
		
		JFrame frame = new JFrame("Network");
		windows.add(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenuBar(frame);

		NodePanel panel = new NodePanel();
		frame.add(panel);
		panel.setLayout(null);

		for (int i = 0; i < numNodes; i++) {
			panel.add(createNode(info.get(i)[0], info.get(i), coords.get(i)[0], coords.get(i)[1], panel));
		}
		
		frame.setSize(MAP_WIDTH, MAP_HEIGHT);
		
		for(int i = 0; i < numNodes; i++) {
			for(int j = 0; j < connections.get(i).length; j++) {
				frame.paint(null);
			}
		}
		
		frame.setVisible(true);
	}

	@SuppressWarnings("serial")
	public void createMenuBar(JFrame frame) {
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
						tags.add(new String[10]);
						for (int i = 0; i < infoArray.length; i++) {
							info.get(info.size()-1)[i] = infoArray[i].getText();
						}
						tags.get(info.size()-1)[0] = RNG.nextInt(1200) + "";
						tags.get(info.size()-1)[1] = RNG.nextInt(700) + "";
						popup.dispose();
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
//						connections.remove(j);
						tags.remove(j);
//						notes.remove(j);
						flags_.remove(j);
						j--;
						if(!checkFlags(flags_)) { // I don't like this workaround
							break;
						}
					}
				}
				GUI.restartNetwork(new ArrayList<>(Arrays.asList(info, connections, tags, notes)));
			}
		});
		JButton saveAndClose = new JButton(new AbstractAction("Save and Close") {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(JFrame f : windows) {
					f.dispose();
				}
				GUI.writeData(new ArrayList<>(Arrays.asList(info, connections, tags, notes)));
			}
		});
		JButton testPaint = new JButton(new AbstractAction("Testing Paint Function") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Paint Function");
				windows.add(popup);
				popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//				JPanel panel = new NodePanel();

//				popup.add(panel);
				defaultPopupBehavior(popup);
			}
		});					
		
		menuBar.add(addNodeButton);
		menuBar.add(removeNodeButton);
		menuBar.add(refreshNetwork);
		menuBar.add(saveAndClose);
		menuBar.add(testPaint);
		frame.setJMenuBar(menuBar);
	}
				

	public JButton createNode(String name, String[] nodeContactInfo, int x, int y, NodePanel frame) {
		JButton button = new JButton(new AbstractAction(name) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Choose Node action");
				windows.add(popup);
				int nodeID = info.indexOf(nodeContactInfo);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				
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
				JButton deleteNode = new JButton(new AbstractAction("Delete Node" + existingText(info.indexOf(nodeContactInfo))) {
					@Override
					public void actionPerformed(ActionEvent e) {
						JFrame popup2 = new JFrame("Confirmation");
						windows.add(popup2);			
						JPanel panel = new JPanel();
						JLabel text = new JLabel("Are you sure you want to delete this node?");
						JButton del = new JButton(new AbstractAction("Yes, delete") {
							@Override
							public void actionPerformed(ActionEvent e) {
								flags[info.indexOf(nodeContactInfo)] = true;
								popup2.dispose();
								popup.dispose();
							}
						});
						JButton keep = new JButton(new AbstractAction("No, keep") {
							@Override
							public void actionPerformed(ActionEvent e) {
								flags[info.indexOf(nodeContactInfo)] = false;
								popup2.dispose();
								popup.dispose();
							}
						});
						addComponentsToPanel(panel, new ArrayList<JComponent>(Arrays.asList(text, del, keep)));
						popup2.add(panel);
						defaultPopupBehavior(popup2);
					}
				});
				
				addComponentsToPanel(panel, new ArrayList<JComponent>(Arrays.asList(contactInfo, editNode, editConnections)));
				popup.add(panel);
				defaultPopupBehavior(popup);
			}
		});
		
		frame.appendCoordsList(createConnectionPairs(nameToID(name)), x, y);
		button.setLocation(x, y);
		button.setSize(button.getPreferredSize());
		button.setOpaque(true);
		button.setBackground(new Color(0, 213, 222));

		return button;
	} 
	public void popupContactInfo(String title, String[] nodeContactInfo) {
		JFrame popup = new JFrame(title);
		windows.add(popup);
		popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();

		JTextArea info = new JTextArea();
		info.setEditable(false);
		info.append("Contact Points");

		for (int i = 0; i < nodeContactInfo.length; i++) {
			if(!nodeContactInfo[i].equals(prefixes[i]) && !nodeContactInfo[i].equals("") && !nodeContactInfo[i].equals(" ")) {
				info.append("\n" + prefixes[i] + ": " + nodeContactInfo[i]);
			}
		}

		JScrollPane areaScrollPane = new JScrollPane(info);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(300, 300));

		panel.add(areaScrollPane);

		popup.add(panel);
		defaultPopupBehavior(popup);
	}
	public void popupEditNode(String[] nodeContactInfo) {
		JFrame popup = new JFrame("Edit Node Menu");
		windows.add(popup);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JTextField[] infoArray = new JTextField[prefixes.length];

		for (int i = 0; i < nodeContactInfo.length; i++) {
			if(nodeContactInfo[i] == " " || nodeContactInfo[i] == "") {
				infoArray[i] = new JTextField(prefixes[i]);
			}
			else infoArray[i] = new JTextField(nodeContactInfo[i]);
			panel.add(infoArray[i]);
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
				info.add(info.indexOf(nodeContactInfo), temp);
				info.remove(nodeContactInfo);
				popup.dispose();
			}
		});
		panel.add(submitInfo);

		popup.add(panel);
		defaultPopupBehavior(popup);
	}
	public void popupEditConnections(int nodeID) {
		JFrame popup = new JFrame("Edit Connections");
		windows.add(popup);
		popup.setVisible(true);
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
				connections.remove(nodeID);
				connections.add(nodeID, newConnections.toArray(new String[newConnections.size()])); 
				popup.dispose();
			}
		});
		panel.add(submitChanges);
		JScrollPane areaScrollPane = new JScrollPane(panel);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		popup.add(areaScrollPane);
		defaultPopupBehavior(popup);
	}
	public void defaultPopupBehavior(JFrame popup_) { // helper function to clean code
		popup_.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
		popup_.pack();
		popup_.setVisible(true);
		popup_.setAlwaysOnTop(true);
		popup_.setLocation(MouseInfo.getPointerInfo().getLocation());
	}

	public void addComponentsToPanel(JPanel panel_, ArrayList<JComponent> comps) { // helper function to clean code
		for(JComponent comp : comps) {
			panel_.add(comp);
		}
	}
	public String existingText(int id) {
		if(flags[id]) {
			return " (deleted)";
		}
		else return " (not deleted).";
	}
	public void setCoordsList(ArrayList<String[]> tagsList) {
		for(int i = 0; i < tagsList.size(); i++) {
			coords.add(new int[2]);
			for(int j = 0; j < 2; j++) {
				coords.get(i)[j] = Integer.parseInt(tagsList.get(i)[j]);
			}
		}		
	}
	public boolean checkFlags(ArrayList<Boolean> f) {
		for(boolean val : f) {
			if(val) {
				return true;
			}
		}
		return false;
	}
	public boolean checkNeighboringNodes(int nodeID) {
		return true;
	}
	public int nameToID(String name) { 
		for(int i = 0; i < info.size(); i++) {
			if(info.get(i)[0].equals(name)) return i;
		}
		System.out.println("Name not found...");
		return -1;
	}
	public String IDtoName(int id) {
		try {
			return info.get(0)[0];
		} catch (IndexOutOfBoundsException e) {
			System.out.println("ID not found...");
			return "N/A";
		}
		
	}
	private ArrayList<int[]> createConnectionPairs(int id) {
		ArrayList<int[]> connectionCoords = new ArrayList<int[]>();
		for(int i = 0; i<connections.get(id).length; i++) {
			connectionCoords.add(new int[2]);
			connectionCoords.get(i)[0] = Integer.parseInt(tags.get(nameToID(connections.get(id)[i]))[0]);
			connectionCoords.get(i)[1] = Integer.parseInt(tags.get(nameToID(connections.get(id)[i]))[1]);
		}
		return connectionCoords;
	}
}