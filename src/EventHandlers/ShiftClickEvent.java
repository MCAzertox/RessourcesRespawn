package EventHandlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import fr.mcazertox.ressourcesrespawn.RessourcesRespawn;

public class ShiftClickEvent implements Listener {
	
	private RessourcesRespawn main;
	
	public ShiftClickEvent(RessourcesRespawn ressourcesrespawn) {
		this.main = ressourcesrespawn;
	}
	
	@EventHandler
	public void onShiftClick(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}
		Player p = e.getPlayer();
		if(!p.isSneaking()) {
			return;
		}
		ItemStack it = p.getInventory().getItemInMainHand();
		Material mat = it.getType();
		if(!(mat == Material.valueOf(main.getConfig().getString("blocks.wand-material")) || mat == Material.valueOf(main.getConfig().getString("crops.wand-material")))) {
			return;
		}
		
		Integer i = 0;
		if(!it.hasItemMeta()) {
			return;
		}
		if(!it.getItemMeta().hasDisplayName()) {
			return;
		}
		if(it.getItemMeta().getPersistentDataContainer().isEmpty()) {
			return;
		}
		ItemMeta meta = it.getItemMeta();
		Integer slot = 0;
		for(ItemStack itemStack : p.getInventory().getStorageContents()) {
			if(itemStack != null) {
				if(itemStack.hasItemMeta()) {
					if(itemStack.getItemMeta().hasDisplayName()) {
						if(it.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) {
							for(NamespacedKey s : it.getItemMeta().getPersistentDataContainer().getKeys()) {
								if(s.getKey().equalsIgnoreCase("crops") || s.getKey().equalsIgnoreCase("blocks")) {
									slot = i;
									break;
								}
							}
						}
					}
				}
			}
			i+=1;
		}
		for(NamespacedKey s : meta.getPersistentDataContainer().getKeys()) {
			String key = s.getKey();
			if(key.equalsIgnoreCase("blocks")) {
				if(!p.hasPermission("ressourcesrespawn.usewand")) {
					p.sendMessage("§cYou don't have the permission to do that :/");
					return;
				}
				e.setCancelled(true);
				giveCropsWand(p,it,slot);
				return;
			}
			else if(key.equalsIgnoreCase("crops")) {
				if(!p.hasPermission("ressourcesrespawn.usewand")) {
					p.sendMessage("§cYou don't have the permission to do that :/");
					return;
				}
				giveBlocksWand(p,it,slot);
				e.setCancelled(true);
				return;
			}
			
		}
		
	}
	
	public void giveCropsWand(Player p, ItemStack it, Integer slot) {
		p.getInventory().setItem(slot, null);
		
		Material mat = Material.valueOf(main.getConfig().getString("crops.wand-material"));
		ItemStack newItem = new ItemStack(mat);
		ItemMeta meta = newItem.getItemMeta();
		
		meta.setDisplayName("§3RessourcesRespawn Wand §3- §a§lCrops mode");
		
		List<String> lore = new ArrayList<>();
		lore.add(" ");
		lore.add("§7(§eRight-Click§7) §ato add this crop to the respawn list");
		lore.add("§7(§eLeft-Click§7) §ato remove this crop from the respawn list");
		lore.add("§7(§eDrop§7) §ato change wand into blocks wand");
		
		meta.setLore(lore);
		
		NamespacedKey newkey = new NamespacedKey((Plugin) RessourcesRespawn.getInstance(), "crops");
		meta.getPersistentDataContainer().set(newkey, PersistentDataType.STRING, "crops");
		
		newItem.setItemMeta(meta);
		
		p.getInventory().setItem(slot, newItem);
		p.sendMessage("§7[§3RessourcesRespawn§7] §aCrops mode activated !");
		return;
	}
	
	public void giveBlocksWand(Player p, ItemStack it, Integer slot) {
		p.getInventory().setItem(slot, null);
		
		Material mat = Material.valueOf(main.getConfig().getString("blocks.wand-material"));
		ItemStack newItem = new ItemStack(mat);
		ItemMeta meta = newItem.getItemMeta();
		
		meta.setDisplayName("§3RessourcesRespawn Wand §3- §b§lBlocks mode");
		
		List<String> lore = new ArrayList<>();
		lore.add(" ");
		lore.add("§7(§eRight-Click§7) §ato add this block to the respawn list");
		lore.add("§7(§eLeft-Click§7) §ato remove this block from the respawn list");
		lore.add("§7(§eDrop§7) §ato change wand into crops wand");
		
		meta.setLore(lore);
		
		NamespacedKey newkey = new NamespacedKey((Plugin) RessourcesRespawn.getInstance(), "blocks");
		meta.getPersistentDataContainer().set(newkey, PersistentDataType.STRING, "blocks");
		
		newItem.setItemMeta(meta);
		
		p.getInventory().setItem(slot, newItem);
		p.sendMessage("§7[§3RessourcesRespawn§7] §aBlocks mode activated !");
		return;
	}

}
