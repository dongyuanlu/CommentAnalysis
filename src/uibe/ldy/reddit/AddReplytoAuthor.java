package uibe.ldy.reddit;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.RedditComment;
import util.SQLUtil;

public class AddReplytoAuthor {

	private String sentimentName = "sentiment_1";
	private SQLUtil sql = new SQLUtil(RedditConfig.database);
	
	public static void main(String[] args){
		AddReplytoAuthor add = new AddReplytoAuthor();
		add.addReplytoAuthor();
	}
	
	
	/**
	 * UPDATE current comment_table by add all comments with replyto_author
	 * 
	 */
	public void addReplytoAuthor(){
		ReadReddit reader = new ReadReddit();
		ArrayList<String> linkIdList = reader.readLinkIdListFromComments("replyto_author IS NULL");
		
		for(int i = 0; i < linkIdList.size(); i++){
			String linkId = linkIdList.get(i);
			String condition = "link_id='" + linkId + "'";
			
			
			HashMap<String, RedditComment> commentMap = reader.readCommentsByCondition(condition, sentimentName);
			
			Iterator<String> iter = commentMap.keySet().iterator();
			
			while(iter.hasNext()){
				String commentName = iter.next();
				RedditComment comment = commentMap.get(commentName);
				String parentId = comment.getParent_id();
				if(comment.getLink_id().equals(parentId)){
					updateReplytoAuthor(commentName, "");
					continue;
				}
				if(commentMap.containsKey(parentId)){
					String replytoAuthor = commentMap.get(parentId).getAuthor();
					updateReplytoAuthor(commentName, replytoAuthor);
				}
				
			}
			
			
			
			System.out.println(i);
		}
		
	}
	
	/**
	 * Update current comment by adding replyto_author
	 * 
	 * @param commentName
	 * @param replytoAuthor
	 */
	public void updateReplytoAuthor(String commentName, String replytoAuthor){
		
		
		String query = "UPDATE " + RedditConfig.redditCommentTable + " SET replyto_author='" + replytoAuthor + "' WHERE name='" + commentName + "'";
		
		Statement st = sql.getStatement();
		try {
			st.execute(query);
			st.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
