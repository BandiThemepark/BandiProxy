package net.bandithemepark.bandiproxy.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.bandithemepark.bandiproxy.util.FileUtil
import java.util.*

class BanManager {
    private val activeBans = mutableListOf<Ban>()

    /**
     * Checks if a player is banned by their UUID
     * @param uuid The UUID of the player to check
     * @return True if the player is banned, false otherwise
     */
    fun isBanned(uuid: UUID): Boolean {
        return activeBans.any { it.uuid == uuid }
    }

    /**
     * Gets the reason a player was banned by their UUID
     * @param uuid The UUID of the player to get the ban reason for
     * @return The reason the player was banned, or null if the player is not banned
     */
    fun getBanReason(uuid: UUID): String? {
        return activeBans.find { it.uuid == uuid }?.reason
    }

    /**
     * Bans a player by their UUID
     * @param uuid The UUID of the player to ban
     * @param reason The reason for the ban (optional)
     */
    fun banPlayer(uuid: UUID, reason: String? = null) {
        activeBans.add(Ban(uuid, reason ?: "No reason provided"))
        saveBannedPlayers()
    }

    /**
     * Unbans a player by their UUID
     * @param uuid The UUID of the player to unban
     */
    fun unbanPlayer(uuid: UUID) {
        activeBans.removeIf { it.uuid == uuid }
        saveBannedPlayers()
    }

    /**
     * Saves all the banned player UUIDs in memory to the bans.json file
     */
    private fun saveBannedPlayers() {
        val json = JsonObject()
        val bannedPlayersJson = JsonArray()

        activeBans.forEach {
            bannedPlayersJson.add(it.toJson())
        }

        json.add("activeBans", bannedPlayersJson)

        FileUtil.saveToFile(
            json,
            "bans.json"
        )
    }

    /**
     * Loads all the banned player UUIDs from the bans.json file into memory
     */
    fun loadBannedPlayers() {
        if(!FileUtil.doesFileExist("bans.json")) {
            saveDefaultConfig()
            return
        }

        val json = FileUtil.loadJsonFrom("bans.json")
        val bannedPlayersJson = json.getAsJsonArray("activeBans")

        activeBans.clear()
        bannedPlayersJson.forEach {
            activeBans.add(Ban.fromJson(it.asJsonObject))
        }
    }

    /**
     * Saves the default configuration to the bans.json file
     */
    private fun saveDefaultConfig() {
        val defaultJson = JsonObject()
        defaultJson.add("activeBans", JsonArray())

        FileUtil.saveToFile(
            defaultJson,
            "bans.json"
        )

        activeBans.clear()
    }

    data class Ban(val uuid: UUID, val reason: String) {
        fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("uuid", uuid.toString())
            json.addProperty("reason", reason)
            return json
        }

        companion object {
            fun fromJson(json: JsonObject): Ban {
                return Ban(UUID.fromString(json.get("uuid").asString), json.get("reason").asString)
            }
        }
    }
}