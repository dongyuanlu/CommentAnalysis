package Model;

import java.util.ArrayList;

public class CommentNetwork {
	
	String articleName = ""; //the article name
	
	ArrayList<String> nodeList = new ArrayList<String>();
	
	ArrayList<CommentNetworkEdge> edgeList = new ArrayList<>();
	
	int totalComments = 0;
	
	int totalCommenters = 0;
	
	int isolateCommenters = 0;
	
	public CommentNetwork(String articleName){
		this.articleName = articleName;
	}

	
	public void addNode(String node){
		this.nodeList.add(node);
	}
	
	public void addEdge(CommentNetworkEdge edge){
		this.edgeList.add(edge);
	}


	public ArrayList<String> getNodeList() {
		return nodeList;
	}


	public void setNodeList(ArrayList<String> nodeList) {
		this.nodeList = nodeList;
	}


	public ArrayList<CommentNetworkEdge> getEdgeList() {
		return edgeList;
	}


	public void setEdgeList(ArrayList<CommentNetworkEdge> edgeList) {
		this.edgeList = edgeList;
	}


	public int getTotalComments() {
		return totalComments;
	}


	public void setTotalComments(int totalComments) {
		this.totalComments = totalComments;
	}


	public int getTotalCommenters() {
		return totalCommenters;
	}


	public void setTotalCommenters(int totalCommenters) {
		this.totalCommenters = totalCommenters;
	}


	public int getIsolateCommenters() {
		return isolateCommenters;
	}


	public void setIsolateCommenters(int isolateCommenters) {
		this.isolateCommenters = isolateCommenters;
	}
	
	
}
