package uibe.ldy.reddit;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import model.CommenterLink;
import model.RedditArticle;
import model.RedditArticleMedia;
import model.RedditComment;
import util.SQLUtil;

/**
 * Read everything of reddit list
 * 
 * @author ellen
 *
 */
public class ReadReddit {

//	private ArrayList<RedditArticle> articleList = new ArrayList<RedditArticle>();
	private String listTableName;
	private String condition = "";
	
	private HashMap<String, RedditArticle> articleIndexMap = new HashMap<String, RedditArticle>();
	
	private SQLUtil sql = new SQLUtil("data/database.property");
	
	public ReadReddit(){
		
	}
	
	public ReadReddit(String listTableName){
		this.listTableName = listTableName;
	}
	
	public ReadReddit(String listTableName, String condition){
		this.listTableName = listTableName;
		this.condition = condition;
	}
	
	public HashMap<String, RedditArticle> getArticleIndexList() {
		return articleIndexMap;
	}

	
	
	//////////////////////////////////////////////////////////////////
	////////////////////READ article//////////////////////////////////
	//////////////////////////////////////////////////////////////////

	/****************aticleIndexMap*********************************/
	/**
	 * Read article index list from list_reddit_... table into articleIndexList
	 * key: name
	 * value: article
	 * @param listTable
	 */
	public void readArticleIndexMap(){
		
		String q = "SELECT * FROM " + listTableName + " WHERE " + condition;
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				int rank = rs.getInt("rank");
				String name = rs.getString("name");
				RedditArticle article = new RedditArticle();
				article.setRank(rank);
				article.setName(name);
				articleIndexMap.put(name, article);
			}
			
