package model;

import java.util.ArrayList;

public class CommentNetwork {
	
	private String articleName = ""; //the article name
	
	private ArrayList<String> nodeList = new ArrayList<String>();
	
	private ArrayList<CommentNetworkEdge> edgeList = new ArrayList<>();
	
	private int totalComments = 0;	//the number of total comments
	
	private int firstLevalComments = 0;	// the number of first level comments
	
	private int firstLevalIsolateComments = 0; // the number of first level comments with no replies
	
	private int replies = 0;
	
	private int totalCommenters = 0;
	
	private int isolateCommenters = 0;
	
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


	public String getArticleName() {
		return articleName;
	}


	public void setArticleName(String articleName) {
		this.articleName = articleName;
	}


	public int getFirstLevalComments() {
		return firstLevalComments;
	}


	public void setFirstLevalComments(int firstLevalComments) {
		this.firstLevalComments = firstLevalComments;
	}


	public int getReplies() {
		return replies;
	}


	public void setReplies(int replies) {
		this.replies = replies;
	}


	public int getFirstLevalIsolateComments() {
		return firstLevalIsolateComments;
	}


	public void setFirstLevalIsolateComments(int firstLevalIsolateComments) {
		this.firstLevalIsolateComments = firstLevalIsolateComments;
	}
	
	
}
