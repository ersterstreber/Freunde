package de.ersterstreber.freunde.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.ersterstreber.freunde.Freunde;
import de.ersterstreber.freunde.UUIDFetcher;

public class FreundeCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("freunde")) {
			if (args.length == 0) {
				Freunde.help(p);
				return true;
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("invite") || args[0].equalsIgnoreCase("add")) {
					if (Bukkit.getPlayer(args[1]) != null)  {
						Freunde.invitation(p, Bukkit.getPlayer(args[1]));
						return true;
					} else {
						p.sendMessage("§6" + args[1] + " §cist nicht online!");
						return true;
					}
				}
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("accept")) {
					Freunde.accept(p);
					return true;
				}
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("deny")) {
					Freunde.deny(p);
					return true;
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("remove")) {
					String toRemove = args[1];
					for (String s : Freunde.fConfig().getStringList("namen")) {
						if (s.equalsIgnoreCase(toRemove)) {
							toRemove = s;
						}
					}
					try {
						Freunde.removeFriend(p.getUniqueId(), UUIDFetcher.getUUIDOf(toRemove));
						return true;
					} catch (Exception e) {
						p.sendMessage("§6" + toRemove + " §cist nicht dein Freund!");
						return true;
					}
				}
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					p.sendMessage("§9Deine Freunde:");
					for (String s : Freunde.friendsAsNames(p.getUniqueId())) {
						p.sendMessage(s);
					}
					if (Freunde.friends(p.getUniqueId()).size() == 0) {
						p.sendMessage("§cDu hast keine Freunde!");
						return true;
					}
					return true;
				}
			}
			p.sendMessage("§cIch kenne diesen Command nicht!\nÜberprüfe bitte, ob du ihn richtig geschrieben hast!");
			return true;
		}
		return true;
	}
}
