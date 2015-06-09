package cueEditor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class WriteMachine {
	private File file;
	private PrintWriter pWriter;
	private String[] album;
	private Object[][] track;
	private ArrayList<String> albumNotSupported;
	private int formatNumber;
	private String[] forMatArray = {"WAVE","MP3","AIFF"};
	
	public WriteMachine(String outputPath ,String[] albumInfo,Object[][] trackInfo, ArrayList<String> albumNotSupportedInfo ,boolean isMultiFileCue) throws Exception{
		album = albumInfo;
		track = trackInfo;
		albumNotSupported = albumNotSupportedInfo;
		
		file = new File(outputPath);
		
		pWriter = new PrintWriter(file,"UTF-8");
		
		System.out.println(Arrays.toString(albumInfo));
		System.out.println(albumNotSupported.toString());
		
		if(isMultiFileCue){
			writeMultiFileCueAlbum();
			writeMultiFileCueTrack();
		}else{
			writeAlbum();
			writeTrack();
		}
		
		pWriter.close();
	}
	
	private int checkFormat(String s){
		s = s.split("\\.")[s.split("\\.").length-1];
		int result = 0;
		switch(s){
		case"wav":
			result = 0;
			break;
		case"mp3":
			result = 1;
		case"aiff":
			result = 2;
		default:
			result = 0;
		}
		return result;
	}
	
	private void writeAlbum(){
		pWriter.println("REM GENRE \""+album[ReadMachine.ALBUM_GENRE]+"\"");
		pWriter.println("REM DATE \""+album[ReadMachine.ALBUM_DATE]+"\"");
		pWriter.println("REM COMMENT \""+album[ReadMachine.ALBUM_COMMENT]+"\"");
		for(String s : albumNotSupported)
			pWriter.println(s);
		pWriter.println("PERFORMER \""+album[ReadMachine.ALBUM_PERFORMER]+"\"");
		pWriter.println("SONGWRITER \""+album[ReadMachine.ALBUM_COMPOSER]+"\"");
		pWriter.println("TITLE \""+album[ReadMachine.ALBUM_TITLE]+"\"");
		pWriter.println("FILE \""+album[ReadMachine.ALBUM_FILE]+"\" "+forMatArray[checkFormat(album[ReadMachine.ALBUM_FILE])]);
	}
	
	private void writeTrack(){
		for(Object[] oneTrack:track){
			pWriter.printf("\tTRACK %02d AUDIO\n",(int)oneTrack[ReadMachine.TRACK_ORDER]);
			pWriter.printf("\t\tTITLE \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_TITLE]);
			pWriter.printf("\t\tPERFORMER \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_PERFORMER]);
			pWriter.printf("\t\tSONGWRITER \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_COMPOSER]);
			if(!oneTrack[ReadMachine.TRACK_MINUTEPREINDEX].equals(""))
				pWriter.printf("\t\tINDEX 00 %02d:%02d:%02d\n",(int)oneTrack[ReadMachine.TRACK_MINUTEPREINDEX],(int)oneTrack[ReadMachine.TRACK_SECONDPREINDEX],(int)oneTrack[ReadMachine.TRACK_FRAMEPREINDEX]);
			pWriter.printf("\t\tINDEX 01 %02d:%02d:%02d\n",(int)oneTrack[ReadMachine.TRACK_MINUTEINDEX],(int)oneTrack[ReadMachine.TRACK_SECONDINDEX],(int)oneTrack[ReadMachine.TRACK_FRAMEINDEX]);
		}
	}
	
	private void writeMultiFileCueAlbum(){
		pWriter.println("REM GENRE \""+album[ReadMachine.ALBUM_GENRE]+"\"");
		pWriter.println("REM DATE \""+album[ReadMachine.ALBUM_DATE]+"\"");
		pWriter.println("REM COMMENT \""+album[ReadMachine.ALBUM_COMMENT]+"\"");
		for(String s : albumNotSupported)
			pWriter.println(s);
		pWriter.println("PERFORMER \""+album[ReadMachine.ALBUM_PERFORMER]+"\"");
		pWriter.println("SONGWRITER \""+album[ReadMachine.ALBUM_COMPOSER]+"\"");
		pWriter.println("TITLE \""+album[ReadMachine.ALBUM_TITLE]+"\"");
	}
	
	private void writeMultiFileCueTrack(){
		for(Object[] oneTrack : track){
			pWriter.println("FILE \""+oneTrack[ReadMachine.TRACK_FILE]+"\" "+forMatArray[checkFormat(""+oneTrack[ReadMachine.TRACK_FILE])]);
			pWriter.printf("\tTRACK %02d AUDIO\n",(int)oneTrack[ReadMachine.TRACK_ORDER]);
			pWriter.printf("\t\tTITLE \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_TITLE]);
			pWriter.printf("\t\tPERFORMER \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_PERFORMER]);
			pWriter.printf("\t\tSONGWRITER \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_COMPOSER]);
			if(!oneTrack[ReadMachine.TRACK_MINUTEPREINDEX].equals(""))
				pWriter.printf("\t\tINDEX 00 %02d:%02d:%02d\n",(int)oneTrack[ReadMachine.TRACK_MINUTEPREINDEX],(int)oneTrack[ReadMachine.TRACK_SECONDPREINDEX],(int)oneTrack[ReadMachine.TRACK_FRAMEPREINDEX]);
			pWriter.printf("\t\tINDEX 01 %02d:%02d:%02d\n",(int)oneTrack[ReadMachine.TRACK_MINUTEINDEX],(int)oneTrack[ReadMachine.TRACK_SECONDINDEX],(int)oneTrack[ReadMachine.TRACK_FRAMEINDEX]);
		}
		
	}
	
	public void close(){
		pWriter.close();
	}
}
