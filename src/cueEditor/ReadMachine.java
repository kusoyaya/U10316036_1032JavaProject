package cueEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMachine {
	Scanner input;
	File file;
	Pattern pattern;
	Matcher matcher;
	String[] album;
	String[][] track;
	ArrayList<String> main = new ArrayList<String>();
	ArrayList<String> trackTitle = new ArrayList<String>();
	ArrayList<String> trackPerformer = new ArrayList<String>();
	ArrayList<String> trackMinuteIndex = new ArrayList<String>();
	ArrayList<String> trackSecondIndex = new ArrayList<String>();
	ArrayList<String> trackFrameIndex = new ArrayList<String>();
	boolean isBeforeTrackOne = true;
	
	public ReadMachine(String path){
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
				System.out.println("hi");
			}
				
		}
	}
	
	public void mainInfo(String s){
		pattern = Pattern.compile("TITLE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			main.add(takeFromIt(matcher.group()));
		}
		pattern = Pattern.compile("PERFORMER\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			main.add(takeFromIt(matcher.group()));
		}
		pattern = Pattern.compile("FILE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			main.add(takeFromIt(matcher.group()));
		}
	}
	
	public void trackInfo(String s){
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
	
	public String takeFromIt(String s){
		String temp = "";
		pattern = Pattern.compile("(?<=\").*(?=\")");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			temp = matcher.group();
		}
		return temp;
	}
	
	public void addToIndex(String s){
		String[] temp = s.split(":");
		trackMinuteIndex.add(temp[0].substring(temp[0].length()-2));
		trackSecondIndex.add(temp[1]);
		trackFrameIndex.add(temp[2]);
	}
	
	public void transToArray(){
		album = new String[main.size()];
		track = new String[trackTitle.size()][5];
		for(int i = 0; i <main.size(); i++){
			album[i] = main.get(i);
		}
		for(int i = 0; i < trackTitle.size() ; i++){
			track[i][0] = trackTitle.get(i);
			track[i][1] = trackPerformer.get(i);
			track[i][2] = trackMinuteIndex.get(i);
			track[i][3] = trackSecondIndex.get(i);
			track[i][4] = trackFrameIndex.get(i);
		}
	}
	
	
	public static void main(String[] args){
		ReadMachine a = new ReadMachine("/Users/nasirho/Desktop/test.cue");
		System.out.println(a.main);
		a.transToArray();
		for(String s : a.album){
			System.out.println(s);
		}
		for(String[] ss : a.track){
			for(String s : ss){
				System.out.println(s);
			}
			System.out.println("======");
		}
	}
}
