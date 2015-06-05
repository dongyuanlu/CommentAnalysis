package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class ReadFileNameFromFolder {
	
	public static ArrayList<String> readFileFromFolder(String folderPath){
		ArrayList<String> fileList = new ArrayList<String>();
		
		File folder = new File(folderPath);
		String[] files = folder.list();
		
		for(int i = 0; i< files.length; i++){
			String fileName = files[i];
			fileName = fileName.replace(".xml", "");
			fileList.add(fileName);
		}
		
		return fileList;
	}

	
	
	public static void main(String[] args){
		ReadFileNameFromFolder reader = new ReadFileNameFromFolder();
		
		ArrayList<String> list = reader.readFileFromFolder("E:/WORK/Cross/OnSampleDATAset/burstTimeLine2level_gamma01_combined_sample/");
		
		Iterator<String> iter = list.iterator();
		int i = 0;
		while(iter.hasNext()){
//			System.out.println(iter.next());
			System.out.println("userList[" + i + "] = \"" + iter.next() + "\";");
			i++;
		}
		
	}
}
