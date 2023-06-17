package aff.net;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.*;

public class Network {

	private final int MAP_WIDTH = 1280;
	private final int MAP_HEIGHT = 720;
	
	String[][] info;
	String[][] connections;
	String[][] tags;
	String[][] notes;
	
	int numNodes = 1;
	Random RNG = new Random();
	
	String[] prefixes = { "Name", "Email 1", "Email 2", "Email 3", "Cell Number", "Work Number", "LinkedIn",
			"Instagram 1", "Instagram 2", "Snapchat", "Discord", "Facebook Link", "Twitter Link", "Reddit", "Other 1",
			"Other 2" };

	public Network(String[][][] data) {
		
		info = data[0];
		connections = data[1];
		tags = data[2];
		notes = data[3];
		
		numNodes = info.length;
		JFrame frame = new JFrame("Network");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenuBar(frame);

		Container pane = frame.getContentPane();
		pane.setLayout(null);

		for (int i = 0; i < info.length; i++) {
			if (i == 0) {
				pane.add(createNode("You", info[0], MAP_WIDTH / 2, MAP_HEIGHT / 2));
			} else
				pane.add(createNode(info[i][0], info[i], RNG.nextInt(1000) + 30, RNG.nextInt(600) + 30));
		}

		frame.setSize(MAP_WIDTH, MAP_HEIGHT);

		frame.setVisible(true);
	}

	public void createMenuBar(JFrame frame) {
		JMenuBar menuBar = new JMenuBar();
		menuBar.setOpaque(true);
		menuBar.setBackground(new Color(0, 165, 127));
		menuBar.setPreferredSize(new Dimension(MAP_WIDTH, 20));

		JButton addNodeButton = new JButton(new AbstractAction("Add Node") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Add Node Menu");
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
						String[] infoData = new String[infoArray.length];
						for (int i = 0; i < infoArray.length; i++) {
							infoData[i] = infoArray[i].getText();
						}
//						GUI.writeContactInfo(infoData);
						popup.dispose();
					}
				});
				panel.add(submitInfo);

				popup.add(panel);
				popup.pack();
				popup.setVisible(true);
				popup.setAlwaysOnTop(true);
				popup.setLocation(MouseInfo.getPointerInfo().getLocation());
			}
		});

		JButton removeNodeButton = new JButton(new AbstractAction("Remove Node") {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame("Remove Node");
				popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

				JLabel label = new JLabel("Choose node to remove:");
				panel.add(label);
				JButton[] buttons = new JButton[numNodes];
				for (int i = 0; i < numNodes; i++) {
//					buttons[i] = new JButton(nodes[i]);
					panel.add(buttons[i]);
				}

				JScrollPane areaScrollPane = new JScrollPane(panel);
				areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

				popup.add(areaScrollPane);
				popup.pack();
				popup.setVisible(true);
				popup.setAlwaysOnTop(true);

			}
		});

		menuBar.add(addNodeButton);
		menuBar.add(removeNodeButton);
		frame.setJMenuBar(menuBar);
	}

	public JButton createNode(String title, String[] nodeContactInfo, int x, int y) {

		JButton button = new JButton(new AbstractAction(title) {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame popup = new JFrame(title);
				popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				JPanel panel = new JPanel();

				JTextArea info = new JTextArea();
				info.setEditable(false);
				info.append("Contact Points\n");

				for (int i = 0; i < nodeContactInfo.length; i++) {
					info.append(prefixes[i] + " : " + nodeContactInfo[i] + "\n");
				}

				JScrollPane areaScrollPane = new JScrollPane(info);
				areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				areaScrollPane.setPreferredSize(new Dimension(250, 250));

				panel.add(areaScrollPane);

				popup.add(panel);
				popup.pack();
				popup.setVisible(true);
				popup.setAlwaysOnTop(true);
				popup.setLocation(MouseInfo.getPointerInfo().getLocation());
			}
		});
		button.setBounds(new Rectangle(x, y, 100, 30));
		button.setOpaque(true);
		button.setBackground(new Color(0, 213, 222));

		return button;
	}

}