package uibe.ldy.sentistrength;

import uk.ac.wlv.sentistrength.SentiStrength;

public class SentiStrengthAnlaysis {
	
	public static void main(String[] args){

		//Method 2: One initialisation and repeated classifications
		SentiStrength sentiStrength = new SentiStrength(); 
		//Create an array of command line parameters to send (not text or file to process)
		String ssthInitialisation[] = {"sentidata", "data/SentStrength_Data/", "explain"};
		sentiStrength.initialise(ssthInitialisation); //Initialise
		//can now calculate sentiment scores quickly without having to initialise again
		System.out.println(sentiStrength.computeSentimentScores("No you wouldn't. I'm sure she wouldn't let you get close enough.")); 
		System.out.println(sentiStrength.computeSentimentScores("It was like my Spanish friend making fun of my Mexican accent. Loved it."));
		
	}

}
