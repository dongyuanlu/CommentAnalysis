package uibe.ldy.network;

import java.util.ArrayList;

import model.CommenterLink;
import uibe.ldy.reddit.ReadReddit;
import uibe.ldy.reddit.RedditConfig;
import util.SQLUtil;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * JUNG graph
 * 
 * representing commenter network
 * 
 * @author Administrator
 *
 */
public class CommenterGraph {
	private String articleName;
	private int network_index;
	
	private UndirectedSparseGraph<Integer, CmmEdge> graph;

	ReadReddit reader;
	
	
	/**
	 * 
	 * Constructor
	 * 
	 * @param articleName
	 * @param network_index
	 */
	public CommenterGraph(String articleName, int network_index){
		this.articleName = articleName;
		this.network_index = network_index;
		
		graph = new UndirectedSparseGraph<Integer, CmmEdge>();
		
		reader = new ReadReddit();

	}
	
	
	public static void main(String[] args){
		CommenterGraph cg = new CommenterGraph("t3_1zgwte", 10);
		cg.generateGraphFromDB();
	}
	
	
	/**
	 * 
	 * Generate Graph from database
	 * 
	 */
	public void generateGraphFromDB(){
	
		ArrayList<CommenterLink> linkList = reader.readCommenterLinkList(articleName, network_index);
		for(int i = 0; i < linkList.size(); i++){
			CommenterLink link = linkList.get(i);
			graph.addEdge(new CmmEdge(i, link.getWeight(),link.getPolarity()), link.getIndexCommenter_1(), link.getIndexCommenter_2());
		}
		
//		System.out.println(graph.toString());
		
	}
	
	
	
	class CmmEdge {
		private double weight;
		private int polarity;
		
		int edgeIndex;
		
		public CmmEdge(int edgeIndex, double weight, int polarity){
			this.edgeIndex = edgeIndex;
			this.weight = weight;
			this.polarity = polarity;
		}

		public double getWeight() {
			return weight;
		}

		public int getPolarity() {
			return polarity;
		}
		
		public String toString(){
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("edge index: ");
			buffer.append(edgeIndex);
			buffer.append("; polarity: ");
			buffer.append(polarity);
			buffer.append("; weight: ");
			buffer.append(weight);
			
			return buffer.toString();
		}
		
	}
	
	
	

}
