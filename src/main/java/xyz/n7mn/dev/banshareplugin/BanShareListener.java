package xyz.n7mn.dev.banshareplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.banshareplugin.data.BanData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanShareListener implements Listener {

    private Connection con;
    private final String area;
    private final Plugin plugin = Bukkit.getPluginManager().getPlugin("BanSharePlugin");

    public BanShareListener(Connection con, String area){
        this.con = con;
        this.area = area;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncPlayerPreLoginEvent (AsyncPlayerPreLoginEvent e){

        UUID uuid = e.getUniqueId();
        List<BanData> banDataList = new ArrayList<>();

        try {

            try {
                PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList` WHERE Active = 1");
                statement.execute();
            } catch (SQLException ex){
                String pass = "jdbc:mysql://" + plugin.getConfig().getString("mysqlServer") + ":" + plugin.getConfig().getInt("mysqlPort") + "/" + plugin.getConfig().getString("mysqlDatabase") + plugin.getConfig().getString("mysqlOption");
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

                try {
                    con = DriverManager.getConnection(pass, plugin.getConfig().getString("mysqlUsername"), plugin.getConfig().getString("mysqlPassword"));
                    con.setAutoCommit(true);
                } catch (SQLException exx){
                    Bukkit.getServer().getPluginManager().disablePlugin(plugin, true);
                }
            }

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
        } catch (SQLException thr) {
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

}
