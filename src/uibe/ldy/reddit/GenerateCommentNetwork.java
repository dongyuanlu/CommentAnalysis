package uibe.ldy.reddit;

import java.util.ArrayList;

import Model.CommentNetwork;
import Model.CommentNetworkEdge;
import Model.RedditComment;

public class GenerateCommentNetwork {

	public static void main(String[] args) {
		

	}

	
	public CommentNetwork generateNetwork(String articleName){
		CommentNetwork network = new CommentNetwork(articleName);
		
		ReadRedditArticle reader = new ReadRedditArticle();
		ArrayList<RedditComment> commentList = reader.readCommentsByArticleName(articleName, "", "sentiment_1");
		
		for(int i = 0; i < commentList.size(); i++){
			
		}
		
		return network;
	}
}
