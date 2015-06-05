package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import me.champeau.ld.UberLanguageDetector;

import org.tartarus.snowball.EnglishSnowballStemmerFactory;
import org.tartarus.snowball.util.StemmerException;



import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.cybozu.labs.langdetect.Language;



public class PreProcess {
	
	
//***************************************************	
	/**
	 * Remove @somebody
	 * @param str
	 * @return
	 */
	public static String removeAtSb(String str){
		String newStr = str.replaceAll("@\\w+?[\\s:,')]", "");	//replace: @info
		newStr = newStr.replaceAll("@\\w+?$", "");	//replace: @info
		return newStr;
	}
	
	/**
	 * Remove Http link
	 * @param str
	 * @return
	 */
	public static String removeHttp(String str){
		String newStr = str.replaceAll("http:.*?\\s", "");	//remove http://...
		newStr = newStr.replaceAll("http:.*", "");
		newStr = newStr.replaceAll("https:.*?\\s", "");	//remove http://...
		newStr = newStr.replaceAll("https:.*", "");
		
		newStr = newStr.replaceAll("www\\..*?\\.com", "");	//remove www.aaa.com
		newStr = newStr.replaceAll("\\b[\\s]*\\w*?\\.com", "");	//remove aaa.com
		return newStr;
	}

	
	/**
	 * Remove HTML tags
	 * @param str
	 * @return
	 */
	public static String removeHTML(String str){
		String newStr = str.replaceAll("<.*?>", " ");	//replace: <html>
		return newStr;
	}
	
	
	public static String removePuntuation(String str){
		String newStr = str.replaceAll("[$%=@#&’\\?,:!\\+\\.'\\(\\)\"\\*|/;\\[\\]\\{\\}“”<>-]", " ");	//remove signals
	//	newStr = str.replaceAll("[-]", "");	//remove signals

		return newStr;
	}
	
	
	public static String removeUploadBy(String str){
		String newStr = str.replaceAll("\\s\\w*?:\\w*?=\\w*?\\s", " ");	//remove upload:flickr=...
		newStr = newStr.replaceAll("\\w*?:\\w*?=\\w+", "");	//remove upload:flickr=...
		newStr = newStr.replaceAll("\\w*?=\\w*?\\s", " ");	//remove upload:flickr=...
		newStr = newStr.replaceAll("\\w+?=\\w+", " ");	//remove upload:flickr=...
		
		return newStr;
	}
	
	/**
	 * Remove noisy strings:  RT  &quit;  
	 * @param str
	 * @return
	 */
	public static String removeNoisy(String str){
		String newStr = str.replaceAll("RT", "");	//remove RT
		newStr = newStr.replaceAll("&\\w+;", "");	//remove &quit; &39;
		newStr = newStr.replaceAll("&#\\w+;", ""); //remove &#39;
		newStr = newStr.replaceAll("View Post", "");
		newStr = newStr.replaceAll("[“~♫_̅ ด้้้้้็็็็็้้้้้็็็็็้้้้้้้้็็็็็้ –—█♢£\\?•●➔⦿]", " ");
		return newStr;
	}
	
	/**
	 * Remove numbers
	 * @param str
	 * @return
	 */
	public static String removeNumber(String str){
		String newStr = str.replaceAll("\\b[\\s]*\\w*?\\d+?[\\w]*[\\s]*\\b"," ");	//remove words with numbers, e.g., AK47, Alarsk91
		newStr = newStr.replaceAll("\\d+?\\s", " ");	//remove numbers
		newStr = newStr.replaceAll("\\s\\d+\\b", " ");
		return newStr;
	}
	
	
	public static String removeExtraSpaces(String str){
		String newStr = str.replaceAll("\\s+", " ");	//combine: multiple spaces into one
		if(newStr.startsWith(" ")){
			newStr = newStr.substring(1);
		}
		
		return newStr;
	}
	
	public static String removeExtraEntre(String str){
		String newStr = str.replaceAll("\\r", " ");
		newStr = newStr.replaceAll("\\n", " ");
		
		return newStr;
	}
	
