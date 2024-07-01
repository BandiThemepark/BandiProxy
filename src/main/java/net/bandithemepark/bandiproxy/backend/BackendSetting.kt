package net.bandithemepark.bandiproxy.backend

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.bandithemepark.bandiproxy.BandiProxy
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class BackendSetting(val name: String) {
    /**
     * Creates the setting if it does not already exist
     * @param defaultValue The default value of the setting, which will be set
     */
    fun createIfNotExistElseSet(defaultValue: String) {
        getId { id ->
            if(id == null) {
                create(defaultValue)
            } else {
                setValue(defaultValue)
            }
        }
    }

    private fun create(defaultValue: String) {
        val data = JsonObject()
        data.addProperty("name", name)
        data.addProperty("value", defaultValue)

        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/settings/")
            .method("POST", data.toString().toRequestBody(mediaType))
            .header("Authorization", BandiProxy.instance.settings.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    BandiProxy.instance.logger.info("Created a new setting called $name with value $defaultValue because it did not exist yet.")
                } else {
                    BandiProxy.instance.logger.warn("Failed to create a new setting called $name with value $defaultValue.")
                }
            }
        })
    }

    /**
     * Gets the value of the setting
     * @param callback Whatever should happen when the answer is received. The value of the setting will be passed to the callback
     */
    fun getValue(callback: (String) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/settings/find/$name")
            .method("GET", null)
            .header("Authorization", BandiProxy.instance.settings.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    val value = responseJson.getAsJsonObject("data").get("value").asString
                    callback.invoke(value)
                } else {
                    BandiProxy.instance.logger.warn("An attempt was made at retrieving data of setting $name, but no data was found. The following message was given: ${responseJson.get("message")}")
                }
            }
        })
    }

    private fun getId(callback: (String?) -> Unit) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.bandithemepark.net/settings/find/$name")
            .method("GET", null)
            .header("Authorization", BandiProxy.instance.settings.apiKey)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject


                if(responseJson.has("data") && !responseJson.get("data").isJsonNull) {
                    callback.invoke(responseJson.getAsJsonObject("data").get("id").asString)
                } else {
                    callback.invoke(null)
                }
            }
        })
    }

    /**
     * Sets the value of the setting
     * @value Value to set it to
     */
    fun setValue(value: String) {
        getId { id ->
            val data = JsonObject()
            data.addProperty("value", value)

            val client = OkHttpClient()
            val mediaType = "application/json".toMediaTypeOrNull()

            val request = Request.Builder()
                .url("https://api.bandithemepark.net/settings/$id")
                .method("PUT", data.toString().toRequestBody(mediaType))
                .header("Authorization", BandiProxy.instance.settings.apiKey)
                .build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseJson = JsonParser.parseString(response.body!!.string()).asJsonObject
                    if(!responseJson.has("data") || responseJson.get("data").isJsonNull) {
                        BandiProxy.instance.logger.warn("An attempt was made at setting value of setting $name, but no return data was found. The following message was given: ${responseJson.get("message")}")
                    }
                }
            })
        }
    }
}