package uibe.ldy.reddit;

import Model.RedditComment;

public class DetectPolarity {
	
	
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

}
