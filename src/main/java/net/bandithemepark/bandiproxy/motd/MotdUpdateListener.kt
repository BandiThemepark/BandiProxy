package net.bandithemepark.bandiproxy.motd

import net.bandithemepark.bandiproxy.BandiProxy
import net.bandithemepark.bandiproxy.backend.BackendSetting
import net.bandithemepark.bandiproxy.backend.MQTTListener
import java.util.concurrent.TimeUnit

class MotdUpdateListener: MQTTListener("/proxy/mode/trigger") {
    override fun onMessage(message: String) {
        BandiProxy.instance.server.scheduler.buildTask(BandiProxy.instance, Runnable {
            BackendSetting("motd").getValue { BandiProxy.instance.motd = it }
        }).delay(500, TimeUnit.MILLISECONDS).schedule()
    }
}