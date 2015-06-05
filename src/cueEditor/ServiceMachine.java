package cueEditor;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;

import org.json.*;

public class ServiceMachine {
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
}
