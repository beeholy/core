package org.beeholy.holyCore.utility;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ResourcePack {
    private static ResourcePackInfo getHash() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://holymc.uk/resource_pack/hash.txt"))
                .build();

        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .join();

        return ResourcePackInfo.resourcePackInfo()
                .uri(URI.create("https://holymc.uk/resource_pack/" + response + ".zip"))
                .hash(response)
                .build();
    }

    public static void sendResourcePack(final Player target) {
        final ResourcePackRequest request = ResourcePackRequest.resourcePackRequest()
                .packs(getHash())
                .prompt(Component.text("Please download the resource pack!"))
                .required(true)
                .build();

        // Send the resource pack request to the target audience
        target.sendResourcePacks(request);
    }
}

