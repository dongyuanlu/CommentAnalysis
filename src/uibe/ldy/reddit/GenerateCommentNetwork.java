package uibe.ldy.reddit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import util.Combine;
import util.Counter;
import Model.CommentNetwork;
import Model.CommentNetworkEdge;
import Model.RedditComment;

public class GenerateCommentNetwork {

	public static void main(String[] args) {
		

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
		Counter counter = new Counter();
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
			counter.counter(Combine.combineTwoStr(commenter_1,commenter_2), polarity);
		}
		
		//add edges to network
		HashMap<String, Integer> commenterPolarityMap = counter.getStrHashMap();
		Iterator<String> iterC = commenterPolarityMap.keySet().iterator();
		while(iterC.hasNext()){
			
			String commenterPair = iterC.next();
			int pol = commenterPolarityMap.get(commenterPair);
			String[] commenters = commenterPair.split("_");
			CommentNetworkEdge edge = new CommentNetworkEdge(commenters[0],commenters[1]);
			if(pol > 0){
				edge.setPolarity(1);
			}else if(pol < 0){
				edge.setPolarity(-1);
			}else{
				edge.setPolarity(0);
			}
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
	
	
	public void writeNetwork2DB(CommentNetwork network){
		
	}
}
