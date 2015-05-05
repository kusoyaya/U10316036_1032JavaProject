package cueEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMachine {
	private Scanner input;
	private File file;
	private Pattern pattern;
	private Matcher matcher;
	private String[] album ;
	private String[][] track;
	private ArrayList<String> main = new ArrayList<String>();
	private ArrayList<String> trackTitle = new ArrayList<String>();
	private ArrayList<String> trackPerformer = new ArrayList<String>();
	private ArrayList<String> trackMinuteIndex = new ArrayList<String>();
	private ArrayList<String> trackSecondIndex = new ArrayList<String>();
	private ArrayList<String> trackFrameIndex = new ArrayList<String>();
	private boolean isBeforeTrackOne = true;
	
	public ReadMachine(String path){
		album = new String[5];
		try{
			file = new File(path);
			input = new Scanner(file,"UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		while(input.hasNextLine()){
			String s = input.nextLine();
			if(isBeforeTrackOne){
				mainInfo(s);
			}else{
				trackInfo(s);
			}
			if(s.matches("\\s*TRACK\\s*01\\s*AUDIO\\s*")){
				isBeforeTrackOne = false;
			}		
		}
		transToArray();
	}
	
	private void mainInfo(String s){
		pattern = Pattern.compile("TITLE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			album[0] = takeFromIt(matcher.group());
		}
		pattern = Pattern.compile("PERFORMER\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			album[1] = takeFromIt(matcher.group());
		}
		pattern = Pattern.compile("FILE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			album[2] = takeFromIt(matcher.group());
		}
	}
	
	private void trackInfo(String s){
		pattern = Pattern.compile("TITLE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			trackTitle.add(takeFromIt(matcher.group()));
		}
		pattern = Pattern.compile("PERFORMER\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			trackPerformer.add(takeFromIt(matcher.group()));
		}
		pattern = Pattern.compile("INDEX\\s*01\\s*\\d\\d:\\d\\d:\\d\\d");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			addToIndex(matcher.group());
		}
	}
	
	private String takeFromIt(String s){
		String temp = "";
		pattern = Pattern.compile("(?<=\").*(?=\")");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			temp = matcher.group();
		}
		return temp;
	}
	
	private void addToIndex(String s){
		String[] temp = s.split(":");
		trackMinuteIndex.add(temp[0].substring(temp[0].length()-2));
		trackSecondIndex.add(temp[1]);
		trackFrameIndex.add(temp[2]);
	}
	
	private void transToArray(){
		track = new String[trackTitle.size()][5];
		
		for(int i = 0; i < trackTitle.size() ; i++){
			track[i][0] = trackTitle.get(i);
			track[i][1] = trackPerformer.get(i);
			track[i][2] = trackMinuteIndex.get(i);
			track[i][3] = trackSecondIndex.get(i);
			track[i][4] = trackFrameIndex.get(i);
		}
	}
	
	public String[] getAlbumInfo(){
		return album;
	}
	
	public String[][] getTrackInfo(){
		return track;
	}
	
	public static void main(String[] args){
		ReadMachine a = new ReadMachine("/Users/nasirho/Desktop/test.cue");
		System.out.println(a.main);
		
		for(String[] ss : a.track){
			for(String s : ss){
				System.out.println(s);
			}
			System.out.println("======");
		}
	}
}
