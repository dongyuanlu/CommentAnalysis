package uibe.ldy.reddit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import model.CommenterLink;
import model.RedditComment;
import util.Combine;
import util.Counter;
import util.SQLUtil;

public class CommenterNetwork {
	
	private String sentimentName = "sentiment_1";
	private long WEEK = 604800000;
	private int networkNum = 10;
	
	private ArrayList<Network> networkList;
	private String articleName; //e.g., "t3_1zgwte"
	private int index;
	
	HashSet<String> allCommenterSet;
	HashSet<String> networkCommenterSet;
	HashSet<String> firstLevelCommentsHasReply;
	
	CalculateEdgeAttribute calculater;
	ReadReddit reader;
	SQLUtil sql;
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param articleName
	 */
	public CommenterNetwork(String articleName){
		index = 0;
		this.articleName = articleName;
		this.networkList = new ArrayList<Network>();

		allCommenterSet = new HashSet<>();
		networkCommenterSet = new HashSet<>();
		firstLevelCommentsHasReply = new HashSet<>();
		
		calculater = new CalculateEdgeAttribute();
		reader = new ReadReddit();
		sql = new SQLUtil(RedditConfig.database);
	}
	
	
	public static void main(String[] args){
		CommenterNetwork n = new CommenterNetwork("t3_1zgwte");
		n.generateEvolvingNetworkList();
		n.writeNetworkAttibutes();
//		n.writeCommenterLinks();
	}
	
	

	/**
	 * 
	 * Generate evolving temporal network list
	 * Interval by WEEK
	 * 
	 */
	public void generateEvolvingNetworkList(){
		long startTime = reader.startTime(articleName);
		long endTime = reader.endTime(articleName);
		
		//calculate the evolving temporal window
		long interval;
		if(networkNum <= 0){
			interval = WEEK;
		}else{
			interval = (reader.avgTime(articleName) - startTime) / networkNum;
		}
		
		long midTime = startTime + interval;
		
		while(midTime < endTime && index < 10){
			generateNetwork(startTime, midTime);
			System.out.println(index);
			
			index ++;
			midTime += interval;
		}
		System.out.println(index);
		generateNetwork(startTime, endTime);
	}
	
	