			rs.close();
			st.close();
		} catch (SQLException e) {			
			e.printStackTrace();			
		}

	}
	/*************************************************/
	
	/**
	 * Get link_id list from comment table
	 * 
	 * @param condition
	 * @return
	 */
	public ArrayList<String> readLinkIdListFromComments(String condition){
		ArrayList<String> linkIdList = new ArrayList<String>();
		
		String q = "SELECT DISTINCT link_id FROM " + RedditConfig.redditCommentTable + " WHERE " + condition;
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				linkIdList.add(rs.getString("link_id"));		
			}
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		
		return linkIdList;
	}
	
	
	/****************articleList*********************************/	
	/**
	 * Given articleIndexList
	 * read article value from reddit_article table
	 * Fulfill the value of articleIndexList
	 */
	public void readArticleList(){
		
		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = articleIndexMap.get(name);
			String q = "SELECT * FROM " + RedditConfig.redditArticleTable 
					+ " WHERE name='" + name + "'";
			Statement st = sql.getStatement();
			try {
				ResultSet rs = st.executeQuery(q);
				
				while(rs.next()){
					article.setId(rs.getString("id"));
					article.setDomain(rs.getString("domain"));
					article.setAuthor(rs.getString("author"));
					article.setSubreddit(rs.getString("subreddit"));
					article.setSubreddit_id(rs.getString("subreddit_id"));
					article.setUrl(rs.getString("url"));
					article.setPermalink(rs.getString("permalink"));
					article.setTitle(rs.getString("title"));
					article.setNum_comments(rs.getInt("num_comments"));
					article.setUps(rs.getInt("ups"));
					article.setScore(rs.getInt("score"));
					article.setKind(rs.getString("kind"));
					article.setSelftext(rs.getString("selftext"));
					article.setCreated(rs.getLong("created"));
					article.setCreated_utc(rs.getLong("created_utc"));
					article.setMedia_url(rs.getString("media_url"));
					
					articleIndexMap.put(name, article);
				}
				
				rs.close();
				st.close();
			} catch (SQLException e) {			
				e.printStackTrace();			
			}

		}
		
	}
	/*************************************************/
	
	
	////////////////////////////////////////////////////////////
	///////////////////  READ media  ///////////////////////////
	////////////////////////////////////////////////////////////
	
	/*******************add Media******************************/	
	
	/**
	 * Given article name, return media if has
	 * 
	 * @param name
	 * @return
	 */
	public RedditArticleMedia readArticleMediaByArticleName(String name){
		String q = "SELECT * FROM " + RedditConfig.redditArticleMediaTable 
				+ " WHERE name='" + name + "'";
		Statement st = sql.getStatement();
		RedditArticleMedia media = null;
		try {
			ResultSet rs = st.executeQuery(q);
			
			while(rs.next()){
				media = new RedditArticleMedia();
				media.setName(rs.getString("name"));
				media.setMedia_url(rs.getString("media_url"));
				media.setTitle(rs.getString("title"));
				media.setDescription(rs.getString("description"));
				media.setType(rs.getString("type"));
				media.setAuthor_name(rs.getString("author_name"));
				media.setProvider_name(rs.getString("provider_name"));
				
			}
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		return media;
	}
	
	/**
	 * Given article, add media into this article
	 * 
	 * @param article
	 */
	public RedditArticle addArticleMedia(RedditArticle article){
		RedditArticleMedia media = readArticleMediaByArticleName(article.getName());
		if(media != null){
			article.setMedia(media);
		}
		return article;
	}

	
	
	/**
	 * Given articleIndexList, read article media from reddit_articlemedia table
	 * Fulfill the media of all articles in articleIndexList
	 */
	public void addArticleListMedia(){

		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = addArticleMedia(articleIndexMap.get(name));
			articleIndexMap.put(article.getName(), article);

		}
	}
	/*************************************************/	
	
	/////////////////////////////////////////////////////////////////
	///////////////////    READ comment   ///////////////////////////
	/////////////////////////////////////////////////////////////////
	
	
	/******************READ comment*******************************/
	
	/**
	 * Read Comments Map by condition and sentimentName
	 * Return a HashMap of comments
	 * 
	 * 
	 * @param condition
	 * @param sentimentName
	 * @return
	 */
	public HashMap<String, RedditComment> readCommentsByCondition(String condition, String sentimentName){
		String q = "SELECT * FROM " + RedditConfig.redditCommentTable 
				+ " WHERE " + condition;
		Statement st = sql.getStatement();
		
		HashMap<String, RedditComment> commentMap = null;
		int rank = 0;
		
		try {
			ResultSet rs = st.executeQuery(q);
			if(!rs.wasNull()){
				commentMap = new HashMap<String, RedditComment>();
				while(rs.next()){
					RedditComment comment = new RedditComment();
					
					comment.setAuthor(rs.getString("author"));
					comment.setReplyto_author(rs.getString("replyto_author"));
					comment.setBody(rs.getString("body"));
					comment.setCreated(rs.getLong("created"));
					comment.setCreated_utc(rs.getLong("created_utc"));
					comment.setDowns(rs.getInt("downs"));
					comment.setId(rs.getString("id"));
					comment.setLink_id(rs.getString("link_id"));
					comment.setName(rs.getString("name"));
					comment.setParent_id(rs.getString("parent_id"));
					comment.setScore(rs.getInt("score"));
					comment.setUps(rs.getInt("ups"));
					comment.setRank(rank++);
				
					addCommentSentiment(comment, sentimentName);
					
					commentMap.put(comment.getName(), comment);
				}
			}
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		return commentMap;

	}
	
	
	
	/**
	 * 
	 * Given article name, special condition, and sentiment type
	 * 
	 * return commentMap (in which comment contains sentiment information)
	 * 
	 * 
	 * @param articleName
	 * @param condition
	 * @param sentimentName
	 * @return
	 */
	public HashMap<String, RedditComment> readCommentsByArticleName(String articleName, String condition, String sentimentName){
		
		String newCondition = "link_id='" + articleName + "' " + condition;
		
		return readCommentsByCondition(newCondition, sentimentName);
		
	}
	
	
	/**
	 * 
	 * READ comment list of current article
	 * 
	 * @param articleName: articleName of article
	 * @param condition: specified conditions like "AND created_utc < MAX", or "ORDER BY ..."
	 * @return
	 * 		RedditComment list of current article under condition
	 * 
	 */
	public ArrayList<RedditComment> readCommentsByArticleName(String articleName, String condition){
		String q = "SELECT * FROM " + RedditConfig.redditCommentTable 
				+ " WHERE link_id='" + articleName + "' " + condition;
		Statement st = sql.getStatement();
		
		ArrayList<RedditComment> commentList = null;
		
		try {
			ResultSet rs = st.executeQuery(q);
			if(!rs.wasNull()){
				commentList = new ArrayList<RedditComment>();
				while(rs.next()){
					RedditComment comment = new RedditComment();
					
					comment.setAuthor(rs.getString("author"));
					comment.setReplyto_author(rs.getString("replyto_author"));
					comment.setBody(rs.getString("body"));
					comment.setCreated(rs.getLong("created"));
					comment.setCreated_utc(rs.getLong("created_utc"));
					comment.setDowns(rs.getInt("downs"));
					comment.setId(rs.getString("id"));
					comment.setLink_id(rs.getString("link_id"));
					comment.setName(rs.getString("name"));
					comment.setParent_id(rs.getString("parent_id"));
					comment.setScore(rs.getInt("score"));
					comment.setUps(rs.getInt("ups"));
				
					commentList.add(comment);
				}
			}
			
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		return commentList;

	}
	
	
	/**
	 * Return commentList of articleName
	 * 
	 * @param articleName
	 * @return
	 */
	public ArrayList<RedditComment> readCommentsByArticleName(String articleName){
		return readCommentsByArticleName(articleName, "");
	}
	
	
	
	/**
	 * 
	 * READ replys to the comment with "parentId"
	 * 
	 * @param parentId: the comment id, to get its reply
	 * @param condition: special condition
	 * @return
	 *		RedditComment list replying to comment with 'parentId' 		
	 *
	 */
	public ArrayList<RedditComment> readReplytoCommentByParentId(String parentId, String condition){
		String q = "SELECT * FROM " + RedditConfig.redditCommentTable 
				+ " WHERE parent_id='" + parentId + "' " + condition;
		Statement st = sql.getStatement();
		
		ArrayList<RedditComment> commentList = null;
		
		try {
			ResultSet rs = st.executeQuery(q);
			if(!rs.wasNull()){
				commentList = new ArrayList<RedditComment>();
				while(rs.next()){
					RedditComment comment = new RedditComment();
					
					comment.setAuthor(rs.getString("author"));
					comment.setReplyto_author(rs.getString("replyto_author"));
					comment.setBody(rs.getString("body"));
					comment.setCreated(rs.getLong("created"));
					comment.setCreated_utc(rs.getLong("created_utc"));
					comment.setDowns(rs.getInt("downs"));
					comment.setId(rs.getString("id"));
					comment.setLink_id(rs.getString("link_id"));
					comment.setName(rs.getString("name"));
					comment.setParent_id(rs.getString("parent_id"));
					comment.setScore(rs.getInt("score"));
					comment.setUps(rs.getInt("ups"));
				
					commentList.add(comment);
				}
			}
			
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}
		return commentList;
	}
	
	
	/**
	 * Given RedditComment, add its sentiment
	 * 
	 * @param comment
	 * @param sentimentName
	 */
	public void addCommentSentiment(RedditComment comment, String sentimentName){
		String name = comment.getName();
		String q = "SELECT * FROM " + RedditConfig.redditCommentSentimentTable 
				+ " WHERE name='" + name + "' " + condition;
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(q);
			if(!rs.wasNull()){
				while(rs.next()){					
					comment.setPreprocessedBody(rs.getString("body_preprocessed"));
					comment.setSentimentPos(rs.getInt(sentimentName + "_pos"));
					comment.setSentimentNeg(rs.getInt(sentimentName + "_neg"));
				}
			}
			
			
			rs.close();
			st.close();
			
		} catch (SQLException e) {			
			e.printStackTrace();			
		}

		
	}
	
	/**
	 * Given article object, add comment list into this article
	 * 
	 * @param article
	 * @return
	 */
	public RedditArticle addArticleComment(RedditArticle article){
		ArrayList<RedditComment> commentList = readCommentsByArticleName(article.getName());
		article.setCommentList(commentList);
		return article;
	}
	
	
	/**
	 * Add comment-list for all of articles in the articleIndexMap
	 * 
	 */
	public void addArticleListComment(){
		Iterator<String> iter = articleIndexMap.keySet().iterator();
		while(iter.hasNext()){
			String name = iter.next();
			RedditArticle article = addArticleComment(articleIndexMap.get(name));
			articleIndexMap.put(article.getName(), article);

		}
	}
	
	/*******************************************************/
	
	/**
	 * 
	 * READ the first comment's created_utc
	 * 
	 * @param articleName
	 * @return
	 * 		the min created_utc of comments of articleName
	 */
	public long startTime(String articleName){
		long minTime = 0;
		
		String query = "SELECT MIN(created_utc) FROM " + RedditConfig.redditCommentTable + " WHERE link_id='" + articleName + "'";
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				minTime = rs.getLong("min(created_utc)");
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return minTime;
		
	}
	
	/**
	 * 
	 * @param articleName
	 * @return
	 * 		the max created_utc of comments of articleName
	 * 
	 */
	public long endTime(String articleName){
		long maxTime = 0;
		
		String query = "SELECT MAX(created_utc) FROM " + RedditConfig.redditCommentTable + " WHERE link_id='" + articleName + "'";
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				maxTime = rs.getLong("max(created_utc)");
			}
			
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return maxTime;
		
	}

	/**
	 * 
	 * @param articleName
	 * @return
	 * 		the average created_utc of comments of articleName
	 * 
	 */
	public long avgTime(String articleName){
		long avgTime = 0;
		
		String query = "SELECT AVG(created_utc) FROM " + RedditConfig.redditCommentTable + " WHERE link_id='" + articleName + "'";
		
		Statement st = sql.getStatement();
		
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				avgTime = rs.getLong("AVG(created_utc)");
			}
			
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		
		return avgTime;
		
	}

	
	/**
	 * 
	 * READ commenterLink list from reddit_commenter_link table
	 * 
	 * @param articleName: articleName
	 * @param network_index: index of network in evolving network list
	 * @return
	 */
	public ArrayList<CommenterLink> readCommenterLinkList(String articleName, int network_index){
		ArrayList<CommenterLink> commenterLinks = new ArrayList<CommenterLink>();
		
		ArrayList<String> commenterList = new ArrayList<String>();
		
		String query = "SELECT * FROM " + RedditConfig.redditCommenterLinkTable + " "
				+ "WHERE article_name='" + articleName + "' AND network_index=" + network_index;
		
		Statement st = sql.getStatement();
		try {
			ResultSet rs = st.executeQuery(query);
			
			while(rs.next()){
				String commenter1 = rs.getString("commenter_1");
				String commenter2 = rs.getString("commenter_2");
				
				if(!commenterList.contains(commenter1)){
					commenterList.add(commenter1);
				}
				
				if(!commenterList.contains(commenter2)){
					commenterList.add(commenter2);					
				}
				int indexCommenter1 = commenterList.indexOf(commenter1);
				int indexCommenter2 = commenterList.indexOf(commenter2);
				
				CommenterLink link = new CommenterLink(commenter1, commenter2);
				link.setIndexCommenter_1(indexCommenter1);
				link.setIndexCommenter_2(indexCommenter2);
				link.setPolarity(rs.getInt("polarity"));
				link.setTotalComments(rs.getInt("total"));
				link.setWeight(rs.getDouble("weight"));
				
				commenterLinks.add(link);
			}
			
			st.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return commenterLinks;
	}
	
	
}