	public static String removeNonEng(String str){
		
		String newStr = str.replaceAll("\\b[\\s]*[^a-z0-9]+[\\s]*\\b", " ");
		
		return newStr;
	}
	
	/**
	 * Preprocess titles
	 * 
	 * @param str
	 * @return
	 */
	public static String preprocessTitle(String str){
		String s = str;
		s = PreProcess.removeHTML(s);
		s = PreProcess.removeHttp(s);
		s = PreProcess.removeAtSb(s);
		s = PreProcess.removeUploadBy(s);
		
		s = PreProcess.removeNoisy(s);

		s = PreProcess.removePuntuation(s);
		s = PreProcess.removeNumber(s);
		s = PreProcess.removeExtraSpaces(s);
		
		return s;
		
	}
	
	/**
	 * Check whether the language of s is English
	 * return true: english
	 * @param s
	 * @return
	 */
	public static boolean checkEng(String s){
		//Method 1:
		UberLanguageDetector detector = UberLanguageDetector.getInstance();
		String lang1 = detector.detectLang(s);
//		System.out.println(language);
		
		//Method 2:
		String lang2 = "";
		ArrayList<Language> list = new ArrayList<Language>();
		try {
			DetectorFactory.loadProfile("data/profiles");
		} catch (LangDetectException e) {

//			e.printStackTrace();
			
		}
		
		Detector det;
		try {
			det = DetectorFactory.create();
			det.append(s);
			lang2 = det.detect();
			
			list = det.getProbabilities();
		} catch (LangDetectException e) {

		//	e.printStackTrace();
		}
		
		if(lang1 == null || lang2 == null){
			return false;
		}
		if(lang1.equals("en") && lang2.equals("en")){
			return true;
		}
		
		if(!lang1.equals("en") && lang2.equals("en")){
			if(list.get(0).lang.equals("en")){
				if(list.get(0).prob > 0.85){
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	public static boolean checkEngSimple(String s){
		//Method 1:
		UberLanguageDetector detector = UberLanguageDetector.getInstance();
		String lang1 = detector.detectLang(s);
		if(lang1 == null){
			return false;
		}
		else if(lang1.equals("en")){
			return true;
		}
		
		return false;
	}
	
	

	
	
	
	/**
	 * TEST
	 * @param args
	 */
	public static void main(String[] args){
		String s = "";
/*		
		BufferedReader brFile;
		try {
			brFile = new BufferedReader(new InputStreamReader
					(new FileInputStream("data/temp.txt"), "utf-8"));
			for(String readline = brFile.readLine(); readline!=null; readline = brFile.readLine()){
				s += readline + "\n";
			}
			brFile.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
*/
/*		
		s = PreProcess.removeHTML(s);
		s = PreProcess.removeHttp(s);
		s = PreProcess.removeAtSb(s);
		s = PreProcess.removeUploadBy(s);
		
		s = PreProcess.removeNoisy(s);

		s = PreProcess.removeSignals(s);
		s = PreProcess.removeNumber(s);
		s = PreProcess.removeExtraSpaces(s);
		
		
		
		*/
		s = PreProcess.preprocessTitle(s);
		System.out.println(s);
		System.out.println(checkEng(s));
		

		
		if(s != null && s.length() > 0  && !s.equals(" ")){
			UberLanguageDetector detector = UberLanguageDetector.getInstance();

			String language = detector.detectLang(s);
			System.out.println(language);
			
			
			try {
				DetectorFactory.loadProfile("data/profiles");
				Detector det = DetectorFactory.create();
				det.append(s);

				String lang = det.detect();
				System.out.println(lang);
				
				ArrayList<Language> list = det.getProbabilities();
				
				for (int i = 0; i < list.size(); i++){
					System.out.println(list.get(i));
				}
			}catch(com.cybozu.labs.langdetect.LangDetectException e){
				e.printStackTrace();
			}
			
			System.out.println(checkEng(s));
			System.out.println(s.matches("\\A\\p{ASCII}*\\z"));
			
		}
		
		System.out.println(s);
		System.out.println(PreProcess.removeNonEng(s));
	}
}
