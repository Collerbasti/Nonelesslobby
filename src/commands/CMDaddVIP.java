package commands;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;

public class CMDaddVIP implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)  
	{
 
		if(sender instanceof Player) {
		}else {
		if(Main.Frdb.getBoolean(args[0]+".VIP.Enable")){
			Date Time = (Date) Main.Frdb.get(args[0]+".VIP.Expression");
			Time.setMonth(Time.getMonth()+1);
			Main.Frdb.set(args[0]+".VIP.Enable", true);
			Main.Frdb.set(args[0]+".VIP.Expression", Time);
		}else {
		Date Time = Calendar.getInstance().getTime();
		Time.setMonth(Time.getMonth()+1);
		Main.Frdb.set(args[0]+".VIP.Enable", true);
		Main.Frdb.set(args[0]+".VIP.Expression", Time);
		
		
	}}
		return true;
		
	}

}
