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
		  p.sendMessage("Der Report wird Gesendet");
		  Main.Main.rpt.set(p.getInventory().getItemInMainHand().getType()+".Reports", args[0]);
		  ReportList.addAll(Main.Main.rpt.getStringList("Report.List"));
		  ReportList.add(args.length==2 );
		  Main.Main.rpt.set("Report.List",ReportList);
		  Main.Main.rpt.set("Report.Count", Count+1);
		  
		  
		try {
			Main.Main.rpt.save(Main.Main.Reports);
		}catch (IOException e) {
			e.printStackTrace();
			
		}
	return false;
	}
}
	return false;
}
	return false;
	}
	}