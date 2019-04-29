package de.mcruben.cloudnetinterface;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.internal.serverselectors.MobSelector;
import de.dytanic.cloudnet.bridge.internal.serverselectors.SignSelector;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.mcruben.cloudnetinterface.command.CommandCloudNet;
import de.mcruben.cloudnetinterface.inventory.InventoryManager;
import de.mcruben.cloudnetinterface.inventory.def.ProxiesInventory;
import de.mcruben.cloudnetinterface.inventory.def.ServerGroupsInventory;
import de.mcruben.cloudnetinterface.inventory.def.ServersInventory;
import de.mcruben.cloudnetinterface.listener.GUIListeners;
import lombok.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class CloudNetInterface extends JavaPlugin {

    @Getter
    private static CloudNetInterface instance;

    private Map<String, ServerInfo> servers;
    private Map<String, ProxyInfo> proxies;
    private Map<String, ServerGroup> serverGroups;
    private InventoryManager inventoryManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        if (!this.getServer().getPluginManager().isPluginEnabled("CloudNetAPI")) {
            this.getLogger().severe("§cNo CloudNetAPI was found, disabling the plugin");
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        //Don't get all the servers from the cloud, if we already have them in the SignSelector/MobSelector
        if (SignSelector.getInstance() != null && SignSelector.getInstance().getServers() != null && !SignSelector.getInstance().getServers().isEmpty()) {
            this.servers = SignSelector.getInstance().getServers();
        } else if (MobSelector.getInstance() != null && MobSelector.getInstance().getServers() != null && !MobSelector.getInstance().getServers().isEmpty()) {
            this.servers = MobSelector.getInstance().getServers();
        } else {
            this.servers = CloudAPI.getInstance().getServers().stream().collect(Collectors.toMap(object -> object.getServiceId().getServerId(), object -> object));
        }

        this.proxies = CloudAPI.getInstance().getProxys().stream().collect(Collectors.toMap(object -> object.getServiceId().getServerId(), object -> object));

        CloudAPI.getInstance().getNetworkHandlerProvider().registerHandler(new NetworkHandlerImpl());

        this.inventoryManager = new InventoryManager();

        this.getServer().getPluginManager().registerEvents(new GUIListeners(), this);

        this.getCommand("cloudnet").setExecutor(new CommandCloudNet());

        this.servers.values().forEach(ServersInventory::handleServerAdd);
        this.proxies.values().forEach(ProxiesInventory::handleServerAdd);

        Thread thread = new Thread(() -> {
            this.serverGroups = new HashMap<>();
            CloudAPI.getInstance().getServerGroupMap().values().forEach(simpleServerGroup -> {
                ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(simpleServerGroup.getName());
                this.serverGroups.put(serverGroup.getName(), serverGroup);
                ServerGroupsInventory.handleServerAdd(serverGroup);
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public int getOnlineServers(String group) {
        int i = 0;
        for (ServerInfo value : this.servers.values()) {
            if (value.getServiceId().getGroup().equalsIgnoreCase(group)) {
                i++;
            }
        }
        return i;
    }

    public Collection<ServerInfo> getServers(String group) {
        return this.servers.values().stream().filter(serverInfo -> serverInfo.getServiceId().getGroup().equalsIgnoreCase(group)).collect(Collectors.toList());
    }
}
