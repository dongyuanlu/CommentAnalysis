package uibe.ldy.reddit;

import java.util.ArrayList;

import Model.RedditComment;

public class NestedCommentList {
	
	
	
	public void generateNestedList(String articleName){
		ReadRedditArticle reader = new ReadRedditArticle();
		ArrayList<RedditComment> commentList = reader.readCommentsByArticleName(articleName);
		
	}

}
