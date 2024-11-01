package net.bandithemepark.bandiproxy.network

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import net.bandithemepark.bandiproxy.BandiProxy
import net.kyori.adventure.text.minimessage.MiniMessage

class BanEvents {
    @Subscribe
    fun onPlayerConnect(event: PlayerChooseInitialServerEvent) {
        if(BandiProxy.instance.banManager.isBanned(event.player.uniqueId)) {
            event.player.disconnect(MiniMessage.miniMessage().deserialize("<#aaa9a8>You have been banned from visiting BandiThemepark.<br>The following reason was given: <br><br><#b82727>"
            + BandiProxy.instance.banManager.getBanReason(event.player.uniqueId) + "<br><br><#aaa9a8>If you believe this ban was issued in error,<br>please contact our crew via our Discord at<br>discord.bandithemepark.net"))
        }
    }
}