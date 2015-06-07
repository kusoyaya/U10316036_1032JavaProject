package cueEditor;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;

public class TagReadMachine {
	public static final int TRACK_ORDER = 0;
	public static final int TRACK_TOTAL = 1;
	public static final int TRACK_TITLE = 2;
	public static final int TRACK_PERFORMER = 3;
	public static final int TRACK_COMPOSER = 4;
	public static final int TRACK_MINUTE = 5;
	public static final int TRACK_SECOND = 6;
	public static final int TRACK_RATE = 7;
	public static final int TRACK_ALBUM_TITLE = 7;
	public static final int TRACK_ALBUM_PERFORMER = 8;
	public static final int TRACK_GENRE = 9;
	public static final int TRACK_DATE = 10;
	
	private String[] filePath;
	private String[] album;
	private Object[][] track;
	private BufferedImage[] coverArray;
	private int i = -1;
	
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
			System.out.println(tag.getFirst(FieldKey.TRACK_TOTAL));
			System.out.println(f.getAudioHeader().getTrackLength());
			System.out.println(f.getAudioHeader().getBitRate());
			System.out.println(f.getAudioHeader().getBitRateAsNumber());
			System.out.println(System.currentTimeMillis());
			Artwork cover = tag.getFirstArtwork();
			System.out.println(System.currentTimeMillis());
			BufferedImage coverImage = ImageIO.read(new ByteArrayInputStream(cover.getBinaryData()));
			System.out.println(System.currentTimeMillis());
			JOptionPane.showMessageDialog(null, new ImageIcon(coverImage));
			System.out.println(System.currentTimeMillis());
		} catch (Exception e){
			e.printStackTrace();
		}
		System.out.println(src.getAbsolutePath());
		System.out.println(src.getParent());
		System.out.println(src.getName().split("\\.")[src.getName().split("\\.").length-1]);
		
	}
	
	public TagReadMachine(String[] path){
		this.filePath = path;
		album = new String[6];
		track = new Object[path.length][11];
		for(int i = 0; i < path.length; i++){
			File src = new File(path[i]);
			try{
				AudioFile f = AudioFileIO.read(src);
				Tag tag = f.getTag();
				if(album[ReadMachine.ALBUM_TITLE] != null && !album[ReadMachine.ALBUM_TITLE].equalsIgnoreCase(tag.getFirst(FieldKey.ALBUM))){
					album[ReadMachine.ALBUM_TITLE] = "[multi]";
				}else{
					album[ReadMachine.ALBUM_TITLE] = tag.getFirst(FieldKey.ALBUM);
				}
				if(album[ReadMachine.ALBUM_PERFORMER] != null && !album[ReadMachine.ALBUM_PERFORMER].equalsIgnoreCase(tag.getFirst(FieldKey.ARTIST))){
					album[ReadMachine.ALBUM_PERFORMER] = "[multi]";
				}else{
					album[ReadMachine.ALBUM_PERFORMER] = tag.getFirst(FieldKey.ARTIST);
				}
				if(album[ReadMachine.ALBUM_GENRE] != null && !album[ReadMachine.ALBUM_GENRE].equalsIgnoreCase(tag.getFirst(FieldKey.GENRE))){
					album[ReadMachine.ALBUM_GENRE] = "[multi]";
				}else{
					album[ReadMachine.ALBUM_GENRE] = tag.getFirst(FieldKey.GENRE);
				}
				if(album[ReadMachine.ALBUM_DATE] != null && !album[ReadMachine.ALBUM_DATE].equalsIgnoreCase(tag.getFirst(FieldKey.YEAR))){
					album[ReadMachine.ALBUM_DATE] = "[multi]";
				}else{
					album[ReadMachine.ALBUM_DATE] = tag.getFirst(FieldKey.YEAR);
				}
				track[i][TagReadMachine.TRACK_ORDER] = Integer.parseInt(tag.getFirst(FieldKey.TRACK));
				track[i][TagReadMachine.TRACK_TOTAL] = Integer.parseInt(tag.getFirst(FieldKey.TRACK_TOTAL));
				track[i][TagReadMachine.TRACK_TITLE] = tag.getFirst(FieldKey.TITLE);
				track[i][TagReadMachine.TRACK_PERFORMER] = tag.getFirst(FieldKey.ARTIST);
				track[i][TagReadMachine.TRACK_COMPOSER] = tag.getFirst(FieldKey.COMPOSER);
				track[i][TagReadMachine.TRACK_MINUTE] = f.getAudioHeader().getTrackLength() / 60;
				track[i][TagReadMachine.TRACK_SECOND] = f.getAudioHeader().getTrackLength() % 60;
				track[i][TagReadMachine.TRACK_RATE] = f.getAudioHeader().getBitRateAsNumber();
				track[i][TagReadMachine.TRACK_ALBUM_TITLE] = tag.getFirst(FieldKey.ALBUM);
				track[i][TagReadMachine.TRACK_ALBUM_PERFORMER] = tag.getFirst(FieldKey.ALBUM_ARTIST);
				track[i][TagReadMachine.TRACK_GENRE] = tag.getFirst(FieldKey.GENRE);
				track[i][TagReadMachine.TRACK_DATE] = tag.getFirst(FieldKey.YEAR);
			}catch(Exception e){
				
			}
		}
	}
	
	public String[] getAlbum(){
		return album;
	}
	
	public static String[] getAlbum(Object[][] track){
		String[] album = new String[6];
		
		for(int i = 0 ; i < track.length; i++){
			if(album[ReadMachine.ALBUM_TITLE] != null && !album[ReadMachine.ALBUM_TITLE].equalsIgnoreCase((String)track[i][TagReadMachine.TRACK_ALBUM_TITLE])){
				album[ReadMachine.ALBUM_TITLE] = "[multi]";
			}else{
				album[ReadMachine.ALBUM_TITLE] = (String)track[i][TagReadMachine.TRACK_ALBUM_TITLE];
			}
			if(album[ReadMachine.ALBUM_PERFORMER] != null && !album[ReadMachine.ALBUM_PERFORMER].equalsIgnoreCase((String)track[i][TagReadMachine.TRACK_ALBUM_PERFORMER])){
				album[ReadMachine.ALBUM_PERFORMER] = "[multi]";
			}else{
				album[ReadMachine.ALBUM_PERFORMER] = (String)track[i][TagReadMachine.TRACK_ALBUM_PERFORMER];
			}
			if(album[ReadMachine.ALBUM_GENRE] != null && !album[ReadMachine.ALBUM_GENRE].equalsIgnoreCase((String)track[i][TagReadMachine.TRACK_GENRE])){
				album[ReadMachine.ALBUM_GENRE] = "[multi]";
			}else{
				album[ReadMachine.ALBUM_GENRE] = (String)track[i][TagReadMachine.TRACK_GENRE];
			}
			if(album[ReadMachine.ALBUM_DATE] != null && !album[ReadMachine.ALBUM_DATE].equalsIgnoreCase((String)track[i][TagReadMachine.TRACK_DATE])){
				album[ReadMachine.ALBUM_DATE] = "[multi]";
			}else{
				album[ReadMachine.ALBUM_DATE] = (String)track[i][TagReadMachine.TRACK_DATE];
			}
		}
		
		return album;
	}
	public Object[][] getTrack(){
		return track;
	}
	
	public void setCoverArray(){
		coverArray = new BufferedImage[filePath.length];
		
		for(int i = 0; i < filePath.length; i++){
			File src = new File(filePath[i]);
			try{
				AudioFile f = AudioFileIO.read(src);
				Tag tag = f.getTag();
				Artwork cover = tag.getFirstArtwork();
				coverArray[i] = ImageIO.read(new ByteArrayInputStream(cover.getBinaryData()));
			}catch(Exception e){
				
			}
		}
	}
	
	public BufferedImage getNextCover(boolean resizeOrNot){
		i++;
		if(i >= coverArray.length)
			i = coverArray.length - 1;
		
		BufferedImage image = coverArray[i];
		
		if(resizeOrNot)
			image = ServiceMachine.resizeTo300(image);
		
		return image;
	}
	
	public BufferedImage getLastCover(boolean resizeOrNot){
		i--;
		if(i<0)
			i = 0;
		
		BufferedImage image = coverArray[i];
		
		if(resizeOrNot)
			image = ServiceMachine.resizeTo300(image);
		
		return image;
	}
	
	public void writeTag(Object[][] trackInfo){
		for(int i = 0; i < filePath.length;i++){
			File src = new File(filePath[i]);
			try{
				AudioFile f = AudioFileIO.read(src);
				Tag tag = f.getTag();
				tag.setField(FieldKey.TRACK, ""+trackInfo[i][TRACK_ORDER]);
				tag.setField(FieldKey.TRACK_TOTAL, ""+trackInfo[i][TRACK_TOTAL]);
				tag.setField(FieldKey.TITLE, ""+trackInfo[i][TRACK_TITLE]);
				tag.setField(FieldKey.ARTIST,""+trackInfo[i][TRACK_PERFORMER]);
				tag.setField(FieldKey.COMPOSER,""+trackInfo[i][TRACK_COMPOSER]);
				tag.setField(FieldKey.ALBUM,""+trackInfo[i][TRACK_ALBUM_TITLE]);
				tag.setField(FieldKey.ALBUM_ARTIST,""+trackInfo[i][TRACK_ALBUM_PERFORMER]);
				tag.setField(FieldKey.GENRE,""+trackInfo[i][TRACK_GENRE]);
				tag.setField(FieldKey.YEAR,""+trackInfo[i][TRACK_DATE]);
				f.commit();
				JOptionPane.showMessageDialog(null, "寫入完成");
			}catch(Exception e){
				JOptionPane.showMessageDialog(null, e.getMessage(), "寫入錯誤!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
