package EventHandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import fr.mcazertox.ressourcesrespawn.DataConfig;
import fr.mcazertox.ressourcesrespawn.RessourcesRespawn;

public class BreakBlock implements Listener {
	
	private RessourcesRespawn main;
	
	private HashMap<UUID, Location> tryBlock = new HashMap<UUID, Location>();
	
	public BreakBlock(RessourcesRespawn ressourcesrespawn) {
		this.main = ressourcesrespawn;
	}

	@EventHandler
	public void breakBlock(BlockBreakEvent e) {
		Block b = e.getBlock();
		
		Player p = e.getPlayer();
		
		if(RessourcesRespawn.isPlayerBuildingBlocks(p.getUniqueId())) {
			return;
		}
		if(isInProtectedRegion(p,b)) { //WorldGuard rg mine set
			if(!itemWichBreakBlacklisted(p)) {
				breakIt(e);
				return;
			}else {
				p.sendMessage(main.getConfig().getString("blacklist-message").replace("&", "§"));
				e.setCancelled(true);
				return;
			}
		}
		
		
		//Else check for independant blocks
		String worldName = e.getPlayer().getWorld().getName();
		String chunkStr = String.valueOf(b.getChunk().getX()) + "_" + String.valueOf(b.getChunk().getZ());
		String locaStr = String.valueOf(b.getX() + "_" + b.getY() + "_" + b.getZ());
		
		if(DataConfig.get().getConfigurationSection(worldName + "." + chunkStr + "." + locaStr) == null) {
			return;
		}
		
		String type = DataConfig.get().getString(worldName + "." + chunkStr + "." + locaStr + ".Type");
		
		if(!type.equalsIgnoreCase("Blocks")) {
			return;
		}
		
		
		boolean isBlackListed = itemWichBreakBlacklisted(p);
		
		if(isBlackListed) {
			p.sendMessage(main.getConfig().getString("blacklist-message").replace("&", "§"));
			e.setCancelled(true);
			return;
		}else {
			breakIt(e);
		}

		
	}
	
	private boolean isInProtectedRegion(Player p, Block b) {
		if(!RessourcesRespawn.getWhiteList().containsKey(p.getWorld())) {
			return false;
		}
		HashMap<String,List<Material>> allRegions = RessourcesRespawn.getWhiteList().get(p.getWorld());
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		
		com.sk89q.worldedit.world.World wgWorld = BukkitAdapter.adapt(p.getWorld());

		RegionManager regions = container.get(wgWorld);
		
		for(Entry<String,List<Material>> rg : allRegions.entrySet()) {
			String regionStr = rg.getKey();
			List<Material> materials = rg.getValue();
			try {
				ProtectedRegion region = regions.getRegion(regionStr);
				BlockVector3 block = BlockVector3.at(b.getX(), b.getY(), b.getZ());

	            if (region.contains(block)) {
	            	if(materials.contains(b.getType())) {
	            		return true;
	            	}
	            }
			} catch (Exception e2) {
				//PASS
			}
		}
		
		return false;
	}
	
