package org.beeholy.holyCore.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.beeholy.holyCore.HolyCore;
import org.beeholy.holyCore.gui.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Vouchers {
    private static MiniMessage mm = MiniMessage.miniMessage();

    private static File vouchersFile;
    private static FileConfiguration vouchersConfig;

    private static List<String> vouchers;

    public static void setup() {
        JavaPlugin plugin = HolyCore.getInstance();

        vouchersFile = new File(plugin.getDataFolder(), "vouchers.yml");
        vouchers = new ArrayList<>();

        if (!vouchersFile.exists()) {
            vouchersFile.getParentFile().mkdirs();
            plugin.saveResource("vouchers.yml", false); // copies from jar if present
        }

        vouchersConfig = YamlConfiguration.loadConfiguration(vouchersFile);
        // From config, load into static hashmap
        vouchers.addAll(vouchersConfig.getKeys(false));
    }

    public static void reload() {
        vouchers.clear(); // Clear old tags
        vouchersConfig = YamlConfiguration.loadConfiguration(vouchersFile);
        vouchers.addAll(vouchersConfig.getKeys(false));
    }
    public static String getPermission(String name){
        return vouchersConfig.getString(name + ".permission");
    }
    public static String getCommand(String name){
        return vouchersConfig.getString(name + ".command");
    }
    public static List<String> getLore(String name){
        return (List<String>) vouchersConfig.getList(name + ".lore");
    }
    public static String getCustomModel(String name){
        return vouchersConfig.getString(name + ".custom_model");
    }
    public static String getSuccessMessage(String name){
        return vouchersConfig.getString(name + ".success_message");
    }
    public static List<String> getVouchers(){
        return vouchers;
    }
    public static boolean hasVoucher(String name){
        return vouchersConfig.getKeys(false).contains(name);
    }
    public static ItemStack getItemStack(String name){
        ItemStack item = ItemStack.of(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(mm.deserialize("<italic:false>" + vouchersConfig.getString(name + ".name") + "</italic>"));

        List<String> rawLore = Vouchers.getLore(name);
        List<Component> styledLore = new ArrayList<>();

        for (String line : rawLore) {
            styledLore.add(mm.deserialize("<italic:false>" + line + "</italic>"));
        }

        meta.lore(styledLore);
        if(getCustomModel(name) != null) {
            CustomModelDataComponent customModel = meta.getCustomModelDataComponent();
            customModel.setStrings(List.of(getCustomModel(name)));
            meta.setCustomModelDataComponent(customModel);
        }
        NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "voucher_name");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, name);
        item.setItemMeta(meta);

        if(vouchersConfig.getInt(name + ".stack_size", -1) != -1){
            item.setAmount(vouchersConfig.getInt(name + ".stack_size"));
        }
        return item;
    }
}