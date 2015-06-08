package cueEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CueReadMachine {

	private String[] albumInfo;
	private Object[][] trackInfo;
	private ArrayList<String> albumNotSupport = new ArrayList<String>();
	
	private final String[] albumPatternArray = {"\\s*TITLE\\s*\".*\"","\\s*PERFORMER\\s*\".*\"","\\s*FILE\\s*\".*\".*","\\s*COMPOSER\\s*\".*\"","\\s*REM\\s*GENRE\\s*\".*\"","\\s*REM\\s*DATE\\s*\".*\"","\\s*REM\\s*COMMENT\\s*\".*\""};
	private final String[] trackPatternArray =  {"\\s*TITLE\\s*\".*\"","\\s*PERFORMER\\s*\".*\"","\\s*COMPOSER\\s*\".*\"","\\s*INDEX\\s*01\\s*\\d\\d:\\d\\d:\\d\\d","\\s*INDEX\\s*00\\s*\\d\\d:\\d\\d:\\d\\d"};
	
	
	public CueReadMachine(String path, String encode) throws Exception{
		File src = new File(path);
		Scanner input = new Scanner(src,encode);
		
		albumInfo = new String[7];
		trackInfo = new Object[countTrack(path,encode)][10];
		
		for(int i = 0; i < albumInfo.length ; i ++)
			albumInfo[i] = "";
		for(Object[] oa : trackInfo)
			for(int i = 0 ; i < oa.length ; i++)
				oa[i] = "";
		
		trackInfo[trackInfo.length-1][0] = trackInfo.length;
		int trackIndexNumber = 0;
		boolean isAlbum = true;
		
		while(input.hasNextLine())
			if(isAlbum){
				isAlbum = setAlbumInfo(input.nextLine());
			}else{
				trackIndexNumber += setTrackInfo(input.nextLine(),trackIndexNumber);
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
					albumInfo[i] = takeFromIt(s);
					break;
				}else{
					albumNotSupport.add(s);
				}
			}
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
					trackInfo[trackIndexNumber][3 * i - 5] = tmp[0].substring(tmp[0].length() -2);
					trackInfo[trackIndexNumber][3 * i - 4] = tmp[1];
					trackInfo[trackIndexNumber][3 * i - 3] = tmp[2];
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
	
	private String takeFromIt(String s){
		String temp = "";
		Pattern pattern = Pattern.compile("(?<=\").*(?=\")");
		Matcher matcher = pattern.matcher(s);
		while(matcher.find()){
			temp = matcher.group();
		}
		return temp;
	}
	
	public String[] getAlbum(){
		return albumInfo;
	}
	
	
	public Object[][] getTrack(){
		return trackInfo;
	}
	
	
	public static void main(String[] args) throws Throwable{
		CueReadMachine crm = new CueReadMachine("/Users/nasirho/Desktop/test.cue","UTF-8");
		System.out.println(Arrays.toString(crm.getAlbum()));
		for(Object[]oa:crm.getTrack())
			System.out.println(Arrays.toString(oa));
		
	}
}
