package fr.mcazertox.ressourcesrespawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import EventHandlers.BreakBlock;
import EventHandlers.BreakBlockBuild;
import EventHandlers.BreakCrops;
import EventHandlers.LeftClickEvent;
import EventHandlers.PlaceBlock;
import EventHandlers.RightClickEvent;

public class RessourcesRespawn extends JavaPlugin {
	
	private static HashMap<UUID, Boolean> buildBlocks = new HashMap<UUID, Boolean>();
	private static HashMap<UUID, Boolean> buildCrops = new HashMap<UUID, Boolean>();
	private static HashMap<World, HashMap<String, List<Material>>> whiteListBlocks = new HashMap<World, HashMap<String, List<Material>>>();
	
	private static RessourcesRespawn instance;
	
	public static RessourcesRespawn getInstance() {
		return instance;
	}
	
	public static boolean isPlayerBuildingBlocks(UUID uuid){
		
		if(!buildBlocks.containsKey(uuid)) {
			return false;
		}
		
		return buildBlocks.get(uuid);
	}
	
	public static void setBuildingBlocksMode(UUID uuid, Boolean bool) {
		
		buildBlocks.put(uuid, bool);
	}
	
	public static boolean isPlayerBuildingCrops(UUID uuid){
		
		if(!buildCrops.containsKey(uuid)) {
			return false;
		}
		
		return buildCrops.get(uuid);
	}
	
	public static void setBuildingCropsMode(UUID uuid, Boolean bool) {
		
		buildCrops.put(uuid, bool);
	}

	@Override
	public void onEnable(){
		instance = this;
		DataConfig.setup();
		DataConfig.save();
		getServer().getPluginManager().registerEvents(new LeftClickEvent(), this);
		getServer().getPluginManager().registerEvents(new RightClickEvent(), this);
		getServer().getPluginManager().registerEvents(new BreakBlock(this), this);
		getServer().getPluginManager().registerEvents(new BreakCrops(this), this);
		getServer().getPluginManager().registerEvents(new PlaceBlock(), this);
		getServer().getPluginManager().registerEvents(new BreakBlockBuild(), this);
		//getServer().getPluginManager().registerEvents(new ShiftClickEvent(this), this);
		getCommand("rr").setExecutor(new Commands(this));
		saveDefaultConfig();
		loadWhiteList();
		debug();
		super.onEnable();
		System.out.println("[RessourcesRespawn] Plugin loaded successfully !");
		return;
	}
	
	@Override
	public void onDisable() {
		
		super.onDisable();
		System.out.println("[RessourcesRespawn] Plugin unloaded successfully !");
		return;
	}
	
	public static HashMap<World, HashMap<String, List<Material>>> getWhiteList(){
		return whiteListBlocks;
	}
	
	public void loadWhiteList() {
		for(String world : getConfig().getConfigurationSection("worldguard-regions").getKeys(false)) {
			HashMap<String,List<Material>> whitelist = new HashMap<String, List<Material>>();
			for(String rg : getConfig().getConfigurationSection("worldguard-regions." + world).getKeys(false)) {
				if(getConfig().getStringList("worldguard-regions." + world + "." + rg + ".whitelisted-blocks") != null) {
					List<Material> materials = new ArrayList<Material>();
					for(String mat : getConfig().getStringList("worldguard-regions." + world + "." + rg + ".whitelisted-blocks")) {
						try {
							Material material = Material.matchMaterial(mat);
							materials.add(material);
						} catch (Exception e) {
							System.out.println("[RessourcesRespawn] invalid material: " + ChatColor.RED + mat + ChatColor.RESET + " found in region: " + ChatColor.BLUE + rg);
						}
					}
					whitelist.put(rg, materials);
				}
			}
			if(Bukkit.getWorld(world) != null) {
				whiteListBlocks.put(Bukkit.getWorld(world), whitelist);
			}
		}
	}
	
	public void debug() {
		if(DataConfig.get() == null) {
			return;
		}
		for(String world : DataConfig.get().getKeys(false)) {
			for(String chunk : DataConfig.get().getConfigurationSection(world).getKeys(false)) {
				for(String location : DataConfig.get().getConfigurationSection(world + "." + chunk).getKeys(false)) {
					String [] pos = location.split("_");
					Location loc = new Location(Bukkit.getWorld(world), Float.valueOf(pos[0]), Float.valueOf(pos[1]), Float.valueOf(pos[2]));
					loc.getBlock().setType(Material.matchMaterial(DataConfig.get().getString(world+"."+chunk+"."+location+".Material")));
				}
			}
		}
	}
}
