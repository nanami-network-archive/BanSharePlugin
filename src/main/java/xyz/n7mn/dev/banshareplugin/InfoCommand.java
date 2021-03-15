package xyz.n7mn.dev.banshareplugin;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import xyz.n7mn.dev.banshareplugin.data.BanData;
import xyz.n7mn.dev.banshareplugin.data.MCID2UUIDAPIResult;
import xyz.n7mn.dev.banshareplugin.data.UUID2MCIDResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InfoCommand implements CommandExecutor {

    private Connection con;
    private final Plugin plugin;
    public InfoCommand(Plugin plugin, Connection con){
        this.plugin = plugin;
        this.con = con;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp() && !sender.hasPermission("7misys.info")){
            sender.sendMessage(ChatColor.RED + "権限がありません！");
            return true;
        }


        if (args.length == 0){
            new Thread(()->{
                List<BanData> banDataList = new ArrayList<>();
                int AreaBanCount = 0;
                try {
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList` WHERE Active = 1");
                        statement.execute();
                        statement.close();
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

                        if (set.getString("Area").equals(plugin.getConfig().getString("Area"))){
                            AreaBanCount++;
                        }
                    }
                } catch (SQLException thr) {
                    thr.printStackTrace();
                }

                sender.sendMessage("" +
                        ChatColor.GREEN + "--- BAN情報 ---\n" +
                        ChatColor.GREEN + "有効件数 : " + banDataList.size() + "件\n" +
                        ChatColor.GREEN + "内 "+plugin.getConfig().getString("Area")+"のみ : " + AreaBanCount + "件\n" +
                        ChatColor.YELLOW + "詳細な情報を取得したい場合は/baninfo <ID>または/baninfo <MCID>と入力してください。"
                );
            }).start();

            return true;
        }

        if (args.length != 1){
            return true;
        }

        int id = 0;
        try {
            id = Integer.parseInt(args[0]);
        } catch (Exception e){
            id = -1;
        }

        OkHttpClient client = new OkHttpClient();

        if (id <= 0){

            String url = "https://api.mojang.com/users/profiles/minecraft/" + args[0];

            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                MCID2UUIDAPIResult result = new Gson().fromJson(json, MCID2UUIDAPIResult.class);
                String uuidText = result.getName();
                response.close();

                UUID uuid = UUID.fromString(uuidText.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
                try {
                    try {
                        PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList`");
                        statement.execute();
                        statement.close();
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
                } catch (SQLException thr) {
                    thr.printStackTrace();
                }

                PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList` WHERE UserUUID = ?");
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();

                boolean s = false;
                while (set.next()){
                    s = true;

                    sender.sendMessage("" +
                            ChatColor.GREEN + "---- BAN情報 (ID "+set.getInt("BanID")+") ----\n" +
                            ChatColor.GREEN + "MinecraftID : " + args[0] + "\n" +
                            ChatColor.GREEN + "理由 : " + set.getString("Reason") + "\n" +
                            ChatColor.GREEN + "有効 : " + set.getBoolean("Active") + "\n" +
                            ChatColor.GREEN + "実行日付 : " + set.getString("ExecuteDate") + "\n" +
                            ChatColor.GREEN + "期限日付 : " + set.getString("EndDate") + "\n"
                    );
                }
                set.close();
                statement.close();
                if (s){
                    return true;
                }

                sender.sendMessage(ChatColor.YELLOW + "BANされていないユーザーです");

            } catch (Exception e){
                e.printStackTrace();
            }

        }


        try {

            try {
                try {
                    PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList`");
                    statement.execute();
                    statement.close();
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
            } catch (SQLException thr) {
                thr.printStackTrace();
            }

            PreparedStatement statement = con.prepareStatement("SELECT * FROM `BanList` WHERE BanID = ?");
            statement.setInt(1, id);
            ResultSet set = statement.executeQuery();
            if (set.next()){

                String username = "";
                Request request = new Request.Builder()
                        .url("https://api.mojang.com/user/profiles/"+set.getString("UserUUID").replaceAll("-","")+"/names")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    String json = response.body().string();
                    UUID2MCIDResult result = new Gson().fromJson(json, UUID2MCIDResult.class);
                    username = result.getName();
                    response.close();
                } catch (Exception e){
                    e.printStackTrace();
                }

                sender.sendMessage("" +
                        ChatColor.GREEN + "---- BAN情報 (ID "+set.getInt("BanID")+") ----\n" +
                        ChatColor.GREEN + "MinecraftID : " + username + "\n" +
                        ChatColor.GREEN + "理由 : " + set.getString("Reason") + "\n" +
                        ChatColor.GREEN + "有効 : " + set.getBoolean("Active") + "\n" +
                        ChatColor.GREEN + "実行日付 : " + set.getString("ExecuteDate") + "\n" +
                        ChatColor.GREEN + "期限日付 : " + set.getString("EndDate") + "\n"
                );

                set.close();
                statement.close();
                return true;
            }

            sender.sendMessage(ChatColor.YELLOW + "BANされていないユーザーです");

        } catch (Exception e){
            e.printStackTrace();
        }

        return true;
    }
}
