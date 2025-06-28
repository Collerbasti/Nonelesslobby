package de.noneless.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.noneless.Main;
import de.noneless.MySQL;

public class CMDwebRegister implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Dieser Befehl ist nur für Spieler verfügbar.");
			return true;
		}
		Player p = (Player) sender;
		if (!de.noneless.MySQL.isConnected()) {
			p.sendMessage("es tut uns leid aber irgendwas ist schief gelaufen, bitte melde dies einem Supporter");
			return true;
		}
		if (isUserExists(p.getUniqueId(), p)) {
			p.sendMessage("Du bist bereits im System, wenn du dein Passwort vergessen hast, melde dich bei einem Supporter");
			return true;
		}
		if (args.length != 3) {
			p.sendMessage("Bitte gebe folgendes an: /webregister Nickname Passwort Passwortwdhl");
			return true;
		}
		String nick = args[0];
		String pw = args[1];
		String pw2 = args[2];
		if (!pw.equals(pw2)) {
			p.sendMessage("Deine Passwörter stimmen nicht überein!");
			return true;
		}
		if (isNickExists(nick, p)) {
			p.sendMessage("Bitte anderen Nutzernamen verwenden");
			return true;
		}
		try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("INSERT INTO PROFILELIST (UUID,NICKNAME,SPIELERNAME,PASSWORT,WORLD) VALUES (?,?,?,?,?)")) {
			ps.setString(1, p.getUniqueId().toString());
			ps.setString(2, nick);
			ps.setString(3, p.getName());
			ps.setString(4, pw);
			ps.setString(5, p.getLocation().getWorld().getName());
			ps.executeUpdate();
			p.sendMessage("Jetzt kannst du dich anmelden online");
			de.noneless.Main.Frdb.set(p.getName() + ".webregister", true);
		} catch (Exception e) {
			p.sendMessage("Sorry, aber irgendwas ist schiefgelaufen (" + e + ")");
		}
		try (PreparedStatement ps2 = de.noneless.MySQL.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + p.getName() + "_Friends (Friend TEXT(100), MESSAGE TEXT(100), ZUGESTELLT TEXT(100))")) {
			ps2.executeUpdate();
			int counter = de.noneless.Main.Frdb.getInt(p.getName() + ".Count");
			ArrayList<String> friends = new ArrayList<>(de.noneless.Main.Frdb.getStringList(p.getName() + ".Friends"));
			for (int i = 0; i < counter; i++) {
				try (PreparedStatement ps3 = de.noneless.MySQL.getConnection().prepareStatement("INSERT INTO " + p.getName() + "_Friends (Friend,MESSAGE,ZUGESTELLT) VALUES (?,\"Platzhalter\",\"ja\")")) {
					ps3.setString(1, friends.get(i));
					ps3.executeUpdate();
				} catch (Exception e) {
					p.sendMessage(" " + e);
				}
			}
		} catch (Exception e) {
			p.sendMessage(" " + e);
		}
		return true;
	}

	public static boolean isUserExists(UUID uuid, Player p) {
		try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("SELECT WEITERES FROM PROFILELIST WHERE UUID = ?")) {
			ps.setString(1, uuid.toString());
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isNickExists(String Nick, Player p) {
		try (PreparedStatement ps = de.noneless.MySQL.getConnection().prepareStatement("SELECT * FROM `PROFILELIST` WHERE `NICKNAME` LIKE ?")) {
			ps.setString(1, Nick);
			try (ResultSet rs = ps.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
}
