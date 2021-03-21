package xyz.n7mn.dev.banshareplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

public final class BanSharePlugin extends JavaPlugin {

    Connection con = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        String pass = "jdbc:mysql://" + getConfig().getString("mysqlServer") + ":" + getConfig().getInt("mysqlPort") + "/" + getConfig().getString("mysqlDatabase") + getConfig().getString("mysqlOption");

        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());

            con = DriverManager.getConnection(pass, getConfig().getString("mysqlUsername"), getConfig().getString("mysqlPassword"));
            con.setAutoCommit(true);
        } catch (SQLException e){
            e.printStackTrace();
            this.onDisable();
        }

        getServer().getPluginManager().registerEvents(new BanShareListener(con, getConfig().getString("Area")), this);
        getCommand("gban").setExecutor(new BanCommand(con));
        getCommand("gban").setTabCompleter(new CommandTab());
        getCommand("baninfo").setExecutor(new InfoCommand(this, con));

        getCommand("report").setExecutor(new ReportCommand());
        getCommand("r").setExecutor(new ReportCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        if (con != null){
            new Thread(() -> {
                try {
                    con.close();
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
