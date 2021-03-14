package xyz.n7mn.dev.banshareplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.Connection;

public class ListCommand  implements CommandExecutor {

    private final Connection con;
    public ListCommand(Connection con){
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }
}
