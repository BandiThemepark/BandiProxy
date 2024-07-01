package net.bandithemepark.bandiproxy.settings

import com.google.gson.JsonObject
import net.bandithemepark.bandiproxy.util.FileUtil

class ProxySettings {
    init {
        loadSettings()
    }

    var displayedMaxPlayers = 0
    lateinit var emergencyMOTD: String
    lateinit var apiKey: String
    lateinit var mqttAddress: String

    fun loadSettings() {
        if(!FileUtil.doesFileExist("config.json")) {
            FileUtil.saveToFile(
                JsonObject(),
                "config.json"
            )
        }

        val json = FileUtil.loadJsonFrom("config.json")
        displayedMaxPlayers = if(json.has("displayedMaxPlayers")) json.get("displayedMaxPlayers").asInt else 0
        emergencyMOTD = if(json.has("emergencyMOTD")) json.get("emergencyMOTD").asString else ""
        apiKey = if(json.has("apiKey")) json.get("apiKey").asString else ""
        mqttAddress = if(json.has("mqttAddress")) json.get("mqttAddress").asString else ""
    }
}