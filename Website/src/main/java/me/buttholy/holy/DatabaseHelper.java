package me.buttholy.holy;

import org.bukkit.Bukkit;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;


public class DatabaseHelper {
    public static boolean findPlayer(UUID uuid) {
        try (var conn = DriverManager.getConnection("jdbc:sqlite:plugins/HolyMod/database.db");
             var stmt = conn.createStatement()) {
            var sql = "SELECT uuid FROM users WHERE uuid = '" + uuid + "';";
            var result = stmt.executeQuery(sql);
            return result.next();
        } catch (SQLException e ){
            Bukkit.getServer().getLogger().warning(e.getMessage());
        }
        return false;
    }

    public static boolean addPlayer(UUID uuid, String username) {
        try(var conn = DriverManager.getConnection("jdbc:sqlite:plugins/HolyMod/database.db");
            var stmt = conn.createStatement()){
            var sql = "INSERT INTO users(uuid,username) VALUES('" + uuid + "','" + username + "');";
            stmt.execute(sql);
            Bukkit.getServer().getLogger().info("Added " + username + " to database.");
            return true;
        } catch (SQLException e) {
            Bukkit.getServer().getLogger().severe(e.getMessage());
        }
        return false;
    }
}
