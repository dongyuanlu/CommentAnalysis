package model;

public class CommenterLink {
	
	private String commenter_1 = "";
	
	private String commenter_2 = "";
	
	private int totalComments = 0; //total replies between these two commenters
	
	private int polarity = 0;//pos 1; neg -1; obj 0;
	
	private double weight = 0.0; //weight of edge
	
	private int indexCommenter_1;
	
	private int indexCommenter_2;
	
	
	public CommenterLink(){
		
	}
	
	public CommenterLink(String commenter_1, String commenter_2){
		this.commenter_1 = commenter_1;
		this.commenter_2 = commenter_2;
	}

	
	
	
	
	public int getIndexCommenter_1() {
		return indexCommenter_1;
	}

	public void setIndexCommenter_1(int indexCommenter_1) {
		this.indexCommenter_1 = indexCommenter_1;
	}

	public int getIndexCommenter_2() {
		return indexCommenter_2;
	}

	public void setIndexCommenter_2(int indexCommenter_2) {
		this.indexCommenter_2 = indexCommenter_2;
	}

	public int getTotalComments() {
		return totalComments;
	}

	public void setTotalComments(int total) {
		this.totalComments = total;
	}


	public int getPolarity() {
		return polarity;
	}

	public void setPolarity(int polarity) {
		this.polarity = polarity;
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
