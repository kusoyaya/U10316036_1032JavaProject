package cueEditor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class WriteMachine {
	private File file;
	private PrintWriter pWriter;
	private String[] album;
	private Object[][] track;
	private int formatNumber;
	private String[] forMatArray = {"WAVE","WAVE","MP3","AIFF"};
	
	public WriteMachine(String outputPath ,String[] albumInfo,Object[][] trackInfo, int formatNumber) throws Exception{
		album = albumInfo;
		track = trackInfo;
		file = new File(outputPath);
		this.formatNumber = formatNumber;
		
		pWriter = new PrintWriter(file,"UTF-8");
		writeAlbum();
		writeTrack();
		pWriter.close();
	}
	
	private void writeAlbum(){
		pWriter.println("REM GENRE \""+album[ReadMachine.ALBUM_GENRE]+"\"");
		pWriter.println("REM DATE \""+album[ReadMachine.ALBUM_DATE]+"\"");
		pWriter.println("PERFORMER \""+album[ReadMachine.ALBUM_PERFORMER]+"\"");
		pWriter.println("TITLE \""+album[ReadMachine.ALBUM_TITLE]+"\"");
		pWriter.println("FILE \""+album[ReadMachine.ALBUM_FILE]+"\" "+forMatArray[formatNumber]);
	}
	
	private void writeTrack(){
		for(Object[] oneTrack:track){
			pWriter.printf("\tTRACK %02d AUDIO\n",(int)oneTrack[ReadMachine.TRACK_ORDER]);
			pWriter.printf("\t\tTITLE \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_TITLE]);
			pWriter.printf("\t\tPERFORMER \"%s\"\n",(String)oneTrack[ReadMachine.TRACK_PERFORMER]);
			pWriter.printf("\t\tINDEX 01 %02d:%02d:%02d\n",(int)oneTrack[ReadMachine.TRACK_MINUTEINDEX],(int)oneTrack[ReadMachine.TRACK_SECONDINDEX],(int)oneTrack[ReadMachine.TRACK_FRAMEINDEX]);
		}
	}
	
	
}
