package de.noneless.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static de.noneless.Main.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.MySQL;

import org.bukkit.configuration.file.FileConfiguration;

		





	public class CMDaddFriend implements CommandExecutor  
	{
		
		@Override
		public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
				return true;
			}
			Player p = (Player) sender;
			ArrayList<String> Friends = new ArrayList<>();
			ArrayList<String> p2Friends = new ArrayList<>();
			ArrayList<String> SendedRequestslist = new ArrayList<>();
			ArrayList<String> p2BecameRequestslist = new ArrayList<>();
			ArrayList<String> BecameRequestslist = new ArrayList<>();
			int Count = 0;
			if (args.length == 0) {
				p.sendMessage("Bitte gib einen Spielernamen an.");
				return true;
			}
			if (Frdb.getString(p.getName() + ".Name") != null && Frdb.getString(p.getName() + ".Name").equals(Frdb.getString(args[0] + ".Name"))) {
				p.sendMessage("Du kannst dich nicht selber adden");
				return true;
			}
			if (args.length == 1) {
				boolean Exists = false;
				boolean First = false;
				if (!Frdb.getBoolean(p.getName() + ".Exists")) {
					First = true;
					Frdb.set(p.getName() + ".Exists", true);
					Frdb.set(p.getName() + ".Count", Count);
				}
				int C2 = Frdb.getInt(p.getName() + ".Count");
				if (!First) {
					Friends.addAll(Frdb.getStringList(p.getName() + ".Friends"));
				}
				boolean Test = Friends.contains(args[0]);
				if (Test) {
					Exists = true;
				}
				if (Exists) {
					p.sendMessage("Diesen Freund kannst du nicht mehr adden");
					return true;
				}
				if (C2 == 27) {
					p.sendMessage("Du kannst nicht mehr Freunde haben");
					return true;
				}
				if (Frdb.getBoolean(args[0] + ".isOnline")) {
					SendedRequestslist.addAll(Frdb.getStringList(p.getName() + ".SendetRequests"));
					if (SendedRequestslist.contains(args[0])) {
						p.sendMessage("Du hast " + args[0] + " schon eine Anfrage geschickt");
						return true;
					}
					SendedRequestslist.add(args[0]);
					Frdb.set(p.getName() + ".SendetRequests", SendedRequestslist);
					p2BecameRequestslist.addAll(Frdb.getStringList(args[0] + ".BecameRequests"));
					if (p2BecameRequestslist.contains(p.getName())) {
						p.sendMessage("Hier ist ein Fehler unterlaufen. Bitte kontaktiere den Support (FC:AddFriend.already.exists)");
						return true;
					}
					BecameRequestslist.addAll(Frdb.getStringList(p.getName() + ".BecameRequests"));
					if (BecameRequestslist.contains(args[0])) {
						p.sendMessage("Du hast bereits eine Anfrage von " + args[0] + " erhalten, bitte mit /addfriend accept " + args[0] + " annehmen");
						return true;
					}
					p2BecameRequestslist.add(p.getName());
					Frdb.set(args[0] + ".BecameRequests", p2BecameRequestslist);
					Player p2 = Bukkit.getPlayer(args[0]);
					int BCRCounter = Frdb.getInt(args[0] + ".BCRCounter") + 1;
					Frdb.set(args[0] + ".BCRCounter", BCRCounter);
					int SDCounter = Frdb.getInt(p.getName() + ".SDCounter") + 1;
					Frdb.set(p.getName() + ".SDCounter", SDCounter);
					if (p2 != null) {
						p2.sendMessage("Du hast eine neue Anfrage von " + p.getName() + " bitte mit /addfriend accept " + p.getName() + " annehmen");
					}
					p.sendMessage("Du hast " + args[0] + " eine Anfrage geschickt");
					try {
						Frdb.save(de.noneless.Main.Friends);
					} catch (IOException e) {
						p.sendMessage("Fehler beim Speichern der Freundesliste: " + e.getMessage());
					}
				} else {
					p.sendMessage("Der Spieler " + args[0] + " ist leider nicht online");
				}
				return true;
			} else if (args[0].equalsIgnoreCase("accept") && args.length > 1) {
				BecameRequestslist.addAll(Frdb.getStringList(p.getName() + ".BecameRequests"));
				if (BecameRequestslist.contains(args[1])) {
					Player p2 = Bukkit.getPlayer(args[1]);
					p.sendMessage("Du hast eine Anfrage von " + args[1] + " erhalten, die wird nun angenommen");
					int BCRCounter = Frdb.getInt(p.getName() + ".BCRCounter") - 1;
					Frdb.set(p.getName() + ".BCRCounter", BCRCounter);
					int SDCounter = Frdb.getInt(args[1] + ".SDCounter") - 1;
					Frdb.set(args[1] + ".SDCounter", SDCounter);
					SendedRequestslist.addAll(Frdb.getStringList(args[1] + ".SendetRequests"));
					SendedRequestslist.remove(p.getName());
					Frdb.set(args[1] + ".SendetRequests", SendedRequestslist);
					BecameRequestslist.remove(args[1]);
					Frdb.set(p.getName() + ".BecameRequests", BecameRequestslist);
					Friends.addAll(Frdb.getStringList(p.getName() + ".Friends"));
					Friends.add(args[1]);
					if (Frdb.getBoolean(p.getName() + ".webregister")) {
						try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("INSERT INTO " + p.getName() + "_Friends (Friend) VALUES (?)")) {
							ps.setString(1, args[1]);
							ps.executeUpdate();
						} catch (SQLException e) {
							p.sendMessage(" " + e);
						}
					} else {
						p.sendMessage("Benutze /Webregister um den vollen Umfang des Servers zu benutzen");
					}
					Frdb.set(p.getName() + ".Friends", Friends);
					p2Friends.addAll(Frdb.getStringList(args[1] + ".Friends"));
					p2Friends.add(p.getName());
					if (Frdb.getBoolean(args[1] + ".webregister")) {
						try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("INSERT INTO " + args[1] + "_Friends (Friend) VALUES (?)")) {
							ps.setString(1, p.getName());
							ps.executeUpdate();
						} catch (SQLException e) {
							if (p2 != null) p2.sendMessage(" " + e);
						}
					} else {
						p.sendMessage("Benutze /Webregister um den vollen Umfang des Servers zu benutzen");
					}
					Frdb.set(args[1] + ".Friends", p2Friends);
					int p1Counter = Frdb.getInt(p.getName() + ".Count") + 1;
					int p2Counter = Frdb.getInt(args[1] + ".Count") + 1;
					Frdb.set(p.getName() + ".Count", p1Counter);
					Frdb.set(args[1] + ".Count", p2Counter);
					if (p2 != null) {
						p2.sendMessage(p.getName() + " hat deine Anfrage angenommen");
					}
					try {
						Frdb.save(de.noneless.Main.Friends);
					} catch (IOException e) {
						p.sendMessage("Fehler beim Speichern der Freundesliste: " + e.getMessage());
					}
				} else {
					p.sendMessage("Bitte /addfriend accept (Spielername) eingeben");
				}
				return true;
			}
			return false;
		}
		
	}
