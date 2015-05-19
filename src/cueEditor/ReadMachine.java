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
	private Object[][] track;
	private int audioFormat;
	private ArrayList<String> trackTitle = new ArrayList<String>();
	private ArrayList<String> trackPerformer = new ArrayList<String>();
	private ArrayList<String> trackMinuteIndex = new ArrayList<String>();
	private ArrayList<String> trackSecondIndex = new ArrayList<String>();
	private ArrayList<String> trackFrameIndex = new ArrayList<String>();
	private boolean isBeforeTrackOne = true;
	public static final int ALBUM_TITLE = 0;
	public static final int ALBUM_PERFORMER = 1;
	public static final int ALBUM_FILE = 2;
	public static final int ALBUM_GENRE = 4;
	public static final int ALBUM_DATE = 5;
	public static final int TRACK_ORDER = 0;
	public static final int TRACK_TITLE = 1;
	public static final int TRACK_PERFORMER = 2;
	public static final int TRACK_MINUTEINDEX = 3;
	public static final int TRACK_SECONDINDEX = 4;
	public static final int TRACK_FRAMEINDEX = 5;
	public static final int FORMAT_WAVE = 1;
	public static final int FORMAT_MP3 = 2;
	public static final int FORMAT_AIFF = 3;
	
	public ReadMachine(String path ,String encode){
		album = new String[6];
		try{
			file = new File(path);
			input = new Scanner(file,encode);
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
		transToArray();
		try{
			audioFormat = getAudioFormat(album[ALBUM_FILE]);
		}catch(Exception e){
			audioFormat = 0;
		}
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
		pattern = Pattern.compile("REM\\s*GENRE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			album[4] = takeFromIt(matcher.group());
		}
		pattern = Pattern.compile("REM\\s*DATE\\s*\".*\"");
		matcher = pattern.matcher(s);
		while(matcher.find()){
			album[5] = takeFromIt(matcher.group());
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
		track = new Object[trackTitle.size()][6];
		
		for(int i = 0; i < trackTitle.size() ; i++){
			track[i][0] = (i+1);
			track[i][1] = trackTitle.get(i);
			track[i][2] = trackPerformer.get(i);
			track[i][3] = Integer.parseInt(trackMinuteIndex.get(i));
			track[i][4] = Integer.parseInt(trackSecondIndex.get(i));
			track[i][5] = Integer.parseInt(trackFrameIndex.get(i));
		}
	}
	
	public String[] getAlbumInfo(){
		return album;
	}
	
	public Object[][] getTrackInfo(){
		return track;
	}
	
	public int getAudioFormat(){
		return audioFormat;
	}
	
	public static int getAudioFormat(String fileName){
		int result = 0;
		String[] temp = fileName.split("\\.");
		switch(temp[temp.length-1]){
		case"wav":
		case"tak":
		case"tta":
		case"ape":
		case"alac":
		case"flac":
		case"m4a":
			result = 1;
			break;
		case"mp3":
			result = 2;
			break;
		case"aiff":
			result = 3;
			break;
		}
		return result;
	}
	
	public static void main(String[] args){
		ReadMachine a = new ReadMachine("/Users/nasirho/Desktop/testgbk.cue","GBK");
		for(String s: a.album){
			System.out.println(s);
		}
		System.out.println("===");
		System.out.println(a.audioFormat);
		System.out.println("===");
		for(Object[] ss : a.track){
			for(Object s : ss){
				System.out.println(s);
			}
			System.out.println("======");
		}
	}
}
