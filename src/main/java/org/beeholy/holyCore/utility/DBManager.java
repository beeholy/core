package org.beeholy.holyCore.utility;

import org.beeholy.holyCore.HolyCore;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class DBManager {
    private static final DBManager instance = new DBManager();

    private Connection connection;

    public static DBManager getInstance() {
        return instance;
    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            if (this.connection != null && !this.connection.isClosed() && this.connection.isValid(2))
                return;
            final FileConfiguration config = HolyCore.getInstance().getConfig();
            final String jdbcurl = "jdbc:mysql://" + config.getString("mysql.host") + ":" + config.getString("mysql.port") + "/" + config.getString("mysql.database") + "?autoReconnect=true";

            this.connection = DriverManager.getConnection(jdbcurl, config.getString("mysql.username"), config.getString("mysql.password"));

            Bukkit.getLogger().info("[HolyCore] Connected to MysqlDB");
        } catch (final Exception e) {
            Bukkit.getLogger().warning("[HolyCore] Can't connect to MysqlDB: " + e.getMessage());
        }
    }

    public void createSignupLink(UUID uuid, String keyString) {
        connect();
        try {
            final Statement statement = connection.createStatement();
            statement.execute("DELETE FROM signup_link WHERE uuid = '" + uuid + "'");
            statement.execute("INSERT INTO signup_link(uuid,code) VALUES('" + uuid.toString() + "','" + keyString + "')");
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean createUser(@NotNull String name, @NotNull UUID uniqueId) {
        connect();
        try {
            // Check if user exists in DB
            final Statement existsStatement = connection.createStatement();
            final ResultSet result = existsStatement.executeQuery("SELECT * FROM user WHERE uuid = '" + uniqueId + "'");

            if (result.next()) {
                // User exists
                // Check for username updates
                if (!Objects.equals(result.getString("username"), name)) {
                    // Name doesnt match in DB, Log change & Update it
                    final Statement usernameHistory = connection.createStatement();
                    usernameHistory.execute("INSERT INTO username_history(uuid,oldname,newname) VALUES ('" + uniqueId + "','" + result.getString("username") + "','" + name + "')");
                    final Statement usernameUpdate = connection.createStatement();
                    usernameUpdate.execute("UPDATE user SET username = '" + name + "' WHERE uuid = '" + uniqueId + "'");
                    Bukkit.getLogger().info("[HolyCore] Updated " + name + "'s username, formerly: " + result.getString("username"));
                }
                existsStatement.close();
                return false;
            } else {
                // Insert new user into DB
                final Statement statement = connection.createStatement();
                String sql = "INSERT INTO user(uuid,username) VALUES('" + uniqueId + "','" + name + "');";
                statement.execute(sql);
                Bukkit.getLogger().info("[HolyCore] Added " + name + " to the user table");
                statement.close();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public Map<String, Integer> getCratesClaims(UUID uuid) {
        connect();
        HashMap<String, Integer> returnMap = new HashMap<>();
        try {
            final Statement selectStatement = connection.createStatement();
            final ResultSet selectResults = selectStatement.executeQuery(
                    "SELECT * FROM crates_claims WHERE uuid = '" + uuid + "'");
            while (selectResults.next()) {
                String crateName = selectResults.getString("crateName");
                int amount = selectResults.getInt("amount");
                returnMap.put(crateName, amount);
            }
            return returnMap;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void clearCratesClaims(UUID uuid) {
        connect();
        try {
            final Statement selectStatement = connection.createStatement();
            selectStatement.execute(
                    "DELETE FROM crates_claims WHERE uuid = '" + uuid + "'");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean addCratesClaim(UUID uuid, String crateName, int amount) {
        connect();
        try {
            final Statement updateStatement = connection.createStatement();
            updateStatement.execute("UPDATE crates_claims SET amount = amount + " + amount +
                    " WHERE uuid = '" + uuid + "' AND crateName = '" + crateName + "'");
            if (updateStatement.getUpdateCount() == 0) {
                final Statement insertStatement = connection.createStatement();
                insertStatement.execute("INSERT INTO crates_claims(uuid, crateName, amount)" +
                        " VALUES ('" + uuid + "', '" + crateName + "'," + amount + ")");
                insertStatement.close();
            }
            updateStatement.close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (final SQLException e) {
            Bukkit.getLogger().warning("[HolyCore] Server unable to close.");
        }
    }


}
