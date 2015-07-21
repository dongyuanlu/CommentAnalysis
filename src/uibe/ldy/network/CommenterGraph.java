package uibe.ldy.network;

import java.util.ArrayList;

import model.CommenterLink;
import uibe.ldy.reddit.ReadReddit;
import uibe.ldy.reddit.RedditConfig;
import util.SQLUtil;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class CommenterGraph {
	private String articleName;
	private int network_index;
	
	private UndirectedSparseGraph<Integer, CmmEdge> graph;
	
	private SQLUtil sql;
	ReadReddit reader;
	
	public CommenterGraph(String articleName, int network_index){
		this.articleName = articleName;
		this.network_index = network_index;
		
		graph = new UndirectedSparseGraph<Integer, CmmEdge>();
		
		reader = new ReadReddit();
		sql = new SQLUtil(RedditConfig.database);
	}
	
	
	public void generateGraphFromDB(){
	
		ArrayList<CommenterLink> linkList = reader.readCommenterLinkList(articleName, network_index);
		for(int i = 0; i < linkList.size(); i++){
			CommenterLink link = linkList.get(i);
			
		}
		
	}
	
	
	
	class CmmEdge {
		private double weight;
		private int polarity;
		
		int edgeIndex;
		
		public CmmEdge(double weight, int polarity){
			this.weight = weight;
			this.polarity = polarity;
		}

		public double getWeight() {
			return weight;
		}

		public int getPolarity() {
			return polarity;
		}
		
		
	}
	
	
	

}
