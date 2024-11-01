package net.bandithemepark.bandiproxy

import com.google.inject.Inject
import com.velocitypowered.api.event.PostOrder
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Plugin
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import com.velocitypowered.api.proxy.server.PingOptions
import com.velocitypowered.api.proxy.server.RegisteredServer
import net.bandithemepark.bandiproxy.backend.BackendEvents
import net.bandithemepark.bandiproxy.backend.BackendSetting
import net.bandithemepark.bandiproxy.backend.MQTTConnector
import net.bandithemepark.bandiproxy.motd.MotdUpdateListener
import net.bandithemepark.bandiproxy.motd.ServerListEvents
import net.bandithemepark.bandiproxy.network.BanEvents
import net.bandithemepark.bandiproxy.network.BanManager
import net.bandithemepark.bandiproxy.network.TransferEvents
import net.bandithemepark.bandiproxy.settings.ProxySettings
import net.kyori.adventure.key.Key
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.file.Path
import java.time.Duration

@Plugin(id = "bandiproxy", name = "BandiProxy", version = "1.0.0")
class BandiProxy @Inject constructor(
    val server: ProxyServer,
    val logger: Logger,
    @DataDirectory val dataDirectory: Path
) {
    companion object {
        lateinit var instance: BandiProxy
        val PING_CHANNEL_IDENTIFIER: MinecraftChannelIdentifier = MinecraftChannelIdentifier.from("bandicorequeue:ping")
        val TRANSFER_CHANNEL_IDENTIFIER: MinecraftChannelIdentifier = MinecraftChannelIdentifier.from("bandi:transfer")
        val BAN_CHANNEL_IDENTIFIER: MinecraftChannelIdentifier = MinecraftChannelIdentifier.from("bandicore:ban")
    }

    init {
        logger.info("BandiProxy has been constructed")
    }

    lateinit var settings: ProxySettings
    lateinit var mqttConnector: MQTTConnector
    lateinit var banManager: BanManager
    var motd: String? = null
    @Subscribe(order = PostOrder.FIRST)
    fun onInit(event: ProxyInitializeEvent) {
        instance = this

        // Load settings
        settings = ProxySettings()

        // Set up ban manager and load active bans
        banManager = BanManager()
        banManager.loadBannedPlayers()

        // Register messaging channels
        server.channelRegistrar.register(PING_CHANNEL_IDENTIFIER)
        server.channelRegistrar.register(TRANSFER_CHANNEL_IDENTIFIER)
        server.channelRegistrar.register(BAN_CHANNEL_IDENTIFIER)

        // Register events
        server.eventManager.register(this, BackendEvents())
        server.eventManager.register(this, ServerListEvents(logger))
        server.eventManager.register(this, TransferEvents())
        server.eventManager.register(this, BanEvents())

        // Setup mqtt
        MotdUpdateListener().register()
        mqttConnector = MQTTConnector()

        // Load MOTD from back-end
        BackendSetting("motd").getValue { motd = it }

        logger.info("BandiProxy has been initialized")
    }
}