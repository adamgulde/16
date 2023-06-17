package aff.net;

public class Node {
	private String fName;
	private String lName;
	
	public Node(String fName, String lName, String[] tags) {
		new Node(fName, lName);
		// tags... 
	}
	public Node(String fName, String lName) {
		this.fName = fName;
		this.lName = lName;
	}

}
