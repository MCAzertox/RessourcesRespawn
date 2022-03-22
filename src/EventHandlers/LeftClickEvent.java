package EventHandlers;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.mcazertox.ressourcesrespawn.DataConfig;

public class LeftClickEvent implements Listener {
	
	@EventHandler
	public void leftClick(PlayerInteractEvent e) {
		if(e.getAction() != Action.LEFT_CLICK_BLOCK) {
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
		
		if(DataConfig.get().getString(worldName+"."+chunkStr+"."+locaStr+".Material") == null) {
			e.getPlayer().sendMessage("§cBlocks this block is not in the respawn list !");
			return;
		}
		
		DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr, null);
		DataConfig.save();
		e.getPlayer().sendMessage("§3You removed a block from the respawn list ! §7(§e" + e.getClickedBlock().getType().name() + "§7)");
		
		return;
	}

}
