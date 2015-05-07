package cueEditor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class WriteMachine {
	File file;
	PrintWriter pWriter;
	String[] album;
	Object[][] track;
	
	public WriteMachine(String outputPath ,String[] albumInfo,Object[][] trackInfo){
		album = albumInfo;
		track = trackInfo;
		file = new File(outputPath);
		try {
			pWriter = new PrintWriter(file,"UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		writeAlbum();
		writeTrack();
		pWriter.close();
	}
	
	private void writeAlbum(){
		pWriter.println("REM GENRE \""+album[4]+"\"");
		pWriter.println("REM DATE \""+album[5]+"\"");
		pWriter.println("PERFORMER \""+album[1]+"\"");
		pWriter.println("TITLE \""+album[0]+"\"");
		pWriter.println("FILE \""+album[2]+"\" WAVE");//尚未確定是否要偵測副檔名來決定最後的字
	}
	
	private void writeTrack(){
		for(Object[] oneTrack:track){
			pWriter.printf("\tTRACK %02d AUDIO\n",(int)oneTrack[0]);
			pWriter.printf("\t\tTITLE \"%s\"\n",(String)oneTrack[1]);
			pWriter.printf("\t\tPERFORMER \"%s\"\n",(String)oneTrack[2]);
			pWriter.printf("\t\tINDEX 01 %02d:%02d:%02d\n",(int)oneTrack[3],(int)oneTrack[4],(int)oneTrack[5]);
		}
	}
	
	
}
