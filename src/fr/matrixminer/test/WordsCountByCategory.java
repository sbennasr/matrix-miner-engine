package fr.matrixminer.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class WordsCountByCategory {
	File dir;
	private static List<File> plFiles = new ArrayList<File>();
	@Test
	public void test() throws IOException {
		File path;
		File dir1, dir2, dir3, dir4, dir5, dir6, dir7, dir8, dir9;
		List<File> categList = new ArrayList<File>();
		int total = 0;
		
		dir1 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\All Printers");
		dir2 = new File("C:\\Users\\sbennasr\\Downloads\\bestbuy-dataset\\Digital SLR Cameras");
		dir3 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\Dishwashers");
		dir4 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\Laptops");
		dir5 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\No-Contract Phones");
		dir6 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\Ranges");
		dir7 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\Refrigerators");
		dir8 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\TVs");
		dir9 = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\Washing Machines");
		
		
		categList.add(dir1); categList.add(dir2); categList.add(dir3); categList.add(dir4);
		categList.add(dir5);categList.add(dir6);categList.add(dir7);categList.add(dir8);categList.add(dir9);
		
		
		for(File dir: categList){
		File[] plFilesArray = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt") && !name.equals("products.txt");
			}
		});
		
		plFiles = new ArrayList<File>();
		for (File file : plFilesArray) {
			plFiles.add(file);
		}
	
		for (int j = 0; j < plFiles.size(); j++) {
			path = plFiles.get(j);
			total += countWordsByFile(path.getPath());
		}
		
		}	
		System.out.println("total words in all categ: "+ total);
	}
	
	public void testAvgWordsbyFile() throws IOException {
		File path;
		
		dir = new File("C:\\Users\\sbennasr\\FinalSpace\\VMiner\\bestbuy-dataset-no-marketplace\\Washing Machines");
		File[] plFilesArray = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt") && !name.equals("products.txt");
			}
		});
		
		plFiles = new ArrayList<File>();
		for (File file : plFilesArray) {
			plFiles.add(file);
		}
		int total = 0;
		for (int i = 0; i < plFiles.size(); i++) {
			path = plFiles.get(i);
			total += countWordsByFile(path.getPath());
		}
		System.out.println("total words by file: "+ total);
		int avgWords=total/(plFiles.size());
		System.out.println("Avg words by file: "+ avgWords);
	}
	private int countWordsByFile(String file) throws IOException {
		String text = readFile(file);
		String words []= text.split("\\s+");
		return words.length;
		
	}
	public String readFile(String fileName) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}
	
	
}
