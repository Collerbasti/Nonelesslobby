package commands;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Main.Main;

public class CMDHilfe implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		
		
		
		
		
		
		for(Player players : Bukkit.getOnlinePlayers()) {
			
			if(players.hasPermission("Noneless.Admin")) {
				Date date = new Date();
				Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
				calendar.setTime(date);   // assigns calendar to given date 
				int hour = calendar.get(Calendar.HOUR_OF_DAY); // gets hour in 24h format
			
				int Counter = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour));
				if(hour + 1 == 25) {
					hour = hour -24;
				}
				
				
				int Counter2 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+1));
				if(hour + 2 == 25) {
					hour = hour -24;
				}
				int Counter3 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+2));
				if(hour + 3 == 25) {
					hour = hour -24;
				}
				int Counter4 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+3));
				if(hour + 4 == 25) {
					hour = hour -24;
				}
				int Counter5 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+4));
				if(hour + 5 == 25) {
					hour = hour -24;
				}
				int Counter6 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+5));
				if(hour + 6 == 25) {
					hour = hour -24;
				}
				int Counter7 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+6));
				if(hour + 7 == 25) {
					hour = hour -24;
				}
				int Counter8 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+7));
				if(hour + 8 == 25) {
					hour = hour -24;
				}
				int Counter9 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+8));
				if(hour + 9 == 25) {
					hour = hour -24;
				}
				int Counter10 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+9));
				if(hour + 10 == 25) {
					hour = hour -24;
				}
				int Counter11 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+10));
				if(hour + 11 == 25) {
					hour = hour -24;
				}
				int Counter12 = Main.AOnline.getInt(players.getName()+"."+Integer.toString(hour+11));
				
				
				
				
				int Counter_0;
				int Counter_1;
				int Counter_2;
				int Counter_3;
				int Counter_4;
				int Counter_5;
				
				int timer1;
				int timer2;
				int timer3;
				int timer4;
				int timer5;
				int timer6;
				
				int timer11;
				int timer12;
				int timer13;
				
				int timer111;
				int timer112;
				int timer113;
				
				int Counter_01;
				int Counter_02;
				int Counter_03;
				int Counter_011;

				int Counter_0111;
				
				
				if(Counter<Counter2) {
					Counter_0 = Counter2;
					timer1 = hour+1;
				}else {
					Counter_0 = Counter;
					timer1=hour;
				}
				
				
				if(Counter3<Counter4) {
					Counter_1 = Counter4;
					timer2=hour+3;
				}else {
					Counter_1 = Counter3;
					timer2=hour+2;
				}

				if(Counter5<Counter6) {
					Counter_2 = Counter6;
					timer3=hour+5;
				}else {
					Counter_2 = Counter5;
					timer3=hour+4;
				}

				if(Counter7<Counter8) {
					Counter_3 = Counter8;
					timer4=hour+7;
				}else {
					Counter_3 = Counter7;
					
					timer4=hour+6;
				}

				if(Counter9<Counter10) {
					Counter_4 = Counter10;
					timer5 = hour+9;
				}else {
					Counter_4 = Counter9;
					timer5=hour+8;
				}

				if(Counter11<Counter12) {
					Counter_5 = Counter12;
					timer6=hour+11;
				}else {
					Counter_5 = Counter11;
					timer6=hour+10;
				}
				
				
				if(Counter_0<Counter_1) {
					Counter_01 = Counter_1;
					timer11 = timer2;
				}else {
					Counter_01 = Counter_0;
					timer11=timer1;
				}
				
				if(Counter_2<Counter_3) {
					Counter_02 = Counter_3;
					timer12=timer4;
				}else {
					Counter_02 = Counter_2;
				timer12=timer3;
				}
				
				if(Counter_4<Counter_5) {
					Counter_03 = Counter_5;
				timer13=timer6;
				}else {
					Counter_03 = Counter_4;
				timer13=timer5;
				}
				
				
				if(Counter_01<Counter_02) {
					Counter_011 = Counter_02;
					timer111=timer12;
				}else {
					Counter_011 = Counter_01;
					timer111=timer11;
				}
				
				if(Counter_011<Counter_03) {
					Counter_0111 = Counter_03;
					
					timer112=timer13;
				}else {
					Counter_0111 = Counter_011;
					timer112=timer111;
				}
				
				int endCounter= Counter_0111;
				int endTimer = timer112;
			sender.sendMessage(players.getName()+" ist Wahrscheinlich so gegen "+Integer.toString(endTimer)+":00 online");
			if(sender == players) {
				sender.sendMessage(Integer.toString(endCounter)+" Pnk");
			}
			}
		}
		
		// TODO Auto-generated method stub
		return true;
	}

}
