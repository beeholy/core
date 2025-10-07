package org.beeholy.holyCore.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.utility.Language;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RTPCommand implements BasicCommand {
    private final Map<UUID, Long> cooldownMap = new ConcurrentHashMap<>();
    private final long cooldownTimeMs;

    public RTPCommand(long cooldownTimeMs) {
        this.cooldownTimeMs = cooldownTimeMs;
    }

    HolyCore plugin = HolyCore.getInstance();
    FileConfiguration config = plugin.getConfig();
    MiniMessage mm = MiniMessage.miniMessage();



    private Location getSafeLocation(World world){
        double wbX = world.getWorldBorder().getCenter().getBlockX();
        double wbZ = world.getWorldBorder().getCenter().getBlockZ();
        double wbSize = world.getWorldBorder().getSize();
        double minX = Math.floor(wbX - (wbSize / 2));
        double maxX = Math.floor(wbX + (wbSize /2));
        double minZ = Math.floor(wbZ - (wbSize / 2));
        double maxZ = Math.floor(wbZ + (wbSize /2));
        // find location using do while until safe location is found (no lava, etc), then teleport,
        Location safeLocation = null;
        int attempts = 0;

        do {
            double x = minX + Math.random() * (maxX - minX);
            double z = minZ + Math.random() * (maxZ - minZ);
            int y = world.getHighestBlockYAt((int) x, (int) z);
            Location loc = new Location(world, x, y, z);
            Block block = loc.getBlock();

            // Check if the block is safe (not lava, fire, water etc.)
            if (!block.isLiquid() && block.getType() != Material.LAVA && block.getType() != Material.FIRE) {
                safeLocation = loc.add(0,1,0);
            }

            attempts++;
        } while (safeLocation == null && attempts < 10);
        return safeLocation;
    }
    private void teleportPlayer(Player player, String worldString) {
        World world = Bukkit.getWorld(worldString);
        if(world == null){
            player.sendMessage(TextUtils.deserialize(Language.get("rtp_invalid_world")));
            return;
        }
        player.sendMessage(TextUtils.deserialize(Language.get("rtp_teleporting")));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Location safeLocation = getSafeLocation(world);
            Bukkit.getScheduler().runTask(plugin, () -> player.teleport(safeLocation));
        });
    }
    @Override
    public void execute(CommandSourceStack source, String[] args) {

        // Op Commands
        if(args.length == 2) {
            Player otherPlayer = Bukkit.getPlayer(args[1]);
            CommandSender sender = source.getSender();
            if(!sender.hasPermission("rtp.others")) {
                sender.sendMessage(TextUtils.deserialize(Language.get("perms_invalid")));
                return;
            }
            if(otherPlayer != null){
                // teleport other player
                teleportPlayer(otherPlayer, args[0]);
                return;
            }

        }
        if(!(source.getSender() instanceof Player player)) {
           source.getSender().sendMessage("Only players can run this command");
           return;
        }
        //Player player = (Player) source;

        // Argument logic
        if(args.length == 0) {
            // show gui then return
        }
        if(args.length != 1) {
            player.sendMessage(Language.get("rtp_command_usage"));
            return;
        }
        if(!player.hasPermission("rtp." + args[0])) {
            player.sendMessage(Language.get("perms_invalid"));
            return;
        }

        // Cooldown Logic
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();

        if (cooldownMap.containsKey(uuid)) {
            long lastUse = cooldownMap.get(uuid);
            long elapsed = now - lastUse;

            if (elapsed < cooldownTimeMs) {
                long timeLeft = (cooldownTimeMs - elapsed) / 1000;
                player.sendMessage(TextUtils.deserialize(Language.get("cooldown_message"), String.valueOf(timeLeft)));
                return;
            }
        }
        cooldownMap.put(uuid, now);

        // Teleport player
        teleportPlayer(player,args[0]);
    }
}
