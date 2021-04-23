package xyz.n7mn.dev.banshareplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class ReportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player){

            if (args.length != 1){
                sender.sendMessage(ChatColor.YELLOW + "「/r <理由>」または「/report <理由>」と入力してください。");
                return true;
            }

            Player player = (Player) sender;

            Inventory inventory = Bukkit.createInventory(null, 45);
            for (Player p : Bukkit.getOnlinePlayers()){

                ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta itemMeta = (SkullMeta) stack.getItemMeta().clone();
                itemMeta.setPlayerProfile(p.getPlayerProfile());
                stack.setItemMeta(itemMeta);
                inventory.addItem(stack);
            }

            int i = 0;
            for (int x = (Bukkit.getOnlinePlayers().size() + 1); x <= 45; x++){
                ItemStack stack = new ItemStack(Material.STAINED_GLASS_PANE);
                if (i == 0){
                    ItemMeta meta = stack.getItemMeta();
                    meta.setLocalizedName("理由:" + args[0]);
                    stack.setItemMeta(meta);
                }
                inventory.setItem(x - 1, stack);
                i++;
            }

            player.openInventory(inventory);

        }

        return true;
    }
}
