package cueEditor;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.json.*;

public class GoogleImageTestDrive {

	

	

	    public static void main(String[] args) {
	    	String script = "tell application \"Finder\"\n"+
					"open (\"/Users/nasirho/Desktop/test.wav\" as POSIX file) using (\"/Applications/MPlayerX.app\" as POSIX file)\n"+
					"tell application \"MPlayerX\"\n"+
						"delay 1\n"+
						"pause\n"+
						"seekto 100\n"+
						"play\n"+
					"end tell\n"+
				"end tell\n";
	        ScriptEngineManager mgr = new ScriptEngineManager();
	        ScriptEngine engine = mgr.getEngineByName("AppleScriptEngine");
	        try {
				engine.eval(script);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	
}
