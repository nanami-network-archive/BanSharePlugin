package xyz.n7mn.dev.banshareplugin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import xyz.n7mn.dev.banshareplugin.data.BanData;
import xyz.n7mn.dev.nanamilib.api.MySQL;
import xyz.n7mn.dev.nanamilib.event.DiscordReadyEvent;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BanShareListener implements Listener {

    private final String area;
    private final Plugin plugin;

    private JDA jda = null;

    public BanShareListener(String area, Plugin plugin){
        this.area = area;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncPlayerPreLoginEvent (AsyncPlayerPreLoginEvent e){

        UUID uuid = e.getUniqueId();
        List<BanData> banDataList = new ArrayList<>();

        try {
            Connection connect = MySQL.getConnect("BanSharePlugin");

            PreparedStatement statement = connect.prepareStatement("SELECT * FROM `BanList` WHERE Active = 1");
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

            MySQL.closeConnect(connect);

        } catch (Exception thr) {
            thr.printStackTrace();

        }

        for (BanData data : banDataList){
            if (data.isActive() && data.getUserUUID().equals(uuid)){
                if (data.getArea().equals("all") || data.getArea().equals(area)){
                    e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, data.getReason());
                    return;
                }
            }
        }
    }

    @EventHandler
    public void InventoryClickEvent (InventoryClickEvent e){



    }

    @EventHandler
    public void InventoryCreativeEvent (InventoryCreativeEvent e){


    }

}
