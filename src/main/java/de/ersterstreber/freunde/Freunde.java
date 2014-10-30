package de.ersterstreber.freunde;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.ersterstreber.freunde.command.FreundeCommand;
import de.ersterstreber.freunde.listeners.AsyncPlayerChatListener;
import de.ersterstreber.freunde.listeners.PlayerJoinListener;
import de.ersterstreber.freunde.listeners.PlayerQuitListener;

public class Freunde extends JavaPlugin {

	private static Freunde freunde;
	
	private static YamlConfiguration config;
	private static File f;
	
	public static Map<UUID, UUID> invitations;
	
	@Override
	public void onEnable() {
		freunde = this;
		
		invitations = new HashMap<UUID, UUID>();
		
		f = new File("plugins/Freunde", "freunde.yml");
		config = YamlConfiguration.loadConfiguration(f);
		
		Bukkit.getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
		
		getCommand("freunde").setExecutor(new FreundeCommand());
	}
	
	public static Freunde instance() { 
		return freunde;
	}
	
	public static YamlConfiguration fConfig() {
		return config;
	}
	
	public static void save() {
		try {
			config.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isFriend(UUID u1, UUID u2) {
		if (config.getStringList("freunde." + u1.toString()).contains(u2.toString())) return true;
		return false;
	}
	
	public static void addFriend(UUID u1, UUID u2) {
		if (!isFriend(u1, u2)) {
			List<String> friends = config.getStringList("freunde." + u1.toString());
			friends.add(u2.toString());
			config.set("freunde." + u1.toString(), friends);
			save();
			
			List<String> friends2 = config.getStringList("freunde." + u2.toString());
			friends2.add(u1.toString());
			config.set("freunde." + u2.toString(), friends2);
			save();
		} else {
			Bukkit.getPlayer(u1).sendMessage("§6" + Bukkit.getOfflinePlayer(u2).getName() + " §cist bereits dein Freund!");
			return;
		}
	}
	
	public static void removeFriend(UUID u1, UUID u2) {
		if (isFriend(u1, u2)) {
			List<String> friends = config.getStringList("freunde." + u1.toString());
			friends.remove(u2.toString());
			config.set("freunde." + u1.toString(), friends);
			save();
			
			List<String> friends2 = config.getStringList("freunde." + u2.toString());
			friends2.remove(u1.toString());
			config.set("freunde." + u2.toString(), friends2);
			save();
			
			Bukkit.getPlayer(u1).sendMessage("§6" + Bukkit.getOfflinePlayer(u2).getName() + " §fwurde von deiner Freundschaftsliste entfernt.");
			return;
		} else {
			Bukkit.getPlayer(u1).sendMessage("§6" + Bukkit.getOfflinePlayer(u2).getName() + " §cist nicht dein Freund!");
			return;
		}
	}
	
	public static void invitation(final Player p1, final Player p2) {
		if (!isFriend(p1.getUniqueId(), p2.getUniqueId())) {
			if (invitations.containsKey(p1.getUniqueId())) {
				p1.sendMessage("§cDu hast bereits eine ausstehende Freundschaftsanfrage!");
				return;
			}
			invitations.put(p1.getUniqueId(), p2.getUniqueId());
			p1.sendMessage("§6Anfrage versendet.");
			p1.sendMessage("§6Die Anfrage läuft in 30 Sekunden ab.");
			
			p2.sendMessage("§6" + p1.getName() + " §fmöchte dein Freund sein!");
			p2.sendMessage("§6/f accept §f- Annehmen");
			p2.sendMessage("§6/f deny §f- Ablehnen");
			
			new BukkitRunnable() {
				
				public void run() {
					for (Map.Entry<UUID, UUID> it : invitations.entrySet()) {
						if (it.getKey().equals(p1.getUniqueId()) && it.getValue().equals(p2.getUniqueId())) {
							invitations.remove(p1.getUniqueId());
						}
					}
				}
			}.runTaskLater(instance(), 30 * 20L);
		} else {
			p1.sendMessage("§c" + p2.getName() + " §2ist bereits dein Freund!");
			return;
		}
	}
	
	public static void accept(Player p) {
		if (invitations.containsValue(p.getUniqueId())) {
			UUID u = null;
			for (Map.Entry<UUID, UUID> it : invitations.entrySet()) {
				if (it.getValue().equals(p.getUniqueId())) u = it.getKey();
			}
			Player p2 = Bukkit.getPlayer(u);
			if (p2 != null) {
				invitations.remove(u);
				p.sendMessage("§2Anfrage angenommen! Ihr seid nun Freunde!");
				p2.sendMessage("§6" + p.getName() + " §2hat deine Anfrage angenommen!\nIhr seid nun Freunde!");
				addFriend(u, p.getUniqueId());
				return;
			} else {
				p.sendMessage("§c" + Bukkit.getOfflinePlayer(u).getName() + " §2ist nicht mehr online!");
				return;
			}
		} else {
			p.sendMessage("§cDu hast keine ausstehende Freundschaftsanfrage.");
			return;
		}
	}
	
	public static void deny(Player p) {
		if (invitations.containsValue(p.getUniqueId())) {
			UUID u = null;
			for (Map.Entry<UUID, UUID> it : invitations.entrySet()) {
				if (it.getValue().equals(p.getUniqueId())) u = it.getKey();
			}
			Player p2 = Bukkit.getPlayer(u);
			if (p2 != null) {
				invitations.remove(u);
				p.sendMessage("§cAnfrage abgelehnt!");
				p2.sendMessage("§6" + p.getName() + " §chat deine Anfrage abgelehnt!");
				return;
			} else {
				p.sendMessage("§c" + Bukkit.getOfflinePlayer(u).getName() + " §2ist nicht mehr online!");
				return;
			}
		} else {
			p.sendMessage("§cDu hast keine ausstehende Freundschaftsanfrage.");
			return;
		}
	}
	
	public static List<UUID> friends(UUID u) {
		List<String> uuid = config.getStringList("freunde." + u);
		List<UUID> toReturn = new ArrayList<UUID>();
		for (String uu : uuid) {
			toReturn.add(UUID.fromString(uu));
		}
		return toReturn;
	}
	
	public static List<Player> friendsAsPlayers(UUID u) {
		List<Player> toReturn = new ArrayList<Player>();
		for (UUID uu : friends(u)) {
			if (Bukkit.getPlayer(uu) != null) {
				toReturn.add(Bukkit.getPlayer(uu));
			}
		}
		return toReturn;
	}
	
	public static List<String> friendsAsNames(UUID u) {
		List<String> toReturn = new ArrayList<String>();
		for (UUID uu : friends(u)) {
			if (Bukkit.getPlayer(uu) != null) {
				toReturn.add("§2- " + Bukkit.getOfflinePlayer(uu).getName());
			} else {
				toReturn.add("§c- " + Bukkit.getOfflinePlayer(uu).getName());
			}
		}
		return toReturn;
	}
	
	public static void help(Player p) {
		p.sendMessage("§9Freunde-Hilfe:");
		p.sendMessage("§6/f invite <Spieler> §f- Lädt einen Spieler zur Freundesliste ein");
		p.sendMessage("§6/f accept §f- Nimmt eine Anfrage an");
		p.sendMessage("§6/f deny §f- Lehnt eine Abfrage ab");
		p.sendMessage("§6/f remove <Spieler> §f- Entfernt einen Spieler von deiner Freundesliste");
		p.sendMessage("§6/f list §f- Zeigt dir deine Freunde an");
		p.sendMessage("§6Freunde-Chat: §fNachricht mit einem # beginnen");
		
	}
	
}
