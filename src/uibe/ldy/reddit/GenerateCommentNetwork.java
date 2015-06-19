package uibe.ldy.reddit;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


import util.Combine;
import util.Counter;
import util.SQLUtil;
import Model.CommentNetwork;
import Model.CommentNetworkEdge;
import Model.RedditComment;

public class GenerateCommentNetwork {

	public static void main(String[] args) {
		GenerateCommentNetwork generator = new GenerateCommentNetwork();
		CommentNetwork network = generator.generateNetwork("t3_1zgwte");
		generator.writeNetwork2DB(network);

	}

	/**
	 * Given articleName
	 * Return CommentNetwork: contains an edgeList, multi network attributes
	 * 
	 * @param articleName
	 * @return
	 */
	public CommentNetwork generateNetwork(String articleName){
		CommentNetwork network = new CommentNetwork(articleName);
		Counter counterPolarity = new Counter();
		Counter counterNum = new Counter();
		DetectPolarity detecter = new DetectPolarity();
		
		HashSet<String> allCommenterSet = new HashSet<>();
		HashSet<String> networkCommenterSet = new HashSet<>();
		
		ReadRedditArticle reader = new ReadRedditArticle();
		HashMap<String, RedditComment> commentMap = reader.readCommentsByArticleName(articleName, "", "sentiment_1");
		int firstLevel = 0;
		
		//generate edge
		Iterator<String> iter = commentMap.keySet().iterator();
		while(iter.hasNext()){
			String commentName = iter.next();
			RedditComment comment = commentMap.get(commentName);
			if(comment.getParent_id().equals(comment.getLink_id())){ //if comment to article, continue
				allCommenterSet.add(comment.getAuthor());
				firstLevel++;
				continue;
			}
			
			int polarity = detecter.getPolarityFromComment(comment);
			String commenter_1 = comment.getAuthor();
			String commenter_2 = commentMap.get(comment.getParent_id()).getAuthor();
			networkCommenterSet.add(commenter_1);
			networkCommenterSet.add(commenter_2);
			allCommenterSet.add(commenter_1);
			allCommenterSet.add(commenter_2);
			counterPolarity.counter(Combine.combineTwoStr(commenter_1,commenter_2), polarity);
			counterNum.counter(Combine.combineTwoStr(commenter_1,commenter_2));
		}
		
		//add edges to network
		HashMap<String, Integer> commenterPolarityMap = counterPolarity.getStrHashMap();
		HashMap<String, Integer> commentNumMap = counterNum.getStrHashMap();
		Iterator<String> iterC = commenterPolarityMap.keySet().iterator();
		while(iterC.hasNext()){
			
			String commenterPair = iterC.next();
			int pol = commenterPolarityMap.get(commenterPair);
			String[] commenters = Combine.splitTwoStr(commenterPair);
			CommentNetworkEdge edge = new CommentNetworkEdge(commenters[0],commenters[1]);
			
//			if(pol > 0){	//set polarity of edge
//				edge.setPolarity(1);
//			}else if(pol < 0){
//				edge.setPolarity(-1);
//			}else{                                                               
//				edge.setPolarity(0);
//			}
			edge.setPolarity(pol); ////set polarity of edge
			
			edge.setTotal(commentNumMap.get(commenterPair)); //set total comments count between these two commenters
			
			network.addEdge(edge);
		}
		
		//add network attributes
		
		network.setTotalCommenters(allCommenterSet.size());
		network.setIsolateCommenters(allCommenterSet.size() - networkCommenterSet.size());
		network.setTotalComments(commentMap.size());
		network.setFirstLevalComments(firstLevel);
		network.setReplies(network.getTotalComments() - network.getFirstLevalComments());
		
		return network;
	}
	
	///////////////////////////////////////////////////////////////////
	/////////////////////Write Network Into Database///////////////////
	///////////////////////////////////////////////////////////////////
	/**
	 * Write article network attributes and edges into commenterNetwork table
	 * 
	 * 
	 * @param network
	 */
	public void writeNetwork2DB(CommentNetwork network){
		writeArticleNetwork(network);
		writeCommenterNetwork(network);
		
		
	}
	
	
	/**
	 * 
	 * Write article network attributes into database
	 * 
	 * @param network
	 */
	public void writeArticleNetwork(CommentNetwork network){
		SQLUtil sql = new SQLUtil(RedditConfig.database);
		String query = "INSERT IGNORE INTO " + RedditConfig.redditArticleNetworkTable + "  VALUES("
				+ "?,?,?,?,?," + "?)";
		PreparedStatement ps = sql.createPreparedStatement(query);
		
		try {
			ps.setString(1, network.getArticleName());
			ps.setInt(2, network.getTotalComments());
			ps.setInt(3, network.getFirstLevalComments());
			ps.setInt(4, network.getReplies());
			ps.setInt(5, network.getTotalCommenters());
			
			ps.setInt(6, network.getIsolateCommenters());
			
			ps.addBatch();
			
			ps.executeBatch();
			
			ps.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * Write commenterNetwork edges into database
	 * 
	 * @param network
	 */
	public void writeCommenterNetwork(CommentNetwork network){
		String name = network.getArticleName();
		SQLUtil sql = new SQLUtil(RedditConfig.database);
		String query = "INSERT IGNORE INTO " + RedditConfig.redditCommenterNetworkTable + "  VALUES("
				+ "?,?,?,?,?," + "?)";
		PreparedStatement ps = sql.createPreparedStatement(query);
		
		ArrayList<CommentNetworkEdge> edgeList = network.getEdgeList();
		Iterator<CommentNetworkEdge> iter = edgeList.iterator();
		try {
			while(iter.hasNext()){
				CommentNetworkEdge edge = iter.next();
				
				ps.setString(1, name);
				ps.setString(2,edge.getCommenter_1());
				ps.setString(3, edge.getCommenter_2());
				ps.setInt(4, edge.getTotal());
				ps.setInt(5, edge.getPolarity());
				
				ps.setDouble(6, 0.0);
			
				ps.addBatch();
			}
			
			ps.executeBatch();
			ps.close();
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}
}
