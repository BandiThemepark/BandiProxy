package net.bandithemepark.bandiproxy.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.velocitypowered.api.proxy.Player
import net.bandithemepark.bandiproxy.BandiProxy
import net.bandithemepark.bandiproxy.util.FileUtil
import net.kyori.adventure.text.minimessage.MiniMessage
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
    private fun getBanReason(uuid: UUID): String? {
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

        val player = BandiProxy.instance.server.getPlayer(uuid).orElse(null) ?: return
        kickWithBanMessage(player)
    }

    /**
     * Kicks a player with a ban message
     * @param player The player to kick
     */
    fun kickWithBanMessage(player: Player) {
        player.disconnect(
            MiniMessage.miniMessage().deserialize("<#aaa9a8>You have been banned from visiting BandiThemepark.<br>The following reason was given: <br><br><#b82727>"
                + getBanReason(player.uniqueId) + "<br><br><#aaa9a8>If you believe this ban was issued in error,<br>please contact our crew via our Discord at<br>discord.bandithemepark.net"))

    }

    /**
     * Kick a player with a custom reason
     * @param uuid The UUID of the player to kick
     * @param reason The reason for the kick
     */
    fun kick(uuid: UUID, reason: String) {
        val player = BandiProxy.instance.server.getPlayer(uuid).orElse(null) ?: return
        player.disconnect(MiniMessage.miniMessage().deserialize(
            "<#aaa9a8>You have been kicked visiting BandiThemepark.<br>The following reason was given: <br><br><#b82727>" +
            reason +
            "<br><br><#aaa9a8>If you believe this kick was issued in error,<br>please contact our crew via our Discord at<br>discord.bandithemepark.net"
        ))
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