package xyz.n7mn.dev.banshareplugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class ReportCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        /*
        if (args.length == 0 && sender instanceof Player){
            Player player = (Player) sender;

            Inventory inventory = Bukkit.createInventory(null, 36,"通報プレーヤー選択");
            // String version = Bukkit.getServer().getVersion();

            for (Player player1 : Bukkit.getServer().getOnlinePlayers()){
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta)item.getItemMeta().clone();
                meta.setOwner(player1.getName());
                item.setItemMeta(meta);

                inventory.addItem(item);
            }

            player.openInventory(inventory);
        }
        */
        return true;
    }
}
