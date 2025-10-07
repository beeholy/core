package org.beeholy.holyCore;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public final class BigChest {

    private static final BigChest instance = new BigChest();
    private final Map<UUID, Map<String, Integer>> playerChests = new HashMap<>();
    private NamespacedKey key = new NamespacedKey(HolyCore.getInstance(), "player-big-chest");
    private Gson gson = new Gson();

    public BigChest() {

    }

    public void add(Player player, ItemStack itemStack) {
        UUID playerUUID = player.getUniqueId();
        String stackMaterial = itemStack.getType().toString();
        int stackAmount = itemStack.getAmount();
        // Check if the player already has a chest, if not, create a new one
        playerChests.computeIfAbsent(playerUUID, k -> new HashMap<>());

        // Get the player's chest map (material, amount) and update it
        Map<String, Integer> playerChest = playerChests.get(playerUUID);
        playerChest.merge(stackMaterial, stackAmount, Integer::sum);
        save(player);
    }

    // returns true if item has been fully removed
    public void remove(Player player, Material material, int count) {
        UUID playerID = player.getUniqueId();
        String materialString = material.toString();
        Inventory playerInv = player.getInventory();
        if (playerChests.containsKey(playerID)) {
            Map<String, Integer> playersChest = playerChests.get(playerID);
            if (playersChest.containsKey(materialString)) {
                // Player has item
                int itemChestQuantity = playersChest.get(materialString);
                HashMap<Integer, ItemStack> returnedItems;
                ItemStack playerStack = new ItemStack(material, count);
                if (itemChestQuantity < count) {
                    // can not remove
                    player.sendMessage("Big Chest > You dont have enough of this item");
                } else if (itemChestQuantity == count) {
                    // Player has exactly what we are removing
                    returnedItems = playerInv.addItem(playerStack);
                    if (returnedItems.isEmpty()) {
                        // removed all (re-render)
                        playersChest.remove(materialString);
                    } else {
                        // some items were returned
                        int returnedItemAmount = returnedItems.get(0).getAmount();
                        playersChest.replace(materialString, itemChestQuantity - (count - returnedItemAmount));
                    }
                } else {
                    // player has more than we're removing
                    returnedItems = playerInv.addItem(playerStack);
                    if (returnedItems.isEmpty()) {
                        // No returns
                        playersChest.replace(materialString, itemChestQuantity - count);
                    } else {
                        // Put back returns
                        int returnedItemAmount = returnedItems.get(0).getAmount();
                        playersChest.replace(materialString, itemChestQuantity - (count - returnedItemAmount));
                    }
                }
            }
        }
        save(player);
    }

    public int getItemQuantity(Player player, Material material) {
        UUID playerUUID = player.getUniqueId();
        String materialString = material.toString();

        // Check if the player's chest exists and contains the material
        if (playerChests.containsKey(playerUUID)) {
            Map<String, Integer> playerChest = playerChests.get(playerUUID);
            if (playerChest.containsKey(materialString)) {
                return playerChest.get(materialString); // Return the quantity of the item
            }
        }

        return 0; // If player or material isn't found, return 0
    }

    public void save(Player player) {
        Map<String, Integer> playerChest = playerChests.get(player.getUniqueId());
        String serializedContents = gson.toJson(playerChest);

        PersistentDataContainer dataContainer = player.getPersistentDataContainer();

        dataContainer.set(key, PersistentDataType.STRING, serializedContents);
    }

    public void load(Player player) {
        PersistentDataContainer dataContainer = player.getPersistentDataContainer();
        if (dataContainer.has(key, PersistentDataType.STRING)) {
            String serializedContents = dataContainer.get(key, PersistentDataType.STRING);
            Map<String, Integer> contentsMap = gson.fromJson(serializedContents, new TypeToken<Map<String, Integer>>() {
            }.getType());
            playerChests.put(player.getUniqueId(), contentsMap);
        }
    }

    public void quit(Player player) {
        save(player);
        playerChests.remove(player.getUniqueId());
    }

    public Map<String, Integer> getContents(UUID playerUUID) {
        // Get the player's chest or an empty map if it doesn't exist
        Map<String, Integer> playerChest = playerChests.getOrDefault(playerUUID, new HashMap<>());

        // Create a list of entries and sort it by value in descending order
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(playerChest.entrySet());
        entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Create a new LinkedHashMap to maintain the sorted order
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public Map<String, Integer> getContentsSortedByName(UUID playerUUID) {
        // Get the player's chest or an empty map if it doesn't exist
        Map<String, Integer> playerChest = playerChests.getOrDefault(playerUUID, new HashMap<>());

        // Create a list of entries and sort it by key (item name) in alphabetical order
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(playerChest.entrySet());
        entries.sort(Map.Entry.comparingByKey());

        // Create a new LinkedHashMap to maintain the sorted order
        Map<String, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public String getContentsString(UUID playerUUID) {
        if (playerChests.containsKey(playerUUID)) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(playerChests.get(playerUUID).entrySet());

            // Sort the entries by value (quantity) in descending order
            entries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            StringBuilder returnString = new StringBuilder();
            for (Map.Entry<String, Integer> entry : entries) {
                returnString.append(entry.getKey())
                        .append(" : ")
                        .append(entry.getValue())
                        .append("\n");
            }
            return returnString.toString();
        }
        return "No items in chest!";
    }

    public static BigChest getInstance() {
        return instance;
    }
}
