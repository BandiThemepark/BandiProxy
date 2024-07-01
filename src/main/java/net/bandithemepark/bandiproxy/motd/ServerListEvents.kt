package net.bandithemepark.bandiproxy.motd

import com.google.common.io.ByteStreams
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.RegisteredServer
import net.bandithemepark.bandiproxy.BandiProxy
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.ParsingException
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class ServerListEvents(val logger: Logger) {
    @Subscribe
    fun onServerListPing(event: ProxyPingEvent) {
        val description = if(BandiProxy.instance.motd != null) {
            try {
                MiniMessage.miniMessage().deserialize(BandiProxy.instance.motd!!)
            } catch(e: ParsingException) {
                logger.warn("Failed to parse MOTD from back-end. It might include legacy color codes. Using emergency MOTD instead.")
                MiniMessage.miniMessage().deserialize(BandiProxy.instance.settings.emergencyMOTD)
            }
        } else {
            MiniMessage.miniMessage().deserialize(BandiProxy.instance.settings.emergencyMOTD)
        }

        val result = event.ping.asBuilder().maximumPlayers(BandiProxy.instance.settings.displayedMaxPlayers).description(description).build()
        event.ping = result
    }

    @Subscribe
    fun onMessageReceive(event: PluginMessageEvent) {
        if(event.identifier != BandiProxy.PING_CHANNEL_IDENTIFIER) return

        val message = ByteStreams.newDataInput(event.dataAsInputStream()).readUTF()

        if (message == "QUEUE_PING_TO_PARK") {
            val server = BandiProxy.instance.server.getServer("bandithemepark").get()
            val queueServer = BandiProxy.instance.server.getServer("queue")!!.get()

            server.ping().handleAsync { it, _ ->
                if (it == null) {
                    sendMessage(queueServer,  "PARK_OFFLINE")
                } else {
                    sendMessage(queueServer,  "PARK_ONLINE")
                }
            }
        }
    }

    fun sendMessage(server: RegisteredServer, message: String) {
        val stream = ByteArrayOutputStream()
        val out = DataOutputStream(stream)
        out.writeUTF(message)
        server.sendPluginMessage(BandiProxy.PING_CHANNEL_IDENTIFIER, stream.toByteArray())
    }
}