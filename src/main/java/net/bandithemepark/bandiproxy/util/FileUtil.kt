package net.bandithemepark.bandiproxy.util

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileReader

object FileUtil {
    fun loadJsonFrom(path: String): JsonObject {
        val reader = FileReader(path)
        return JsonParser.parseReader(reader).asJsonObject
    }

    fun saveToFile(json: JsonObject, fileName: String) {
        val builder = GsonBuilder().create()
        val stringJson = builder.toJson(json)

        val file = File(fileName)
        if(file.parent != null) createDirectoryIfNotExists(file.parent)
        if(!file.exists()) file.createNewFile()
        file.writeText(stringJson)
    }

    fun createDirectoryIfNotExists(directoryPath: String) {
        if(!directoryPath.contains("/")) return
        val directory = File(directoryPath)
        if(!directory.exists()) directory.mkdirs()
    }

    fun doesFileExist(path: String): Boolean {
        val file = File(path)
        return file.exists()
    }
}