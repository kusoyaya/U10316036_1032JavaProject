package cueEditor;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.json.*;

public class ServiceMachine {
	
	JSONObject json;
	int i = 0;
	
	public static BufferedImage getAlbumCover(String albumInfo) throws Exception{
		String require = albumInfo+" cover";
    	URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+java.net.URLEncoder.encode(require, "UTF-8"));
        URLConnection connection = url.openConnection();

        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while((line = reader.readLine()) != null) {
            builder.append(line);
        }

        JSONObject json = new JSONObject(builder.toString());
        String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(0).getString("url");

        BufferedImage image = ImageIO.read(new URL(imageUrl));
		return image;
	}
	
	public boolean hasCover(String albumInfo){
		i = 0;
		try{
			String require = albumInfo+" cover";
	    	URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q="+java.net.URLEncoder.encode(require, "UTF-8"));
	        URLConnection connection = url.openConnection();

	        String line;
	        StringBuilder builder = new StringBuilder();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        while((line = reader.readLine()) != null) {
	            builder.append(line);
	        }

	        json = new JSONObject(builder.toString());
	        return true;
		}catch(Exception e){
			return false;
		}
		
	}
	
	public BufferedImage getNextAlbumCover(boolean resizeOrNot) throws Exception{
		i++;
		if(i<0)
			i = 0;
		
		String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(i).getString("url");

        BufferedImage image = ImageIO.read(new URL(imageUrl));
        
        if(resizeOrNot)
        	image = resizeTo300(image);
        	
        return image;
	}
	
	public BufferedImage getNowAlbumCover(boolean resizeOrNot) throws Exception{
		String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(i).getString("url");

        BufferedImage image = ImageIO.read(new URL(imageUrl));
        
        if(resizeOrNot)
        	image = resizeTo300(image);
        	
        return image;
	}
	
	public BufferedImage getLastAlbumCover(boolean resizeOrNot) throws Exception{
		i--;
		if(i<0)
			i = 0;
		
		String imageUrl = json.getJSONObject("responseData").getJSONArray("results").getJSONObject(i).getString("url");

        BufferedImage image = ImageIO.read(new URL(imageUrl));
        
        if(resizeOrNot)
        	image = resizeTo300(image);
        
        return image;
	}
	
	public BufferedImage[] getFourAlbumCover() throws IOException {
		BufferedImage[] imageArray = new BufferedImage[4];
		int tmp = i;
		i = -1;
		for(int j = 0; j < 4 ; j++){
			try {
				imageArray[j] = getNextAlbumCover(false);
			} catch (Exception e) {
				imageArray[j] = ImageIO.read(ServiceMachine.class.getResourceAsStream("/fail.png"));
			}
		}
		i = tmp;
		return imageArray;
	}
	
	public static BufferedImage resizeTo300(BufferedImage oImage){
		BufferedImage rImage = new BufferedImage(300,300,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = rImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(oImage,0,0,300,300,null);
		g2.dispose();
		
		return rImage;
	}
	
	public static void playWithAppleScript(String filePath,String location){
		String script = "set missApp to false\n"+
						"try\n"+
							"tell application \"Finder\"\n"+
								"open (\"/Applications/MplayerX.app\" as POSIX file)\n"+
							"end tell\n"+
						"on error errText number errNum\n"+
							"display dialog \"MPlayerX isn't in your Mac! Please click Download and follow the install instruction.\" "
							+ "buttons {\"Download\"} default button {\"Download\"} with title \"Cannot find MPlayerX\" giving up after 5\n"+
							"open location \"http://mplayerx.org\"\n"+
							"set missApp to true\n"+
						"end try\n"+
						"if missApp = false then\n"+
							"try\n"+
								"tell application \"Finder\"\n"+
									"open (\""+filePath+"\" as POSIX file) using (\"/Applications/MPlayerX.app\" as POSIX file)\n"+
									"tell application \"MPlayerX\"\n"+
										"delay 1\n"+
										"pause\n"+
										"seekto "+location+"\n"+
										"play\n"+
									"end tell\n"+
								"end tell\n"+
							"on error errText number errNum\n"+
								"display alert \"Error\" message \"Cannot find the file!\" as warning\n"+
							"end try\n"+
						"end if\n";
		ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("AppleScriptEngine");
        try {
			engine.eval(script);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
