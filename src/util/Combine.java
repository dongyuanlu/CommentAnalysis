package util;

public class Combine {

	private static String tab = " ";
	
	/**
	 * Combine two strings
	 * 
	 * return str1_str2 if str1<str2
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String combineTwoStr(String str1, String str2){
		if(str1.compareTo(str2) <= 0){
			return str1 + tab + str2;
		}
		else{
			return str2 + tab + str1;
		}
	}
	
	public static String[] splitTwoStr(String str1_str2){
		String[] strs = str1_str2.split(tab);
		return strs;
	}
}
