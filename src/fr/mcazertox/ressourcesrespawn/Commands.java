package fr.mcazertox.ressourcesrespawn;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class Commands implements CommandExecutor {
	
	private RessourcesRespawn main;
	
	public Commands(RessourcesRespawn ressourcesrespawn) {
		this.main = ressourcesrespawn;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {
		if (!cmd.getName().equalsIgnoreCase("rr"))return true;
		if(args.length == 0) {
			if(sender.hasPermission("ressourcesrespawn.help")) {
				showHelp(sender);
				return true;
			}
			else {
				noPerm(sender);
				return true;
			}
		}
		if(args.length == 1) {
			if(args[0].equals("help")) {
				if(sender.hasPermission("ressourcesrespawn.help")) {
					showHelp(sender);
					return true;
				}
				else {
					noPerm(sender);
					return true;
				}
			}
			if(args[0].equals("reload")) {
				if(sender.hasPermission("ressourcesrespawn.reload")) {
					DataConfig.reload();
					main.reloadConfig();
					main.loadWhiteList();
					main.debug();
					sender.sendMessage("§7[§3RessourcesRespawn§7] §aConfig reload !");
					return true;
				}
				else {
					noPerm(sender);
					return true;
				}
			}
			if(args[0].equals("give")) {
				if(sender.hasPermission("ressourcesrespawn.give")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§cYou must be a player to execute this command !");
						return true;
					}
					Player p = (Player) sender;
					Material mat = Material.valueOf(main.getConfig().getString("blocks.wand-material"));
					ItemStack it = new ItemStack(mat);
					ItemMeta meta = it.getItemMeta();
					
					List<String> lore = new ArrayList<>();
					lore.add(" ");
					lore.add("§7(§eRight-Click§7) §ato add this block to the respawn list");
					lore.add("§7(§eLeft-Click§7) §ato remove this block from the respawn list");
					//lore.add("§7(§eShift + Right-Click§7) §ato change wand into crops wand");
					
					meta.setDisplayName("§3RessourcesRespawn Wand §3- §b§lBlocks mode");
					meta.setLore(lore);
					NamespacedKey key = new NamespacedKey((Plugin) RessourcesRespawn.getInstance(), "blocks");
		            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "blocks");
					it.setItemMeta(meta);
					p.getInventory().addItem(it);
					return true;
				}
				else {
					noPerm(sender);
					return true;
				}
			}
			if(args[0].equals("buildmode")) {
				if(sender.hasPermission("ressourcesrespawn.buildmode")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§cOnly players can perform this command !");
						return true;
					}
					Player p = (Player) sender;
					RessourcesRespawn.setBuildingBlocksMode(p.getUniqueId(), true);
					p.sendMessage("§7[§eRessources Respawn§7] §3You activated §bBlocks §3build mode !");
					return true;
				}
				else {
					noPerm(sender);
					return true;
				}
			}
			if(args[0].equals("stopbuilding")) {
				if(sender.hasPermission("ressourcesrespawn.stopbuilding")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§cOnly players can perform this command !");
						return true;
					}
					Player p = (Player) sender;
					RessourcesRespawn.setBuildingBlocksMode(p.getUniqueId(), false);
					RessourcesRespawn.setBuildingCropsMode(p.getUniqueId(), false);
					p.sendMessage("§7[§eRessources Respawn§7] §3Build mode disabled !");
					return true;
				}
				else {
					noPerm(sender);
					return true;
				}
			}
		}
		if(args.length == 2) {
			
		}
		return false;
	}
	public void noPerm(CommandSender sender) {
		sender.sendMessage("§cYou don't have the permission :/");
	}
	
	public void showHelp(CommandSender sender) {
		sender.sendMessage("§7§m             §3§lRessourcesRespawn§7§m             ");
		sender.sendMessage("");
		sender.sendMessage("  §6- §c/rr help     §7Show this page");
		sender.sendMessage("  §6- §c/rr reload    §7Reload the config");
		sender.sendMessage("  §6- §c/rr give    §7Give you the wand");
		sender.sendMessage("  §6- §c/rr buildmode    §7Allow you to auto-saving blocks that you're placing in the respawn config");
		sender.sendMessage("  §6- §c/rr stopbuilding");
		sender.sendMessage("");
	}

}
