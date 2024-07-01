package net.bandithemepark.bandiproxy.backend

import com.google.gson.JsonObject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PostLoginEvent
import com.velocitypowered.api.event.player.ServerConnectedEvent
import com.velocitypowered.api.proxy.Player
import net.bandithemepark.bandiproxy.util.BackendUtil
import java.util.Date

class BackendEvents {
    @Subscribe
    fun onConnect(event: PostLoginEvent) {
        BackendUtil.getPlayer(event.player.uniqueId) { data ->
            if(data == null) {
                BackendUtil.addPlayer(event.player.uniqueId, event.player.username) {
                    updateLastJoined(event.player)
                }
            } else {
                updateLastJoined(event.player)
            }
        }
    }

    @Subscribe
    fun onServerSwitch(event: ServerConnectedEvent) {
        val json = JsonObject()
        json.addProperty("playername", event.player.username)
        json.addProperty("onServer", event.server.serverInfo.name)
        BackendUtil.updatePlayer(event.player, json) {}
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        val json = JsonObject()
        json.addProperty("onServer", "offline")
        BackendUtil.updatePlayer(event.player, json) {}
    }

    private fun updateLastJoined(player: Player) {
        val json = JsonObject()
        json.addProperty("lastJoined", Date().toString())
        BackendUtil.updatePlayer(player, json) {}
    }
}