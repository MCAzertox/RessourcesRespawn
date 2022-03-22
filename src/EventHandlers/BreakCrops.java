package EventHandlers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftBeetroot;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCarrots;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCocoa;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftCrops;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftNetherWart;
import org.bukkit.craftbukkit.v1_16_R2.block.impl.CraftPotatoes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import fr.mcazertox.ressourcesrespawn.DataConfig;
import fr.mcazertox.ressourcesrespawn.RessourcesRespawn;

public class BreakCrops implements Listener{
	
private RessourcesRespawn main;
	
	private HashMap<UUID, Location> tryBlock = new HashMap<UUID, Location>();
	
	public BreakCrops(RessourcesRespawn ressourcesrespawn) {
		this.main = ressourcesrespawn;
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		Block b = e.getBlock();
		
		Player p = e.getPlayer();
		
		String worldName = e.getPlayer().getWorld().getName();
		String chunkStr = String.valueOf(b.getChunk().getX()) + "_" + String.valueOf(b.getChunk().getZ());
		String locaStr = String.valueOf(b.getX() + "_" + b.getY() + "_" + b.getZ());
		
		if(DataConfig.get().getConfigurationSection(worldName + "." + chunkStr + "." + locaStr) == null) {
			return;
		}
		
		String type = DataConfig.get().getString(worldName + "." + chunkStr + "." + locaStr + ".Type");
		
		if(!type.equalsIgnoreCase("Crops")) {
			return;
		}
		
		Boolean isBlackListed = false;
		
		for(String itemID : main.getConfig().getConfigurationSection("crops.blacklist-items").getKeys(false)) {
			if(p.getInventory().getItemInMainHand().getType() == Material.matchMaterial(main.getConfig().getString("crops.blacklist-items." + itemID + ".material"))) {
				if(!main.getConfig().getString("crops.blacklist-items." + itemID + ".name").equalsIgnoreCase("NONE")) {
					if(p.getInventory().getItemInMainHand().hasItemMeta()) {
						if(p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
							if(main.getConfig().getString("crops.blacklist-items." + itemID + ".name").replace("&", "§").equalsIgnoreCase(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())) {
								isBlackListed = true;
								break;
							}
						}
					}
				}
				else {
					isBlackListed = true;
					break;
				}
				
			}
		}
		
		if(isBlackListed) {
			p.sendMessage(main.getConfig().getString("blacklist-message").replace("&", "§"));
			e.setCancelled(true);
			return;
		}
		

		tryBlock.put(e.getPlayer().getUniqueId(), b.getLocation());
		
		String material = DataConfig.get().getString(worldName + "." + chunkStr + "." + locaStr + ".Material");
		Material mat = Material.matchMaterial(material);
		
		Integer delay = main.getConfig().getInt("crops.default-delay");
		
		if(main.getConfig().getConfigurationSection("crops.override") != null) {
			for(String s : main.getConfig().getConfigurationSection("crops.override").getKeys(false)) {
				if(s.equalsIgnoreCase(b.getType().toString())) {
					delay = main.getConfig().getInt("crops.override." + s + ".delay");
				}
			}
		}
		
		BlockFace face = null;
		
		if(b.getBlockData() instanceof CraftCocoa) {
			CraftCocoa cocoa = (CraftCocoa)b.getBlockData();
			face = cocoa.getFacing();
		}
		final BlockFace Cocoafacing = face;
		
		Bukkit.getServer().getScheduler().runTaskLater((Plugin)RessourcesRespawn.getPlugin(RessourcesRespawn.class), new Runnable() {
	    	public void run() {
	    		b.setType(mat);
	    		if(b.getBlockData() instanceof CraftCrops) {
	    			CraftCrops crop = (CraftCrops) b.getBlockData();
	    			crop.setAge(crop.getMaximumAge());
	    			b.setBlockData(crop);
	    			return;
	    		}
	    		if(b.getBlockData() instanceof CraftBeetroot) {
	    			CraftBeetroot crop = (CraftBeetroot) b.getBlockData();
	    			crop.setAge(crop.getMaximumAge());
	    			b.setBlockData(crop);
	    			return;
	    		}
	    		if(b.getBlockData() instanceof CraftNetherWart) {
	    			CraftNetherWart crop = (CraftNetherWart) b.getBlockData();
	    			crop.setAge(crop.getMaximumAge());
	    			b.setBlockData(crop);
	    			return;
	    		}
	    		if(b.getBlockData() instanceof CraftPotatoes) {
	    			CraftPotatoes crop = (CraftPotatoes) b.getBlockData();
	    			crop.setAge(crop.getMaximumAge());
	    			b.setBlockData(crop);
	    			return;
	    		}
	    		if(b.getBlockData() instanceof CraftCarrots) {
	    			CraftCarrots crop = (CraftCarrots) b.getBlockData();
	    			crop.setAge(crop.getMaximumAge());
	    			b.setBlockData(crop);
	    			return;
	    		}
	    		if(b.getBlockData() instanceof CraftCocoa) {
	    			CraftCocoa crop = (CraftCocoa) b.getBlockData();
	    			crop.setAge(crop.getMaximumAge());
	    			crop.setFacing(Cocoafacing);
	    			b.setBlockData(crop);
	    			return;
	    		}
	    	}
	   	},  delay.intValue());
	}
	
	@EventHandler
	public void wgBreak(com.sk89q.worldguard.bukkit.event.block.BreakBlockEvent e) {
		
		if(tryBlock == null) {
			return;
		}
		if(e.getCause().getFirstPlayer() == null) {
			return;
		}
		if(tryBlock.get(e.getCause().getFirstPlayer().getUniqueId()) == null) {
			return;
		}
		
		for(Block b : e.getBlocks()) {
			Location loca = tryBlock.get(e.getCause().getFirstPlayer().getUniqueId());
			Integer x = loca.getBlockX();
			Integer y = loca.getBlockY();
			Integer z = loca.getBlockZ();
			
			if(b.getLocation().getBlockX() == x && b.getLocation().getBlockY() == y && b.getLocation().getBlockZ() == z) {
				e.setAllowed(true);
				return;
			}
		}
		
		return;
	}

}
