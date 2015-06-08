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
	public static final int TRACK_ALBUM_TITLE = 8;
	public static final int TRACK_ALBUM_PERFORMER = 9;
	public static final int TRACK_GENRE = 10;
	public static final int TRACK_DATE = 11;
	public static final int FILE_PATH = 12;
	public static final int TRACK_COMMENT = 13;
	
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
			System.out.println(System.getProperty("user.dir"));
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
	
	public TagReadMachine(String[] path) throws Exception{
		this.filePath = path;
		
		track = new Object[path.length][14];
		
		for(int i = 0; i < path.length; i++){
			File src = new File(path[i]);
			
			AudioFile f = AudioFileIO.read(src);
			Tag tag = f.getTag();
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
			track[i][TagReadMachine.FILE_PATH] = path[i];
			track[i][TagReadMachine.TRACK_COMMENT] = tag.getFirst(FieldKey.COMMENT);
			
		}
	}
	
	public String[] getAlbum(){
		return album;
	}
	
	public static String[] getAlbum(Object[][] track){
		String[] album = new String[7];
		
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
			if(album[ReadMachine.ALBUM_COMMENT] != null && !album[ReadMachine.ALBUM_COMMENT].equalsIgnoreCase((String)track[i][TagReadMachine.TRACK_COMMENT])){
				album[ReadMachine.ALBUM_COMMENT] = "[multi]";
			}else{
				album[ReadMachine.ALBUM_COMMENT] = (String)track[i][TagReadMachine.TRACK_COMMENT];
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
	
	public BufferedImage getNowCover(boolean resizeOrNot){
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
	
	public static void writeTag(Object[][] trackInfo) throws Exception{
		for(Object[] oa : trackInfo){
			File src = new File(""+oa[FILE_PATH]);
			
			AudioFile f = AudioFileIO.read(src);
			Tag tag = f.getTag();
			tag.setField(FieldKey.TRACK, ""+oa[TRACK_ORDER]);
			tag.setField(FieldKey.TRACK_TOTAL, ""+oa[TRACK_TOTAL]);
			tag.setField(FieldKey.TITLE, ""+oa[TRACK_TITLE]);
			tag.setField(FieldKey.ARTIST,""+oa[TRACK_PERFORMER]);
			tag.setField(FieldKey.COMPOSER,""+oa[TRACK_COMPOSER]);
			tag.setField(FieldKey.ALBUM,""+oa[TRACK_ALBUM_TITLE]);
			tag.setField(FieldKey.ALBUM_ARTIST,""+oa[TRACK_ALBUM_PERFORMER]);
			tag.setField(FieldKey.GENRE,""+oa[TRACK_GENRE]);
			tag.setField(FieldKey.YEAR,""+oa[TRACK_DATE]);
			tag.setField(FieldKey.COMMENT,""+oa[TRACK_COMMENT]);
			f.commit();	
		}
	}
	
}
