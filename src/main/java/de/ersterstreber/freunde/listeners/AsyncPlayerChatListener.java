package de.ersterstreber.freunde.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.ersterstreber.freunde.Freunde;

public class AsyncPlayerChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if (e.getMessage().replace(" ", "").startsWith("#")) {
			//Ab hier ist Freunde-Chat!
			e.setCancelled(true);
			for (Player p : Freunde.friendsAsPlayers(e.getPlayer().getUniqueId())) {
				p.sendMessage("§4[F] " + "§6" + e.getPlayer().getName() + ": " + e.getMessage().replaceFirst("#", ""));
			}
			e.getPlayer().sendMessage("§4[F] " + "§6" + e.getPlayer().getName() + ": " + e.getMessage().replaceFirst("#", ""));
		}
	}
	
}
