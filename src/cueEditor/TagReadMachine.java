package cueEditor;

import java.io.File;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

public class TagReadMachine {
	private String[] album;
	private Object[][] track;
	
	public static void main(String[] args){
		File src = new File("/Users/nasirho/Desktop/test.m4a");
		try {
			AudioFile f = AudioFileIO.read(src);
			Tag tag = f.getTag();
			System.out.println(tag.getFirst(FieldKey.ARTIST));
			System.out.println(tag.getFirst(FieldKey.TITLE));
			System.out.println(tag.getFirst(FieldKey.ALBUM));
			System.out.println(tag.getFirst(FieldKey.ALBUM_ARTIST));
			System.out.println(tag.getFirst(FieldKey.TRACK));
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public TagReadMachine(String[] path){
		album = new String[6];
		track = new Object[path.length][6];
		for(int i = 0; i < path.length; i++){
			File src = new File(path[i]);
			try{
				AudioFile f = AudioFileIO.read(src);
				Tag tag = f.getTag();
				album[ReadMachine.ALBUM_TITLE] = tag.getFirst(FieldKey.ALBUM);
				album[ReadMachine.ALBUM_PERFORMER] = tag.getFirst(FieldKey.ALBUM_ARTIST);
				track[i][ReadMachine.TRACK_ORDER] = tag.getFirst(FieldKey.TRACK);
				track[i][ReadMachine.TRACK_TITLE] = tag.getFirst(FieldKey.TITLE);
				track[i][ReadMachine.TRACK_PERFORMER] = tag.getFirst(FieldKey.ARTIST);
			}catch(Exception e){
				
			}
		}
	}
	
	public String[] getAlbum(){
		return album;
	}
	
	public Object[][] getTrack(){
		return track;
	}
	
}
