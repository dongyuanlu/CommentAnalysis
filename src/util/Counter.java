package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class Counter {

	HashMap<Integer, Integer> intHashMap = new HashMap<Integer,Integer>();
	
	HashMap<String, Integer> strHashMap = new HashMap<String, Integer>();
	
	HashMap<String, Double> strDoubleHashMap = new HashMap<String, Double>();
	/**
	 * Count "number" appear
	 * 
	 * @param number
	 * @return
	 */
	public void counter(int number){
		if(intHashMap.containsKey(number)){
			int c = intHashMap.get(number) + 1;
			intHashMap.put(number, c);
		}else{
			intHashMap.put(number, 1);
		}
		
	}
	
	
	/**
	 * Count "string" appear
	 * 
	 * @param string
	 * @return
	 */
	public void counter(String string){
		if(strHashMap.containsKey(string)){
			int c = strHashMap.get(string) + 1;
			strHashMap.put(string, c);
		}else{
			strHashMap.put(string, 1);
		}
		
	}
	
	/**
	 * 
	 * Count "string" appear with weight
	 * 
	 * @param string
	 * @param n
	 */
	public void counter(String string, int n){
		if(strHashMap.containsKey(string)){
			int c = strHashMap.get(string) + n;
			strHashMap.put(string, c);
		}else{
			strHashMap.put(string, n);
		}
	}
	
	/**
	 * Count "string" appear with double weight
	 * @param string
	 * @param d
	 */
	public void counter(String string, double d){
		if(strDoubleHashMap.containsKey(string)){
			double dd = strDoubleHashMap.get(string) + d;
			strDoubleHashMap.put(string, dd);
		}else{
			strDoubleHashMap.put(string, d);
		}
	}

	
	/**
	 * Filter the objects that value < threshold
	 * 
	 * @param threshold
	 */
	public void filterValue(int threshold){
		if(strHashMap.size() > 0){
			Iterator<String> iter = strHashMap.keySet().iterator();
			ArrayList<String> rmList = new ArrayList<String>();
			while(iter.hasNext()){
				String key = iter.next();
				int value = strHashMap.get(key);
				if(value < threshold){
					rmList.add(key);
				}
			}
			for(int i = 0; i < rmList.size(); i++){
				strHashMap.remove(rmList.get(i));
			}
		}
		
		else if(intHashMap.size() >0){
			Iterator<Integer> iter = intHashMap.keySet().iterator();
			ArrayList<Integer> rmList = new ArrayList<Integer>();
			while(iter.hasNext()){
				int key = iter.next();
				int value = intHashMap.get(key);
				if(value < threshold){
					rmList.add(key);
				}
			}
			for(int i = 0; i < rmList.size(); i++){
				intHashMap.remove(rmList.get(i));
			}
		}
	}

	/**
	 * Filter the object that key (flag=0)< threshold, key (flag=1)>=threshold
	 * 
	 * @param threshold
	 * @param flag
	 */
	public void filterKey(int threshold, int flag){
		Iterator<Integer> iter = intHashMap.keySet().iterator();
		ArrayList<Integer> rmList = new ArrayList<Integer>();
		while(iter.hasNext()){
			int key = iter.next();
			int value = intHashMap.get(key);
			if(flag==0 && key < threshold){
				rmList.add(key);
			}
			else if(flag == 1 && key >= threshold){
				rmList.add(key);
			}
		}
		for(int i = 0; i < rmList.size(); i++){
			intHashMap.remove(rmList.get(i));
		}
	}
	
	
	/**
	 * Statistic intHashMap, summarize by interval
	 * 
	 * @param interval
	 * @return
	 */
	public HashMap<Integer, Integer> statistic(int interval){
		HashMap<Integer, Integer> statisticMap = new HashMap<Integer, Integer>();
		
		Iterator<Integer> iter = intHashMap.keySet().iterator();
		while(iter.hasNext()){
			int key = iter.next();
			int value = intHashMap.get(key);
			int index = key/interval;
			if(statisticMap.containsKey((index+1) * interval)){
				int c = statisticMap.get((index+1) * interval);
				int newvalue = c + value;
				statisticMap.put((index+1) * interval, newvalue);
			}else{
				statisticMap.put((index+1) * interval, value);
			}
		}
		
		return statisticMap;
	}
	
	
	/**
	 * Return the sum of values
	 * 
	 * @return
	 */
	public int sumValue(){
		int sum = 0;
		
		if(intHashMap.size() > 0){
			Iterator<Integer> iter = intHashMap.keySet().iterator();
			while(iter.hasNext()){
				int key = iter.next();
				int value = intHashMap.get(key);
				sum += value;
			}
		}
		else if (strHashMap.size() > 0){
			Iterator<String> iter = strHashMap.keySet().iterator();
			while(iter.hasNext()){
				String key = iter.next();
				int value = strHashMap.get(key);
				sum += value;
			}
		}
		
		return sum;
	}
	
	
	/**
	 * Return the sum of values
	 * 
	 * @param map
	 * @return
	 */
	public int sumValue(HashMap<Integer, Integer> map){
		int sum = 0;
		Iterator<Integer> iter = map.keySet().iterator();
		while(iter.hasNext()){
			int key = iter.next();
			int value = map.get(key);
			sum += value;
		}
		return sum;
	}
	
	/**
	 * Return the sum of key*value
	 * 
	 * @return
	 */
	public int sumKeyValue(){
		int sum = 0;
		if(intHashMap.size() > 0){
			Iterator<Integer> iter = intHashMap.keySet().iterator();
			while(iter.hasNext()){
				int key = iter.next();
				int value = intHashMap.get(key);
				sum += key * value;
			}
		}
		return sum;
		
	}
	
	
	/**
	 * Calculate cumulative distribution
	 * 
	 * @return
	 */
	public HashMap<Integer, Double> cumulativeDistribution(){
		HashMap<Integer,Double> cdf = new HashMap<>();
		int sum = sumValue();
		Integer[] key = null;
		
		if(intHashMap.size() > 0){
			key = (Integer[]) intHashMap.keySet().toArray();
			Arrays.sort(key); 
			for (int i = 0; i < key.length; i++){
				double proportion = (double)  intHashMap.get(key[i]) / sum;
				for(int j = i-1; j >= 0; j--){
					proportion += cdf.get(key[j]);
				}
				cdf.put(key[i], proportion);
			}
			
		}
		
		return cdf;
	}
	
	
	/**
	 * Return the cumulative distribution of given map
	 * 
	 * 
	 * @param map
	 * @return
	 */
	public HashMap<Integer, Double> cumulativeDistribution(HashMap<Integer, Integer> map){
		HashMap<Integer,Double> cdf = new HashMap<>();
		int sum = sumValue(map);
		Object[] key = null;
		
		
		if(map.size() > 0){
			key = map.keySet().toArray();
			Arrays.sort(key); 
			for (int i = 0; i < key.length; i++){
				double proportion = (double)  map.get(key[i]) / sum;
				if(i > 0){
					proportion += cdf.get(key[i-1]);
				}
				cdf.put((int)key[i], proportion);
			}
			
		}
		
		return cdf;
	}
	
	
	
	
	
	public HashMap<Integer, Integer> getIntHashMap() {
		return intHashMap;
	}


	public HashMap<String, Integer> getStrHashMap() {
		return strHashMap;
	}


	public HashMap<String, Double> getStrDoubleHashMap() {
		return strDoubleHashMap;
	}
	
	
	
}
