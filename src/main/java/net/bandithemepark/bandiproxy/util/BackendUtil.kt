package net.bandithemepark.bandiproxy.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.velocitypowered.api.proxy.Player
import net.bandithemepark.bandiproxy.BandiProxy
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.*

object BackendUtil {
    fun addPlayer(uuid: UUID, name: String, callback: () -> Unit) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/players/")
            .method("POST", "{\"uuid\":\"$uuid\",\"playername\":\"$name\"}".toRequestBody(mediaType))
            .header("Authorization", BandiProxy.instance.settings.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                callback()
            }
        })
    }

    fun getPlayer(uuid: UUID, callback: (data: JsonObject?) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/players/${uuid}")
            .method("GET", null)
            .header("Authorization", BandiProxy.instance.settings.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject

                val returnData = if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    responseJson.getAsJsonObject("data")
                } else {
                    null
                }

                callback(returnData)
            }
        })
    }

    fun updatePlayer(player: Player, data: JsonObject, callback: () -> Unit) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/players/${player.uniqueId}")
            .method("PUT", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiProxy.instance.settings.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                callback()
            }
        })
    }
}