	public void breakIt(BlockBreakEvent e) {
		Block b = e.getBlock();
		String worldName = e.getPlayer().getWorld().getName();
		
		String chunkStr = String.valueOf(b.getChunk().getX()) + "_" + String.valueOf(b.getChunk().getZ());
		String locaStr = String.valueOf(b.getX() + "_" + b.getY() + "_" + b.getZ());
		
		tryBlock.put(e.getPlayer().getUniqueId(), b.getLocation());
		
		String material = DataConfig.get().getString(worldName + "." + chunkStr + "." + locaStr + ".Material");
		if(material == null) {
			material = b.getType().toString();
		}
		Material mat = Material.matchMaterial(material);
		Material matReplace = Material.AIR;
		String sound = main.getConfig().getString("blocks.default-sound");
		
		Integer delay = main.getConfig().getInt("blocks.default-delay");
		String particleOnBreak = main.getConfig().getString("blocks.default-particles-on-break");
		String particleOnRespawn = main.getConfig().getString("blocks.default-particles-on-respawn");
		
		if(main.getConfig().getConfigurationSection("blocks.override") != null) {
			for(String s : main.getConfig().getConfigurationSection("blocks.override").getKeys(false)) {
				if(s.equalsIgnoreCase(b.getType().toString())) {
					delay = main.getConfig().getInt("blocks.override." + s + ".delay");
					matReplace = Material.matchMaterial(main.getConfig().getString("blocks.override." + s + ".replaced-by"));
					sound = main.getConfig().getString("blocks.override." + s + ".sound");
					particleOnBreak = main.getConfig().getString("blocks.override." + s + ".particles-on-break");
					particleOnRespawn = main.getConfig().getString("blocks.override." + s + ".particles-on-respawn");
				}
			}
		}
		if(!sound.equalsIgnoreCase("NONE")) {
			PlaySound(e.getPlayer(), sound, 10f, 1f);
		}
		
		final Material toReplace = matReplace;
		
		if(main.getConfig().getBoolean("blocks.items-to-inventory")) {
			e.setCancelled(true);
			Player p = e.getPlayer();
			List<Material> ores = new ArrayList<Material>();
			ores.add(Material.COAL_ORE);
			ores.add(Material.DIAMOND_ORE);
			ores.add(Material.EMERALD_ORE);
			ores.add(Material.NETHER_GOLD_ORE);
			ores.add(Material.NETHER_QUARTZ_ORE);
			ores.add(Material.LAPIS_ORE);
			List<Material> other = new ArrayList<Material>();
			other.add(Material.REDSTONE_ORE);
			other.add(Material.SEA_LANTERN);
			other.add(Material.GLOWSTONE);
			other.add(Material.SWEET_BERRIES);
			other.add(Material.NETHER_WART);
			other.add(Material.WHEAT);
			other.add(Material.POTATO);
			other.add(Material.CARROT);
			other.add(Material.BEETROOT);
			other.add(Material.MELON);
			other.add(Material.PUMPKIN);
			Boolean fortune = false;
			Integer fortuneLVL = 0;
			Integer bonus = 0;
			if(ores.contains(b.getType()) || other.contains(b.getType())) {
				if(p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && !p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
					fortune = true;
					fortuneLVL = p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
					bonus = (int) (Math.random() * (fortuneLVL + 2)) - 1;
				    if (bonus < 0) {
				        bonus = 0;
				    }
				}
			}
			
			for(ItemStack item : b.getDrops()) {
				if(fortune) {
					if(ores.contains(b.getType())) {
						item.setAmount(item.getAmount()*(bonus+1));
					}
					else if(other.contains(b.getType())) {
						item.setAmount(item.getAmount() + bonus);
					}
				}
				p.getInventory().addItem(item);
			}
		}
		
		final String onBreakParticle = particleOnBreak;
		final String onRespawnParticle = particleOnRespawn;
		
		Bukkit.getServer().getScheduler().runTaskLater((Plugin)RessourcesRespawn.getPlugin(RessourcesRespawn.class), new Runnable() {
	    	public void run() {
	    		b.setType(toReplace);
	    		if(onBreakParticle!= null) {
	    			if(!onBreakParticle.equalsIgnoreCase("NONE")) {
	    				PlayParticle(onBreakParticle, b.getLocation());
	    			}
	    		}
	    	}
	   	},  1L);
		
		String world = b.getWorld().getName();
		String chunk = String.valueOf(b.getLocation().getChunk().getX())+"_"+String.valueOf(b.getLocation().getChunk().getZ());
		String locStr = String.valueOf(b.getLocation().getBlockX()) + "_" + String.valueOf(b.getLocation().getBlockY()) + "_" + String.valueOf(b.getLocation().getBlockZ());
		Material compare;
		if(DataConfig.get().getString(world + "." + chunk + "." + locStr + ".Material") == null) {
			compare=b.getType();
		}else {
			compare = Material.matchMaterial(DataConfig.get().getString(world + "." + chunk + "." + locStr + ".Material"));
		}
		if(e.getBlock().getType() == compare) {
			Bukkit.getServer().getScheduler().runTaskLater((Plugin)RessourcesRespawn.getPlugin(RessourcesRespawn.class), new Runnable() {
		    	public void run() {
		    		b.setType(mat);
		    		if(onRespawnParticle!= null) {
		    			if(!onRespawnParticle.equalsIgnoreCase("NONE")) {
		    				PlayParticle(onRespawnParticle, b.getLocation());
		    			}
		    		}
		    	}
		   	},  delay.intValue());
		}
	}
	
	public boolean itemWichBreakBlacklisted(Player p) {
		
		for(String itemID : main.getConfig().getConfigurationSection("blocks.blacklist-items").getKeys(false)) {
			if(p.getInventory().getItemInMainHand().getType() == Material.matchMaterial(main.getConfig().getString("blocks.blacklist-items." + itemID + ".material"))) {
				if(!main.getConfig().getString("blocks.blacklist-items." + itemID + ".name").equalsIgnoreCase("NONE")) {
					if(p.getInventory().getItemInMainHand().hasItemMeta()) {
						if(p.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()) {
							if(main.getConfig().getString("blocks.blacklist-items." + itemID + ".name").replace("&", "§").equalsIgnoreCase(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName())) {
								return true;
							}
						}
					}
				}
				else {
					return true;
				}
				
			}
		}
		
		return false;
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
	public void PlaySound(Player player, String sound, Float Volume, Float Pitch) {
		Sound thesound = null;
		thesound = Sound.valueOf(sound);
		player.playSound(player.getLocation(), thesound, Volume.floatValue(), Pitch.floatValue());
	}
	
	public void PlayParticle(String particle, Location loc) {
		loc.getWorld().spawnParticle(Particle.valueOf(particle), loc, 100,0,1,0);
	}

}
