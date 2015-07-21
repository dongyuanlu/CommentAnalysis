package uibe.ldy.sentistrength;

import java.util.ArrayList;

import model.RedditComment;
import uibe.ldy.reddit.ReadReddit;
import util.PreProcess;
import util.WriteArrayList2File;

public class Input4SentiStrength {

	String tempInputFilePath = "E:/Work/RedditComment/inputSenti/1.txt";
	
	
	
	public static void main(String[] args){
		Input4SentiStrength inputer = new Input4SentiStrength();
		inputer.generateInput4SentiStrength();
	}
	
	
	/**
	 * Generate file for input of SentiStrength v2.2  UGI
	 * 
	 * return file: 
	 * comment-name \t parent-name \t preprocessed-body
	 * 
	 */
	public void generateInput4SentiStrength(){
		ReadReddit reader = new ReadReddit();
		
		ArrayList<RedditComment> commentList = reader.readCommentsByArticleName("t3_1zgwte");
		
		ArrayList<String> writer = new ArrayList<String>();
		
		for(int i = 0; i < commentList.size(); i++){
			RedditComment comment = commentList.get(i);
			String body = comment.getBody();
			String bodyPreprocessed = preProcessBody(body);
			
			writer.add(comment.getName() + "\t" + comment.getParent_id() + "\t" + bodyPreprocessed);
		}
		
		WriteArrayList2File.writeArrayList2File(writer, tempInputFilePath);
	}
	
	
	
	
	
	public String preProcessBody(String body){
		String bodyPreprocessed = PreProcess.removeHTML(body);
		
		bodyPreprocessed = PreProcess.removeHttp(bodyPreprocessed);
		bodyPreprocessed = PreProcess.removeExtraEntre(bodyPreprocessed);
		
		return bodyPreprocessed;
	}
}
