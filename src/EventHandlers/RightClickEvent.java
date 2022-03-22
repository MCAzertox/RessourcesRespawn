package EventHandlers;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fr.mcazertox.ressourcesrespawn.DataConfig;

public class RightClickEvent implements Listener {
	
	@EventHandler
	public void rightClick(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if(e.getHand() != EquipmentSlot.HAND) {
			return;
		}
		ItemStack it = e.getPlayer().getInventory().getItemInMainHand();
		if(it == null) {
			return;
		}
		if(!it.hasItemMeta()) {
			return;
		}
		if(!e.getPlayer().hasPermission("ressourcesrespawn.wand")) {
			return;
		}
		
		Boolean crops = false;
		Boolean blocks = false;
		for(NamespacedKey key : it.getItemMeta().getPersistentDataContainer().getKeys()) {
			if(key.getKey().equalsIgnoreCase("crops")) {
				crops = true;
				break;
			}
			if(key.getKey().equalsIgnoreCase("blocks")) {
				blocks = true;
				break;
			}
		}
		if(!crops && !blocks) {
			return;
		}
		e.setCancelled(true);
		String worldName = e.getPlayer().getWorld().getName();
		String chunkStr = String.valueOf(e.getClickedBlock().getChunk().getX()) + "_" + String.valueOf(e.getClickedBlock().getChunk().getZ());
		String locaStr = String.valueOf(e.getClickedBlock().getX() + "_" + e.getClickedBlock().getY() + "_" + e.getClickedBlock().getZ());
		
		if(DataConfig.get().getString(worldName+"."+chunkStr+"."+locaStr+".Material") != null) {
			e.getPlayer().sendMessage("§cBlock Already Registered !");
			return;
		}
		
		String type = "";
		if(crops) {
			type = "Crops";
		}else {
			type = "Blocks";
		}
		DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr+".Material", e.getClickedBlock().getType().name());
		DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr+".Type", type);
		DataConfig.save();
		
		e.getPlayer().sendMessage("§aYou added a block to the respawn list ! §7(§e" + e.getClickedBlock().getType().name() + "§7)" + "  §aType: §b" + type);
		
		return;
	}
}
