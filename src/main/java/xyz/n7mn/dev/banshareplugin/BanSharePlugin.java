package xyz.n7mn.dev.banshareplugin;

import org.bukkit.plugin.java.JavaPlugin;

public final class BanSharePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        String area = getConfig().getString("Area");

        getServer().getPluginManager().registerEvents(new BanShareListener(area, this), this);
        getCommand("gban").setExecutor(new BanCommand(this));
        getCommand("gban").setTabCompleter(new CommandTab());
        getCommand("baninfo").setExecutor(new InfoCommand(this));

        getCommand("report").setExecutor(new ReportCommand());
        getCommand("r").setExecutor(new ReportCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
