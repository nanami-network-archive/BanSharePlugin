package xyz.n7mn.dev.banshareplugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import xyz.n7mn.dev.banshareplugin.data.BanData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanShareListener implements Listener {

    private final Connection con;
    private final String area;

    public BanShareListener(Connection con, String area){
        this.con = con;
        this.area = area;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncPlayerPreLoginEvent (AsyncPlayerPreLoginEvent e){

        UUID uuid = e.getUniqueId();
        List<BanData> banDataList = new ArrayList<>();

        try {
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
