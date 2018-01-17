package fr.etrenak.playerlist;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerList extends JavaPlugin implements CommandExecutor, Listener
{

	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		if(command.getName().equalsIgnoreCase("PlayerList"))
		{
			if(args.length < 1)
			{
				args = new String[1];
				args[0] = "1";
			}
			int page = 0;
			try
			{
				page = Integer.parseInt(args[0]);
			}catch(NumberFormatException e)
			{
				e.printStackTrace();
			}

			if(page < 0)
				page = 0;

			else if(page > Bukkit.getOnlinePlayers().size() / (4 * 9))
				page = Bukkit.getOnlinePlayers().size() / (4 * 9);

			page++;

			if(!(sender instanceof Player))
			{
				sender.sendMessage("§cOnly player can use this command !");
				return true;
			}

			Inventory inv = Bukkit.createInventory(null, 5 * 9, "§bListe des joueurs -§1 Page " + page);
			for(int i = (page - 1) * 4 * 9; i < (page - 1) * 4 * 9 + (4 * 9); i++)
			{
				Player p = (Player) Bukkit.getOnlinePlayers().toArray()[i];
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
				SkullMeta skullMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
				skullMeta.setDisplayName("§r" + p.getDisplayName());
				skullMeta.setOwner(p.getName());
				skullMeta.setLore(Arrays.asList("§6Clic droit : §7Se téléporter à " + p.getDisplayName(), "§6Clic gauche : §7 Ouvrir l'inventaire de modération\n" + "§7et passer en mode §cVanish"));

				skull.setItemMeta(skullMeta);
				inv.addItem(new ItemStack(skull));

				if(i + 1 >= Bukkit.getOnlinePlayers().size())
					break;
			}

			if(page < Bukkit.getOnlinePlayers().size() / (4 * 9))
			{
				ItemStack next = new ItemStack(Material.ARROW);
				ItemMeta nxtMeta = next.getItemMeta();
				nxtMeta.setDisplayName("§6Page suivante (" + (page + 1) + ")");
				next.setItemMeta(nxtMeta);

				inv.setItem(44, next);
			}

			if(page > 1)
			{
				ItemStack back = new ItemStack(Material.ARROW);
				ItemMeta bckMeta = back.getItemMeta();
				bckMeta.setDisplayName("§6Page précedente (" + (page - 1) + ")");
				back.setItemMeta(bckMeta);

				inv.setItem(37, back);
			}

			ItemStack ccBack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
			SkullMeta ccBackMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
			ccBackMeta.setDisplayName("§2Retourner au menu");
			ccBackMeta.setOwner("MHF_Arrowleft");
			ccBack.setItemMeta(ccBackMeta);
			inv.setItem(36, ccBack);

			((Player) sender).openInventory(inv);
			((Player) sender).updateInventory();
		}
		return true;
	}

	@EventHandler
	public void click(InventoryClickEvent e)
	{
		if(e.getInventory().getName().startsWith("§bListe des joueurs -§1 Page "))
		{
			e.setCancelled(true);

			if(e.getClickedInventory().getName().startsWith("§bListe des joueurs -§1 Page "))
			{
				if(e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null)
					return;
				if(e.getCurrentItem().getType().equals(Material.ARROW))
				{
					Bukkit.dispatchCommand(e.getWhoClicked(), "PlayerList " + e.getCurrentItem().getItemMeta().getDisplayName().replaceAll("(§6)?\\D?\\D", ""));
					return;
				}

				else if(e.getCurrentItem().getType().equals(Material.SKULL_ITEM))
				{
					SkullMeta skullMeta = (SkullMeta) e.getCurrentItem().getItemMeta();
					Player player = Bukkit.getPlayer(skullMeta.getOwner());

					if(skullMeta.getOwner().equalsIgnoreCase("MHF_ArrowLeft"))
						Bukkit.dispatchCommand(e.getWhoClicked(), "cc open staffs");

					else if(e.isRightClick())
						if(player == null)
							e.getWhoClicked().sendMessage("§cLe joueur est déconnecté");
						else
							e.getWhoClicked().teleport(player);

					else if(e.isLeftClick())
					{
						Bukkit.dispatchCommand(e.getWhoClicked(), "ev");
						Bukkit.dispatchCommand(e.getWhoClicked(), "staff:staff");
					}

				}
			}
		}
	}
}
