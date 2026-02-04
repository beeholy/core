package org.beeholy.holyCore.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.chat.Colors;
import org.beeholy.holyCore.chat.Gradients;
import org.beeholy.holyCore.chat.PlayerData;
import org.beeholy.holyCore.chat.Tags;
import org.beeholy.holyCore.utility.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PermissionMenu extends PaginatedMenu<String> {
    private final NamespacedKey key;
    private final String permission;
    private final Player player;
    private final String displayName;

    public PermissionMenu(Component title, String permission, String key, Player player, String displayName) {
        super(title, 36, List.of());
        this.permission = permission;
        this.player = player;
        this.key = new NamespacedKey(HolyCore.getInstance(), key);
        this.displayName = displayName;
        var items = PlayerData.getPlayerPermissionList(player, permission);

        switch (permission) {
            case "color":
                if (player.hasPermission("colors.all"))
                    items = Colors.getColors();
                break;
            case "gradient":
                if (player.hasPermission("gradients.all"))
                    items = Gradients.getGradients();
                break;
            case "tag":
                if (player.hasPermission("tags.all"))
                    items = Tags.getTags();
                break;
        }

        setItems(items);
        updateInventory();
    }

    @Override
    protected void updateInventory() {
        super.updateInventory();
        getInventory().setItem(pageSize, createControlItem("left_arrow", "<italic:false><gray>Style<gray>"));
    }

    @Override
    protected ItemStack buildItem(String data) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        MiniMessage mm = MiniMessage.miniMessage();
        meta.displayName(TextUtils.deserialize(displayName, data).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        meta.lore(List.of(
                Component.empty(),
                mm.deserialize("<italic:false><gold>| </gold><white>Click to apply " + permission + "</white></italic>"),
                Component.empty()
        ));

        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, data);

        item.setItemMeta(meta);
        return item;
    }

    @Override
    public void handleClick(InventoryClickEvent event) {
        super.handleClick(event);
        event.setCancelled(true);
        if (event.getSlot() == pageSize) {
            CommandSender sender = event.getWhoClicked();
            Bukkit.getServer().dispatchCommand(sender, "style");
        }
        MiniMessage mm = MiniMessage.miniMessage();
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null) {
            return;
        }
        String data = event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (data == null) {
            return;
        }
        PlayerData.setString(player, key.getKey(), data);
        event.getWhoClicked().closeInventory();
    }
}
