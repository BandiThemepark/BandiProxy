package net.bandithemepark.bandiproxy.network

import com.google.common.io.ByteStreams
import com.google.gson.JsonParser
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import net.bandithemepark.bandiproxy.BandiProxy
import java.util.*

class BanEvents {
    @Subscribe
    fun onPlayerConnect(event: PlayerChooseInitialServerEvent) {
        if(BandiProxy.instance.banManager.isBanned(event.player.uniqueId)) {
            BandiProxy.instance.banManager.kickWithBanMessage(event.player)
        }
    }

    @Subscribe
    fun onMessageReceive(event: PluginMessageEvent) {
        if(event.identifier != BandiProxy.BAN_CHANNEL_IDENTIFIER) return

        val message = ByteStreams.newDataInput(event.dataAsInputStream()).readUTF()
        val json = JsonParser.parseString(message)

        when(json.asJsonObject.get("action").asString) {
            "ban" -> {
                BandiProxy.instance.banManager.banPlayer(UUID.fromString(json.asJsonObject.get("uuid").asString), json.asJsonObject.get("reason").asString)
            }
            "unban" -> {
                BandiProxy.instance.banManager.unbanPlayer(UUID.fromString(json.asJsonObject.get("uuid").asString))
            }
            "kick" -> {
                BandiProxy.instance.banManager.kick(UUID.fromString(json.asJsonObject.get("uuid").asString), json.asJsonObject.get("reason").asString)
            }
        }
    }
}