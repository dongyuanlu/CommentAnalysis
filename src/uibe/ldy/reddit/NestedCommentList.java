package uibe.ldy.reddit;

import java.util.ArrayList;

import util.WriteArrayList2File;
import Model.RedditComment;

public class NestedCommentList {
	
	private String sentimentName = "sentiment_1";
	
	private String condition = "ORDER BY created_utc desc";
	
	ArrayList<String> write2File = new ArrayList<>();
	ReadRedditArticle reader = new ReadRedditArticle();
	
	
	public static void main(String[] args){
		String articleName = "t3_1zgwte";
		
		NestedCommentList nestedcommenter = new NestedCommentList();
		nestedcommenter.generateNestedList(articleName);
	}
	
	
	public void generateNestedList(String articleName){
		
		getReplyComments(articleName,0);
		
		WriteArrayList2File.writeArrayList2File(write2File, "E:/Work/RedditComment/inputSenti/t3_1zgwte");
		   
	}

	
	public void getReplyComments(String parentId, int nestedLevel){
		ArrayList<RedditComment> commentList = reader.readNestedCommentsByParentId(parentId, condition);
		
		for(int i = 0 ; i < commentList.size(); i++){
			RedditComment comment = commentList.get(i);
			reader.addCommentSentiment(comment, sentimentName);
			String commentName = comment.getName();
			write2File.add(tabString(nestedLevel) + commentName + "\t" + comment.getPreprocessedBody() + "\t" + comment.getSentimentPos() + "\t" + comment.getSentimentNeg());
			
			getReplyComments(commentName, nestedLevel+1);
			System.out.println(commentName + "\t" + nestedLevel);
		}
		
	}
	
	/**
	 * 
	 * Return a string contains n tabs
	 * 
	 * @param n
	 * @return
	 */
	public String tabString(int n){
		String tabs = "";
		
		for(int i = 0; i < n; i++){
			tabs += "\t";
		}
		
		return tabs;
	}
}
