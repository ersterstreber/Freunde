package de.ersterstreber.freunde.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.ersterstreber.freunde.Freunde;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (!Freunde.fConfig().contains("freunde." + e.getPlayer().getUniqueId())) {
			Freunde.fConfig().addDefault("freunde." + e.getPlayer().getUniqueId(), new ArrayList<String>());
			List<String> names = Freunde.fConfig().getStringList("namen");
			names.add(e.getPlayer().getName());
			Freunde.fConfig().set("namen", names);
			Freunde.fConfig().options().copyDefaults(true);
			Freunde.save();
		}
		for (Player p : Freunde.friendsAsPlayers(e.getPlayer().getUniqueId())) {
			p.sendMessage("§2[+] " + e.getPlayer().getName());
		}
	}
	
}
