package cueEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadMachine {
	public static final int ALBUM_TITLE = 0;
	public static final int ALBUM_PERFORMER = 1;
	public static final int ALBUM_FILE = 2;
	public static final int ALBUM_COMPOSER = 3;
	public static final int ALBUM_GENRE = 4;
	public static final int ALBUM_DATE = 5;
	public static final int ALBUM_COMMENT = 6;
	public static final int TRACK_ORDER = 0;
	public static final int TRACK_TITLE = 1;
	public static final int TRACK_PERFORMER = 2;
	public static final int TRACK_COMPOSER = 3;
	public static final int TRACK_MINUTEINDEX = 4;
	public static final int TRACK_SECONDINDEX = 5;
	public static final int TRACK_FRAMEINDEX = 6;
	public static final int TRACK_MINUTEPREINDEX = 7;
	public static final int TRACK_SECONDPREINDEX = 8;
	public static final int TRACK_FRAMEPREINDEX = 9;
	public static final int TRACK_FILE = 10;
	public static final int FORMAT_WAVE = 1;
	public static final int FORMAT_MP3 = 2;
	public static final int FORMAT_AIFF = 3;
	
	private String[] albumInfo;
	private Object[][] trackInfo;
	private ArrayList<String> albumNotSupport = new ArrayList<String>();
	private boolean isMultiFileCue;
	
	private final String[] albumPatternArray = {"\\s*TITLE\\s*\".*\"","\\s*PERFORMER\\s*\".*\"","\\s*FILE\\s*\".*\".*","\\s*SONGWRITER\\s*\".*\"","\\s*REM\\s*GENRE\\s*.*\\s*","\\s*REM\\s*DATE\\s*.*\\s*","\\s*REM\\s*COMMENT\\s*\".*\""};
	private final String[] trackPatternArray =  {"\\s*TITLE\\s*\".*\"","\\s*PERFORMER\\s*\".*\"","\\s*SONGWRITER\\s*\".*\"","\\s*INDEX\\s*01\\s*\\d\\d:\\d\\d:\\d\\d","\\s*INDEX\\s*00\\s*\\d\\d:\\d\\d:\\d\\d"};
	
	
	public ReadMachine(String path ,String encode) throws Exception{
		File src = new File(path);
		Scanner input = new Scanner(src,encode);
		
		int trackTotal = countTrack(path,encode);
		int fileTotal = countFile(path,encode);
		
		if(fileTotal > 1 && fileTotal != trackTotal){
			input.close();
			throw new CueNotSupportException();
		}
			
		albumInfo = new String[7];
		trackInfo = new Object[trackTotal][11];
		
		for(int i = 0; i < albumInfo.length ; i ++)
			albumInfo[i] = "";
		for(Object[] oa : trackInfo)
			for(int i = 0 ; i < oa.length ; i++)
				oa[i] = "";
		
		trackInfo[trackInfo.length-1][0] = trackInfo.length;
		int trackIndexNumber = 0;
		boolean isAlbum = true;
		
		if(fileTotal >1 && fileTotal == trackTotal){
			while(input.hasNextLine())
				if(isAlbum){
					isAlbum = setMultiFileAlbumInfo(input.nextLine());
				}else{
					trackIndexNumber += setMultiFileTrackInfo(input.nextLine(),trackIndexNumber);
				}
			albumInfo[ALBUM_FILE] = "[multi]";
			isMultiFileCue = true;
		}else{
			while(input.hasNextLine())
				if(isAlbum){
					isAlbum = setAlbumInfo(input.nextLine());
				}else{
					trackIndexNumber += setTrackInfo(input.nextLine(),trackIndexNumber);
				}
			isMultiFileCue = false;
		}
		
		input.close();
	}
	
	
	private boolean setAlbumInfo(String s){
		boolean isAlbum = true;
		
		if(s.matches("\\s*TRACK\\s*\\d*\\s*AUDIO\\s*")){
			isAlbum = false;
		}else{
			for(int i = 0 ; i < albumPatternArray.length; i++){
				if(s.matches(albumPatternArray[i])){
					if(i == 4 || i == 5){
						albumInfo[i] = s.split("\\s+")[s.split("\\s+").length -1];
					}else{
						albumInfo[i] = takeFromIt(s);
					}
					return isAlbum;
				}
			}
			albumNotSupport.add(s);
		}
		return isAlbum;
	}
	
	private int setTrackInfo(String s, int trackIndexNumber){
		if(s.matches("\\s*TRACK\\s*\\d*\\s*AUDIO\\s*")){
			trackInfo[trackIndexNumber][0] = trackIndexNumber+1;
			return 1;
		}else{
			for(int i = 0 ; i < trackPatternArray.length ; i ++){
				if(s.matches(trackPatternArray[i]) && i <3){
					trackInfo[trackIndexNumber][i+1] = takeFromIt(s);
					break;
				}else if(s.matches(trackPatternArray[i]) && i >= 3){
					String[] tmp = s.split(":");
					trackInfo[trackIndexNumber][3 * i - 5] = Integer.parseInt(tmp[0].substring(tmp[0].length() -2));
					trackInfo[trackIndexNumber][3 * i - 4] = Integer.parseInt(tmp[1]);
					trackInfo[trackIndexNumber][3 * i - 3] = Integer.parseInt(tmp[2]);
					//3 * (i - 2 ) + 1
				}
			}
			return 0;
		}	
	}
	
	private boolean setMultiFileAlbumInfo(String s){
		boolean isAlbum = true;
		if(s.matches("\\s*FILE\\s*\".*\".*")){
			trackInfo[0][TRACK_FILE] = takeFromIt(s);
			isAlbum = false;
		}else{
			for(int i = 0; i < albumPatternArray.length; i++){
				if(s.matches(albumPatternArray[i])){
					if(i == 4 || i == 5){
						albumInfo[i] = s.split("\\s+")[s.split("\\s+").length -1];
					}else{
						albumInfo[i] = takeFromIt(s);
					}
				}
			}
			albumNotSupport.add(s);
		}
		return isAlbum;
	}
	
	private int setMultiFileTrackInfo(String s, int trackIndexNumber){
		if(s.matches("\\s*FILE\\s*\".*\".*")){
			trackInfo[trackIndexNumber+1][TRACK_FILE] = takeFromIt(s);
			return 1;
		}else if(s.matches("\\s*TRACK\\s*\\d*\\s*AUDIO\\s*")){
			trackInfo[trackIndexNumber-1][0] = trackIndexNumber;
			return 0;
		}else{
			for(int i = 0 ; i < trackPatternArray.length ; i ++){
				if(s.matches(trackPatternArray[i]) && i <3){
					trackInfo[trackIndexNumber][i+1] = takeFromIt(s);
					break;
				}else if(s.matches(trackPatternArray[i]) && i >= 3){
					String[] tmp = s.split(":");
					trackInfo[trackIndexNumber][3 * i - 5] = Integer.parseInt(tmp[0].substring(tmp[0].length() -2));
					trackInfo[trackIndexNumber][3 * i - 4] = Integer.parseInt(tmp[1]);
					trackInfo[trackIndexNumber][3 * i - 3] = Integer.parseInt(tmp[2]);
					//3 * (i - 2 ) + 1
				}
			}
			return 0;
		}
	}
	
	private int countTrack(String path, String encode) throws Exception{
		int trackTotal =0;
		
		File src = new File(path);
		Scanner input = new Scanner(src,encode);
		
		while(input.hasNextLine())
			if(input.nextLine().matches("\\s*TRACK\\s*\\d*\\s*AUDIO\\s*"))
				trackTotal++;
		
		input.close();
		
		return trackTotal;
	}
	
	private int countFile(String path, String encode) throws Exception{
		int fileTotal = 0;
		
		File src = new File(path);
		Scanner input = new Scanner(src,encode);
		
		while(input.hasNextLine())
			if(input.nextLine().matches("\\s*FILE\\s*\".*\".*"))
				fileTotal++;
		
		input.close();
		return fileTotal;
	}
	
	private String takeFromIt(String s){
		String temp = "";
		Pattern pattern = Pattern.compile("(?<=\").*(?=\")");
		Matcher matcher = pattern.matcher(s);
		while(matcher.find()){
			temp = matcher.group();
		}
		return temp;
	}
	
	public String[] getAlbumInfo(){
		return albumInfo;
	}
	
	public Object[][] getTrackInfo(){
		return trackInfo;
	}
	
	public ArrayList<String> getAlbumNotSupport(){
		return albumNotSupport;
	}
	
	public boolean isMultiFileCue(){
		return isMultiFileCue;
	}
	
}
