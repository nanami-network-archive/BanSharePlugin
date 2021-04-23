package xyz.n7mn.dev.banshareplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.banshareplugin.data.BanData;

import java.sql.*;
import java.util.*;

public class BanShareListener implements Listener {

    private final String area;
    private final Plugin plugin;

    public BanShareListener(String area, Plugin plugin){
        this.area = area;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncPlayerPreLoginEvent (AsyncPlayerPreLoginEvent e){

        UUID uuid = e.getUniqueId();
        List<BanData> banDataList = new ArrayList<>();

        try {

            boolean found = false;
            Enumeration<Driver> drivers = DriverManager.getDrivers();

            while (drivers.hasMoreElements()){
                Driver driver = drivers.nextElement();
                if (driver.equals(new com.mysql.cj.jdbc.Driver())){
                    found = true;
                    break;
                }
            }

            if (!found){
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            }

            Connection con = DriverManager.getConnection("jdbc:mysql://" + plugin.getConfig().getString("mysqlServer") + ":" + plugin.getConfig().getInt("mysqlPort") + "/" + plugin.getConfig().getString("mysqlDatabase") + plugin.getConfig().getString("mysqlOption"), plugin.getConfig().getString("mysqlUsername"), plugin.getConfig().getString("mysqlPassword"));

            PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList` WHERE Active = 1");
            ResultSet set = statement.executeQuery();

            while(set.next()){
                banDataList.add(
                        new BanData(
                                set.getInt("BanID"),
                                UUID.fromString(set.getString("UserUUID")),
                                set.getString("Reason"),
                                set.getString("Area"),
                                set.getString("IP"),
                                set.getDate("EndDate"),
                                set.getDate("ExecuteDate"),
                                UUID.fromString(set.getString("ExecuteUserUUID")),
                                set.getBoolean("Active")
                        )
                );
            }

            set.close();
            statement.close();

            con.close();

        } catch (Exception thr) {
            thr.printStackTrace();

        }

        for (BanData data : banDataList){
            if (data.isActive() && data.getUserUUID().equals(uuid)){
                if (data.getArea().equals("all") || data.getArea().equals(area)){
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "以下の理由でBANされています。\n"+data.getReason());
                    return;
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void InventoryClickEvent (InventoryClickEvent e){
        Inventory inventory = e.getClickedInventory();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        if (inventory == null){
            return;
        }

        ItemStack item = e.getCurrentItem();
        if (item == null){
            return;
        }
        item = item.clone();
        ItemStack finalItem = item;
        ItemStack reason = inventory.getItem(onlinePlayers.size());

        if (reason == null){
            return;
        }

        if (reason.getItemMeta() == null){
            return;
        }

        if (reason.getItemMeta().getLocalizedName() == null || reason.getItemMeta().getLocalizedName().length() == 0){
            return;
        }

        if (!reason.getItemMeta().getLocalizedName().startsWith("理由")){
            return;
        }

        e.setCancelled(true);
        inventory.clear();
        e.getView().close();

        if (!item.getType().equals(Material.SKULL_ITEM)){
            return;
        }

        new Thread(()->{

            SkullMeta meta = (SkullMeta) finalItem.getItemMeta();
            // System.out.println(meta.getOwningPlayer().getName());

            for (Player player : onlinePlayers){
                if (player.isOp() || player.hasPermission("7misys.ban")){
                    player.sendMessage(ChatColor.YELLOW + "" +
                            "[ななみ鯖]"+ChatColor.RESET+" "+meta.getOwningPlayer().getName()+"さんが以下の理由で通報されました。\n" +
                            "          理由 : " + reason.getItemMeta().getLocalizedName().split(":")[1]);
                }
            }

            HumanEntity player = e.getView().getPlayer();
            player.sendMessage(ChatColor.YELLOW + "[ななみ鯖] "+ChatColor.RESET+"通報しました。");
        }).start();

    }

    @EventHandler
    public void InventoryCreativeEvent (InventoryCreativeEvent e){



    }

}
