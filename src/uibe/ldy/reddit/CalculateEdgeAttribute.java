package uibe.ldy.reddit;

import model.RedditComment;

public class CalculateEdgeAttribute {
	
	
	/**
	 * Given comment, comment contains sentiment_pos and sentiment_neg
	 * Strategy: compare the absolute value of sentiment_pos and sentiment_neg
	 * 
	 * return: 1-pos; -1-neg; 0-obj
	 * 
	 * @param comment
	 * @return
	 */
	public int getPolarityFromComment(RedditComment comment){
		int polarity = 0;
		int pos = comment.getSentimentPos();
		int neg = comment.getSentimentNeg();
		if(pos == 1 && neg == -1){
			polarity = 0;
		}
		else if(pos >= Math.abs(neg)){
			polarity = 1;
		}
		else if(pos < Math.abs(neg)){
			polarity = -1;
		}
		
		return polarity;
	}

	
	/**
	 * Given the rank position of two comments
	 * Return the weight of this comment reply edge
	 * @param rank1
	 * @param rank2
	 * @return
	 */
	public double getWeightFromCommentRank(int rank1, int rank2){
		
		int dis = Math.abs(rank2-rank1);
		if(dis == 0){
			return 0.0;
		}else{
			return (double) 1.0/ dis;
		}
	}
}
