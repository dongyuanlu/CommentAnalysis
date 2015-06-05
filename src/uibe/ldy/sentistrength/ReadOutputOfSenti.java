package uibe.ldy.sentistrength;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import uibe.ldy.reddit.RedditConfig;
import util.ReadArrayListFromFile;
import util.SQLUtil;

public class ReadOutputOfSenti {

	String tempOutputFilePath = "E:/Work/RedditComment/inputSenti/1+results.txt";
	
	
	
	public static void main(String[] args){
		ReadOutputOfSenti reader = new ReadOutputOfSenti();
		reader.readOutputIntoDB(reader.tempOutputFilePath);
	}
	
	
	
	/**
	 * Read Output file of SentiStrength v2.2
	 * Write sentiment result into database reddit_comment_sentiment
	 * 
	 */
	public void readOutputIntoDB(String filePath){
		
		ArrayList<String> outputFile = ReadArrayListFromFile.readArrayList_Each_line_notFilter(filePath);
		
		SQLUtil sql = new SQLUtil("data/database.property");
		String query = "INSERT IGNORE INTO " + RedditConfig.redditCommentSentimentTable + "  VALUES("
				+ "?,?,?,?)";
		PreparedStatement ps = sql.createPreparedStatement(query);
		try {
			for(int i = 0; i < outputFile.size(); i++){
				String line = outputFile.get(i);
				String[] terms = line.split("\t");
				String name = terms[0];
				String body = terms[2];
				int pos = Integer.parseInt(terms[4]);
				int neg = Integer.parseInt(terms[5]);
				
				ps.setString(1, name);
				ps.setString(2, body);
				ps.setInt(3, pos);
				ps.setInt(4, neg);
				
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

}
