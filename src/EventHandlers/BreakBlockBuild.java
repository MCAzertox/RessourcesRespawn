package EventHandlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import fr.mcazertox.ressourcesrespawn.DataConfig;
import fr.mcazertox.ressourcesrespawn.RessourcesRespawn;

public class BreakBlockBuild implements Listener {
	
	@EventHandler
	public void BuildModeBreak(BlockBreakEvent e) {
		if(RessourcesRespawn.isPlayerBuildingBlocks(e.getPlayer().getUniqueId())) {
			if(DataConfig.get().getConfigurationSection(e.getBlock().getWorld().getName()) == null) {
				return;
			}
			String world = e.getBlock().getWorld().getName();
			String chunkStr = String.valueOf(e.getBlock().getLocation().getChunk().getX()) + "_" + String.valueOf(e.getBlock().getLocation().getChunk().getZ());
			if(DataConfig.get().getConfigurationSection(world+"."+chunkStr) == null) {
				return;
			}
			Integer x = e.getBlock().getLocation().getBlockX();
			Integer y = e.getBlock().getLocation().getBlockY();
			Integer z = e.getBlock().getLocation().getBlockZ();
			String locStr = String.valueOf(x) + "_" + String.valueOf(y) + "_" + String.valueOf(z);
			if(DataConfig.get().getConfigurationSection(world+"."+chunkStr + "." + locStr) == null) {
				return;
			}
			DataConfig.get().set(world+"."+chunkStr + "." + locStr, null);
			DataConfig.save();
			e.getPlayer().sendMessage("§3You removed a block from the respawn list ! §7(§e" + e.getBlock().getType().name() + "§7)");
		}
	}

}
