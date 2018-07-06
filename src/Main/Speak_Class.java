package Main;


import java.io.IOException;

import java.net.URL;



import org.bukkit.Bukkit;

public class Speak_Class {

	public static void Speak(String Text) throws IOException {
		Bukkit.broadcastMessage("Test");
		
		new URL("https://noneless.de/currentTrack.php?name="+Text).openStream().close();
			
		
	}
}
