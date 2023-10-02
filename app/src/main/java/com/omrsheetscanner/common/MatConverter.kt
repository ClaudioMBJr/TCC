package com.omrsheetscanner.common

import android.content.ContentValues.TAG
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.opencv.core.Mat


object MatConverter {

    fun matToJson(mat: Mat): String? {
        val obj = JsonObject()
        if (mat.isContinuous) {
            val cols = mat.cols()
            val rows = mat.rows()
            val elemSize = mat.elemSize().toInt()
            val data = ByteArray(cols * rows * elemSize)
            mat[0, 0, data]
            obj.addProperty("rows", mat.rows())
            obj.addProperty("cols", mat.cols())
            obj.addProperty("type", mat.type())

            // We cannot set binary data to a json object, so:
            // Encoding data byte array to Base64.
            val dataString = String(Base64.encode(data, Base64.DEFAULT))
            obj.addProperty("data", dataString)
            val gson = Gson()
            return gson.toJson(obj)
        } else {
            Log.e(TAG, "Mat not continuous.")
        }
        return "{}"
    }

    fun matFromJson(json: String?): Mat {
        val parser = JsonParser()
        val JsonObject = parser.parse(json).asJsonObject
        val rows = JsonObject["rows"].asInt
        val cols = JsonObject["cols"].asInt
        val type = JsonObject["type"].asInt
        val dataString = JsonObject["data"].asString
        val data: ByteArray = Base64.decode(dataString.toByteArray(), Base64.DEFAULT)
        val mat = Mat(rows, cols, type)
        mat.put(0, 0, data)
        return mat
    }
}