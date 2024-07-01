package net.bandithemepark.bandiproxy.network

import com.google.common.io.ByteStreams
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PluginMessageEvent
import net.bandithemepark.bandiproxy.BandiProxy

class TransferEvents {
    @Subscribe
    fun onMessageReceive(event: PluginMessageEvent) {
        if (event.identifier != BandiProxy.TRANSFER_CHANNEL_IDENTIFIER) return
        BandiProxy.instance.logger.info("Received transfer request")

        val input = ByteStreams.newDataInput(event.dataAsInputStream())
        val serverName = input.readUTF()
        val playerName = input.readUTF()

        val server = BandiProxy.instance.server.getServer(serverName).get()
        val player = BandiProxy.instance.server.getPlayer(playerName).get()

        player.createConnectionRequest(server).fireAndForget()
    }
}