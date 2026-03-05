package org.beeholy.holyCore.commands.admin;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.items.ItemFactory;
import org.beeholy.holyCore.skins.Skin;
import org.beeholy.holyCore.skins.SkinManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class SkinCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (args[0].equals("applicator") && args.length == 3) {

            Player player = Bukkit.getPlayerExact(args[2]);
            if (player != null) {
                SkinManager manager = HolyCore.getInstance().getSkinService().getSkinManager();
                Skin skin = manager.getRegistry().get(args[1]);
                if (skin == null) {
                    source.getSender().sendMessage("Skin doesn't exist");
                }
                player.give(ItemFactory.createSkinApplicator(skin));
            } else {
                source.getSender().sendMessage("Player isn't online");
            }

        }
        if (args[0].equals("remover") && args.length == 2) {

            Player player = Bukkit.getPlayerExact(args[1]);
            if (player != null) {
                NamespacedKey removerKey = new NamespacedKey(HolyCore.getInstance(), "skin_remover");
                ItemStack itemStack = ItemStack.of(Material.PAPER);
                ItemMeta meta = itemStack.getItemMeta();
                meta.getPersistentDataContainer().set(removerKey, PersistentDataType.BOOLEAN, true);
                itemStack.setItemMeta(meta);
                player.give(itemStack);
            } else {
                source.getSender().sendMessage("Player isn't online");
            }

        }
    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if(args.length == 1) {
            return List.of("applicator");
        }
        if(args.length == 2) {
            return HolyCore.getInstance().getSkinService().getSkinManager().getRegistry().getKeys();
        }
        return List.of();
    }

    @Override
    public @Nullable String permission() {
            return "holycore.admin";
    }
}

