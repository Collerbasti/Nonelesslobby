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
		  
		  
		  int ArgsCount = args.length;

		  ReportList.addAll(Main.Main.rpt.getStringList(args[0]+".List"));
		  while(ArgsCount > 1) {
		    
			  ArgsCount = ArgsCount -1;
		  String text = args[ArgsCount];
		  //  report Collerbasti Grunz
		  // args[-1]  args[0]   args[1]
 		  
		  ReportList.add(text);
		  
		  }
		  p.sendMessage("Der Report wird Gesendet");
		  Main.Main.rpt.set(args[0]+".Reports."+Count+1, p.getName());
		  Main.Main.rpt.set(args[0]+".List"+Count+1,ReportList);
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