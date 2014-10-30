package de.ersterstreber.freunde.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.ersterstreber.freunde.Freunde;

public class PlayerQuitListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent e) {
		if (Freunde.invitations.containsKey(e.getPlayer().getUniqueId())) {
			UUID u = Freunde.invitations.get(e.getPlayer().getUniqueId());
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				p.sendMessage("§6" + e.getPlayer().getName() + " §cging offline!");
			}
			Freunde.invitations.remove(e.getPlayer().getUniqueId());
		}
		for (Player p : Freunde.friendsAsPlayers(e.getPlayer().getUniqueId())) {
			p.sendMessage("§c[-] " + e.getPlayer().getName());
		}
	}
	
}
