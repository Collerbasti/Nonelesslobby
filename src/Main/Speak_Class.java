package Main;


import java.io.IOException;

import java.net.URL;


public class Speak_Class {

	public static void Speak(String Text) throws IOException {
		
		
		new URL("https://noneless.de/currentTrack.php?key=Threams&name="+Text.replaceAll(" ", "%20")).openStream().close();
			
		
	}
}
