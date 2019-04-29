package de.mcruben.cloudnetinterface;
/*
 * Created by Mc_Ruben on 28.11.2018
 */

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.handlers.adapter.NetworkHandlerAdapter;
import de.dytanic.cloudnet.lib.CloudNetwork;
import de.dytanic.cloudnet.lib.server.ServerGroup;
import de.dytanic.cloudnet.lib.server.SimpleServerGroup;
import de.dytanic.cloudnet.lib.server.info.ProxyInfo;
import de.dytanic.cloudnet.lib.server.info.ServerInfo;
import de.mcruben.cloudnetinterface.inventory.def.ProxiesInventory;
import de.mcruben.cloudnetinterface.inventory.def.ServerGroupsInventory;
import de.mcruben.cloudnetinterface.inventory.def.ServersInventory;

import java.util.Map;

public class NetworkHandlerImpl extends NetworkHandlerAdapter {
    @Override
    public void onServerAdd(ServerInfo serverInfo) {
        ServersInventory.handleServerAdd(serverInfo);
        CloudNetInterface.getInstance().getServers().put(serverInfo.getServiceId().getServerId(), serverInfo);
    }

    @Override
    public void onServerInfoUpdate(ServerInfo serverInfo) {
        ServersInventory.handleServerUpdate(serverInfo);
        CloudNetInterface.getInstance().getServers().put(serverInfo.getServiceId().getServerId(), serverInfo);
    }

    @Override
    public void onServerRemove(ServerInfo serverInfo) {
        ServersInventory.handleServerRemove(serverInfo);
        CloudNetInterface.getInstance().getServers().remove(serverInfo.getServiceId().getServerId());
    }

    @Override
    public void onProxyAdd(ProxyInfo proxyInfo) {
        ProxiesInventory.handleServerAdd(proxyInfo);
        CloudNetInterface.getInstance().getProxies().put(proxyInfo.getServiceId().getServerId(), proxyInfo);
    }

    @Override
    public void onProxyInfoUpdate(ProxyInfo proxyInfo) {
        ProxiesInventory.handleServerUpdate(proxyInfo);
        CloudNetInterface.getInstance().getProxies().put(proxyInfo.getServiceId().getServerId(), proxyInfo);
    }

    @Override
    public void onProxyRemove(ProxyInfo proxyInfo) {
        ProxiesInventory.handleServerRemove(proxyInfo);
        CloudNetInterface.getInstance().getProxies().remove(proxyInfo.getServiceId().getServerId());
    }

    @Override
    public void onCloudNetworkUpdate(CloudNetwork cloudNetwork) {
        Map<String, SimpleServerGroup> serverGroupMap = cloudNetwork.getServerGroups();
        CloudNetInterface.getInstance().getServerGroups().forEach((s, serverGroup) -> {
            if (!serverGroupMap.containsKey(s)) {
                CloudNetInterface.getInstance().getServerGroups().remove(s);
                ServerGroupsInventory.handleServerRemove(serverGroup);
            }
        });
        serverGroupMap.forEach((s, simpleServerGroup) -> {
            if (!CloudNetInterface.getInstance().getServerGroups().containsKey(s)) {
                ServerGroup serverGroup = CloudAPI.getInstance().getServerGroup(s);
                CloudNetInterface.getInstance().getServerGroups().put(s, serverGroup);
                ServerGroupsInventory.handleServerAdd(serverGroup);
            }
        });
    }
}
