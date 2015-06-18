package util;

public class Combine {

	
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
			return str1+"_"+str2;
		}
		else{
			return str2+"_"+str1;
		}
	}
}
