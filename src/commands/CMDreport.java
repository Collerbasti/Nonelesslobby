package commands;

import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CMDreport implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)  
	
	{			
    int Count = Main.Main.rpt.getInt("Report.Count");
	ArrayList<String> ReportList = new ArrayList<String>();
	if(sender instanceof Player) {
		if(args.length == 1) {
		Player p = (Player) sender;
		  p.sendMessage("Bitte gebe /report [Spieler] [Grund] ein");
		  return true;
	}else {
		if(args.length == 2) {
		  Player p = (Player) sender;
		  
		  
		  StringBuilder sb = new StringBuilder();

		  for(int i = 1; i >= args.length; i++) {
		    sb.append(args[i] + " ");
		  }

		  sb.setLength(sb.length() - 1);
		  String text = sb.toString();
		  
		  p.sendMessage("Der Report wird Gesendet");
		  Main.Main.rpt.set(args[0]+".Reports", args[0]);
		  ReportList.addAll(Main.Main.rpt.getStringList(args[0]+".List"));
		  ReportList.add(text);
		  Main.Main.rpt.set(args[0]+".List",ReportList);
		  Main.Main.rpt.set("Report.Count", Count+1);
		  Main.Main.rpt.set(args[0]+".Number",Count+1);
		  p.sendMessage("Die Report Nummer lautet :"+ (Count+1)+" Bitte für alle Fälle aufbewaren");
		try {
			Main.Main.rpt.save(Main.Main.Reports);
		}catch (IOException e) {
			e.printStackTrace();
			
		}
	return true;
	}
}
	return true;
}
	return true;
	}
	}