package Model;

public class CommentNetworkEdge {

	private String commenter_1 = "";
	
	private String commenter_2 = "";
	
	private int total = 0; //total replies between these two commenters
	
	private String poliarty = ""; //pos; neg; obj;
	
	private double weight = 0.0; //weight of edge
	
	
	public CommentNetworkEdge(){
		
	}
	
	public CommentNetworkEdge(String commenter_1, String commenter_2){
		this.commenter_1 = commenter_1;
		this.commenter_2 = commenter_2;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getPoliarty() {
		return poliarty;
	}

	public void setPoliarty(String poliarty) {
		this.poliarty = poliarty;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getCommenter_1() {
		return commenter_1;
	}

	public String getCommenter_2() {
		return commenter_2;
	}
	
	
}
