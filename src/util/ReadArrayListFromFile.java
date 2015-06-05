package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * 1. This method returns an arraylist containing the list storied in the file
	 * (each line corresponds an entity in the arraylist)
	 * 
 * 2. the same with method 1, BUT:
	 * the duplicated entities are filtered in the arraylist
	 * 
 * 3. Read arrayList from Hashmap file (take the first word)
 * 
 * 4. Read topN arrayList from HashMap file (take the first word)
 * @author adminnus
 *
 */
public class ReadArrayListFromFile {

	/**
	 * Method 1:
	 * This method returns an arraylist containing the list storied in the file
	 * (each line corresponds an entity in the arraylist)
	 * ****the duplicated entities may exist in the arraylist
	 * 14-Mar-2011
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static ArrayList<String> readArrayList_Each_line_notFilter(String filePath){
		ArrayList<String> arraylist = new ArrayList<String>();
		BufferedReader brFile;
		try {
			brFile = new BufferedReader(new InputStreamReader
					(new FileInputStream(filePath), "utf-8"));
			for(String readline = brFile.readLine(); readline!=null; readline = brFile.readLine()){
				arraylist.add(readline);
			}
			brFile.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arraylist;
	}

	/**
	 * Method 2: 
	 * the same with method 1, BUT:
	 * the duplicated entities are filtered in the arraylist
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static ArrayList<String> readArrayList_Each_line_Filtered(String filePath){
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		ArrayList<String> arraylist = new ArrayList<String>();
		try {
			BufferedReader brFile = new BufferedReader(new InputStreamReader
					(new FileInputStream(filePath), "utf-8"));
			for(String readline = brFile.readLine(); readline!=null; readline = brFile.readLine()){
				set.add(readline);
			}
			brFile.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		Iterator<String> iter = set.iterator();
		while(iter.hasNext()){
			String term = iter.next();
			arraylist.add(term);
		}
		return arraylist;
	}
	
	
	/**
	 * Read the first term from HashMap file
	 * 
	 * @param filePath
	 * @return
	 */
	public static ArrayList<String> readArrayList_From_MapFile(String filePath){
		ArrayList<String> arraylist = new ArrayList<String>();
		BufferedReader brFile;
		try {
			brFile = new BufferedReader(new InputStreamReader
					(new FileInputStream(filePath), "utf-8"));
			for(String readline = brFile.readLine(); readline!=null; readline = brFile.readLine()){
				String word = readline.split("\t")[0];
				arraylist.add(word);
			}
			brFile.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arraylist;

	}
	
	/**
	 * Read topN arrayList from HashMap file (take the first word)
	 * if(topN == -1, take all)
	 * 
	 * @param filePath
	 * @return
	 */
	public static ArrayList<String> readArrayList_From_MapFile(String filePath, int topN){
		ArrayList<String> arraylist = new ArrayList<String>();
		BufferedReader brFile;
		int index = 0;
		try {
			brFile = new BufferedReader(new InputStreamReader
					(new FileInputStream(filePath), "utf-8"));
			for(String readline = brFile.readLine(); readline!=null; readline = brFile.readLine()){
				String word = readline.split("\t")[0];
				arraylist.add(word);
				index++;
				if(topN > 0 && index >= topN){
					break;
				}
			}
			brFile.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		
		return arraylist;

	}
	
}