	/**
	 * 
	 * Generate one Network, based on comments between startTime and endTime
	 * ADD this Network into networkList
	 * 
	 * @param startTime: created_utc of startTime 
	 * @param endTime: created_utc of endTime 
	 */
	public void generateNetwork(long startTime, long endTime){
		Network network = new Network();
		
		network.setIndex(index);
		
		allCommenterSet.clear();
		networkCommenterSet.clear();
		
		Counter counterPolarity = new Counter();
		Counter counterNum = new Counter();
		Counter countWeight = new Counter();
		
		//Read comments between startTime and endTime
		String condition = "AND created_utc>=" + startTime + " AND created_utc<=" + endTime + " ORDER BY created_utc DESC";
		HashMap<String, RedditComment> commentMap = reader.readCommentsByArticleName(articleName, condition, sentimentName);
		int firstLevel = 0;
		
		//Generate commenter links
		Iterator<String> iter = commentMap.keySet().iterator();
		while(iter.hasNext()){
			String commentName = iter.next();
			RedditComment comment = commentMap.get(commentName);
			//if comment to article, continue
			if(comment.getParent_id().equals(comment.getLink_id())){ 
				allCommenterSet.add(comment.getAuthor());
				firstLevel++;
				continue;
			}
			
			//get commenter and replyto_commenter
			String commenter_1 = comment.getAuthor();
			String commenter_2 = commentMap.get(comment.getParent_id()).getAuthor(); //get replyto_author
			networkCommenterSet.add(commenter_1);
			networkCommenterSet.add(commenter_2);
			allCommenterSet.add(commenter_1);
			allCommenterSet.add(commenter_2);
			
			//get first level comments which has replies
			if(commentMap.get(comment.getParent_id()).getParent_id().equals(comment.getLink_id())){
				firstLevelCommentsHasReply.add(comment.getParent_id());
			}
			
			
			//Count num, polarity, weight
			String commenterPair = Combine.combineTwoStr(commenter_1,commenter_2);
			counterNum.counter(commenterPair);
			counterPolarity.counter(commenterPair, calculater.getPolarityFromComment(comment));
			int rank1 = comment.getRank();
			int rank2 = commentMap.get(comment.getParent_id()).getRank();
			countWeight.counter(commenterPair, calculater.getWeightFromCommentRank(rank1, rank2));
		}
		
		//ADD commenter links into network
		HashMap<String, Integer> commenterPolarityMap = counterPolarity.getStrHashMap(); //record edge-polarity
		HashMap<String, Integer> commenterNumMap = counterNum.getStrHashMap();	//record edge-appearNum
		HashMap<String, Double> commenterWeightMap = countWeight.getStrDoubleHashMap(); //record edge-weight
		Iterator<String> iterC = commenterPolarityMap.keySet().iterator();
		while(iterC.hasNext()){
			
			String commenterPair = iterC.next();
			int pol = commenterPolarityMap.get(commenterPair);
			String[] commenters = Combine.splitTwoStr(commenterPair);
			CommenterLink link = new CommenterLink(commenters[0],commenters[1]);
			
			link.setPolarity(pol); ////set polarity of edge
			
			link.setTotalComments(commenterNumMap.get(commenterPair)); //set total comments count between these two commenters
			
			link.setWeight(commenterWeightMap.get(commenterPair));
			
			network.addLink(link);
		}
		
		//ADD commenter network attributes
		
		network.setTotalCommenters(allCommenterSet.size());
		network.setIsolateCommenters(allCommenterSet.size() - networkCommenterSet.size());
		network.setTotalComments(commentMap.size());
		network.setFirstLevalComments(firstLevel);
		network.setFirstLevalIsolateComments(firstLevel - firstLevelCommentsHasReply.size());
		network.setReplies(network.getTotalComments() - network.getFirstLevalComments());
		
		networkList.add(network);
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////
	/////////////////////Write Network Into Database///////////////////
	///////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * WRITE attribute part of commenter network list into database
	 * 
	 */
	public void writeNetworkAttibutes(){
		
		String query = "INSERT IGNORE INTO " + RedditConfig.redditArticleNetworkTable + "  VALUES("
				+ "?,?,?,?,?," + "?,?,?,?)";
		PreparedStatement ps = sql.createPreparedStatement(query);
		
		Iterator<Network> iter = networkList.iterator();
		
		try {
			while(iter.hasNext()){
				Network network = iter.next();
				
				ps.setString(1, articleName);
				ps.setInt(2, network.getTotalComments());
				ps.setInt(3, network.getFirstLevalComments());
				ps.setInt(4, network.getFirstLevalIsolateComments());
				ps.setInt(5, network.getReplies());
				ps.setInt(6, network.getTotalCommenters());
				
				ps.setInt(7, network.getEdgeList().size());
				ps.setInt(8, network.getIsolateCommenters());
				ps.setInt(9, network.getIndex());
				
				ps.addBatch();
			}
			
			
			ps.executeBatch();
			
			ps.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * WRITE commenterLinks of commenter network list into database
	 * 
	 * 
	 */
	public void writeCommenterLinks(){

		String query = "INSERT IGNORE INTO " + RedditConfig.redditCommenterLinkTable + "  VALUES("
				+ "?,?,?,?,?," + "?,?)";
		PreparedStatement ps = sql.createPreparedStatement(query);
		
		Iterator<Network> iter = networkList.iterator();
		
		try {
			while(iter.hasNext()){
				Network network = iter.next();
				
				ArrayList<CommenterLink> linkList = network.getEdgeList();
				Iterator<CommenterLink> iterLink = linkList.iterator();
				
				
				while(iterLink.hasNext()){
					CommenterLink link = iterLink.next();
					
					ps.setString(1, articleName);
					ps.setString(2,link.getCommenter_1());
					ps.setString(3, link.getCommenter_2());
					ps.setInt(4, link.getTotalComments());
					ps.setInt(5, link.getPolarity());
					
					ps.setDouble(6, link.getWeight());
					ps.setInt(7, network.getIndex());
				
					ps.addBatch();
				}
				
			}
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
		
			e.printStackTrace();
		}
	}
		
		
		

	
	
	
	
	///////////////////////////////////////////////////////////////
	///////////////////CommentNetwork class////////////////////////
	///////////////////////////////////////////////////////////////
	
	class Network {
		
		private int index;	//index of current network in networkList
		
//		private String articleName = ""; //the article name
		
		private ArrayList<String> nodeList = new ArrayList<String>();
		
		private ArrayList<CommenterLink> commenterLinkList = new ArrayList<>();
		
		private int totalComments = 0;	//the number of total comments
		
		private int firstLevalComments = 0;	// the number of first level comments
		
		private int firstLevalIsolateComments = 0; // the number of first level comments with no replies
		
		private int replies = 0;
		
		private int totalCommenters = 0;
		
		private int isolateCommenters = 0;
		
		
		

		
		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void addNode(String node){
			this.nodeList.add(node);
		}
		
		public void addLink(CommenterLink edge){
			this.commenterLinkList.add(edge);
		}


		public ArrayList<String> getNodeList() {
			return nodeList;
		}


		public void setNodeList(ArrayList<String> nodeList) {
			this.nodeList = nodeList;
		}


		public ArrayList<CommenterLink> getEdgeList() {
			return commenterLinkList;
		}


		public void setEdgeList(ArrayList<CommenterLink> edgeList) {
			this.commenterLinkList = edgeList;
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

}
