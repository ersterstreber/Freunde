package de.ersterstreber.freunde.listeners;

import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.potion.PotionEffectType;

import de.ersterstreber.freunde.Freunde;

public class VehicleExitListener implements Listener {

	@EventHandler
	public void onExit(VehicleExitEvent e) {
		if (!(e.getExited() instanceof Player)) return;
		if (!(e.getVehicle() instanceof Pig)) return;
		Player p = (Player) e.getExited();
		if (Freunde.spectators.containsKey(p.getName())) {
			e.setCancelled(true);
			Pig pig = Freunde.spectators.get(p.getName());
			p.sendMessage("§2Du hast das Spectaten beendet!");
			Freunde.spectators.remove(p.getName());
			p.leaveVehicle();
			pig.remove();
			p.teleport(Freunde.locations.get(p.getName()));
			p.removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}
	
}
