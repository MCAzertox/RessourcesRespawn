package EventHandlers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import fr.mcazertox.ressourcesrespawn.DataConfig;
import fr.mcazertox.ressourcesrespawn.RessourcesRespawn;

public class PlaceBlock implements Listener {
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Block b = e.getBlock();
		Player p = e.getPlayer();
		
		if(!RessourcesRespawn.isPlayerBuildingBlocks(p.getUniqueId()) && !RessourcesRespawn.isPlayerBuildingCrops(p.getUniqueId())) {
			return;
		}
		
		if(RessourcesRespawn.isPlayerBuildingBlocks(p.getUniqueId())) {
			String worldName = e.getPlayer().getWorld().getName();
			String chunkStr = String.valueOf(b.getChunk().getX()) + "_" + String.valueOf(b.getChunk().getZ());
			String locaStr = String.valueOf(b.getX() + "_" + b.getY() + "_" + b.getZ());
			
			DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr+".Material", b.getType().name());
			DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr+".Type", "Blocks");
			DataConfig.save();
			
			p.sendMessage("§b" + b.getType().toString() + "§a added as 'blocks' type §l✔");
			
			return;
		}
		if(RessourcesRespawn.isPlayerBuildingCrops(p.getUniqueId())) {
			String worldName = e.getPlayer().getWorld().getName();
			String chunkStr = String.valueOf(b.getChunk().getX()) + "_" + String.valueOf(b.getChunk().getZ());
			String locaStr = String.valueOf(b.getX() + "_" + b.getY() + "_" + b.getZ());
			
			DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr+".Material", b.getType().name());
			DataConfig.get().set(worldName+"."+chunkStr+"."+locaStr+".Type", "Crops");
			DataConfig.save();
			
			p.sendMessage("§b" + b.getType().toString() + "§a added as 'crops' type §l✔");
			
			return;
		}
	}

}
