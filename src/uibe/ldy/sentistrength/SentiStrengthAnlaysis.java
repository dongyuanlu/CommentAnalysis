package uibe.ldy.sentistrength;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.RedditComment;
import uibe.ldy.reddit.ReadReddit;
import uibe.ldy.reddit.RedditConfig;
import uk.ac.wlv.sentistrength.SentiStrength;
import util.PreProcess;
import util.SQLUtil;

public class SentiStrengthAnlaysis {
	private SQLUtil sql = new SQLUtil("data/database.property");
	private SentiStrength sentiStrength = new SentiStrength(); 
	//Create an array of command line parameters to send (not text or file to process)
	private String ssthInitialisation[] = {"sentidata", "data/SentStrength_Data/", "explain"};
	
	
	public SentiStrengthAnlaysis(){
		sentiStrength.initialise(ssthInitialisation); //Initialise
	}
	
	
	public static void main(String[] args){
		ReadReddit reader = new ReadReddit();
		ArrayList<String> articleList = reader.readLinkIdListFromComments("1");
		
		SentiStrengthAnlaysis sentiAnalyzer = new SentiStrengthAnlaysis();
		
		for(int i = 0; i < articleList.size(); i++){
			String articleName = articleList.get(i);
			HashMap<String, RedditComment> commentMap = sentiAnalyzer.detectSentiment(articleName, "sentiment_1");
			sentiAnalyzer.write2CommentSentimentTable(commentMap);
			System.out.println(i + "\t" + articleName);
		}
		
	}
	
	
	/**
	 * Given articleName, and sentimentName
	 * Detect sentiment pos and neg of each comment
	 * Return comment HashMap
	 * 
	 * @param articleName
	 * @param sentimentName
	 * @return HashMap<String, RedditComment>
	 */
	public HashMap<String, RedditComment> detectSentiment(String articleName, String sentimentName){
		
		ReadReddit reader = new ReadReddit();
		String condition = "AND name NOT IN (SELECT name FROM " + RedditConfig.redditCommentSentimentTable + ")";
		HashMap<String, RedditComment> commentMap = reader.readCommentsByArticleName(articleName, condition, sentimentName);
		
		Iterator<String> iter = commentMap.keySet().iterator();
		
		while(iter.hasNext()){
			String name = iter.next();
			RedditComment comment = commentMap.get(name);
			
			//pre-process body
			String body = comment.getBody();
			String preprocessedBody = preProcessBody(body);
			comment.setPreprocessedBody(preprocessedBody);
			
			//detect sentiment
			String rawSentimentResult = sentiStrength.computeSentimentScores(preprocessedBody);
			String[] rawResults = rawSentimentResult.split(" ");
			int pos = Integer.parseInt(rawResults[0]);
			int neg = Integer.parseInt(rawResults[1]);
			comment.setSentimentPos(pos);
			comment.setSentimentNeg(neg);
			
			//Update commentMap
			commentMap.put(name, comment);
		}
		
		return commentMap;
	}
	

	
	public void write2CommentSentimentTable(HashMap<String, RedditComment> commentMap){
		
		String query = "INSERT IGNORE INTO " + RedditConfig.redditCommentSentimentTable + "  VALUES("
				+ "?,?,?,?)";
		
		Iterator<String> iter = commentMap.keySet().iterator();
		PreparedStatement ps = sql.createPreparedStatement(query);
				
		try {
			while(iter.hasNext()){
				String name = iter.next();
				RedditComment comment = commentMap.get(name);
				
				ps.setString(1, comment.getName());
				ps.setString(2, comment.getPreprocessedBody());
				ps.setInt(3, comment.getSentimentPos());
				ps.setInt(4, comment.getSentimentNeg());
				
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}	

	}
	
	
	
	/**
	 * Preprocess comment body
	 * 
	 * @param body
	 * @return
	 */
	public String preProcessBody(String body){
		String bodyPreprocessed = PreProcess.removeHTML(body);
		
		bodyPreprocessed = PreProcess.removeHttp(bodyPreprocessed);
		bodyPreprocessed = PreProcess.removeExtraEntre(bodyPreprocessed);
		
		return bodyPreprocessed;
	}

}
